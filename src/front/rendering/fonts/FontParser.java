	package front.rendering.fonts;
	
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix3f;

import front.rendering.fonts.meta_data.FontMetaData;
import front.rendering.fonts.meta_data.FontTableMetaData;
import front.rendering.fonts.meta_data.FontTableTags;
import front.rendering.fonts.tables.CMAP_Table;
import front.rendering.fonts.tables.CMAP_Table.Encoding;
import front.rendering.fonts.tables.HEAD_Table;
import front.rendering.fonts.tables.LOCA_Table;
import front.rendering.fonts.tables.MAXP_Table;
import front.rendering.fonts.tables.encodings.FORMAT_12_ENCODING_Table;
import front.rendering.fonts.tables.encodings.FORMAT_4_ENCODING_Table;
	
public class FontParser {
	
	private static final HashMap<Integer,GlyphInstanceRange> glyphInstanceRanges = new HashMap<>();
	private static final ArrayList<GlyphInstance> finalGlyphInstances = new ArrayList<>();
	
	private static final class GlyphInstance{
		public Matrix3f transform;
		public GlyphGeometry geometry;
		
		public GlyphInstance(Matrix3f transform,GlyphGeometry geometry) {
			this.geometry = geometry;
			this.transform = transform;
		}
	}
	private static final class GlyphInstanceRange{
		public int startIndex;
		public int length;
		public GlyphInstanceRange(int start_index,int length) {
			startIndex = start_index;
			this.length = length;
		}
	}
	
	private static final HashMap<Integer,UnresolvedCompoundGlyph> unresolvedCompounds = new HashMap<>();
	private static final class UnresolvedCompoundGlyph{
		public int[] childrenIds;
		Matrix3f[] transforms;
		int[] pMatchPairs;
		
		public UnresolvedCompoundGlyph(int[] children_ids,Matrix3f[] transforms,int[]p_match_pairs) {
			childrenIds = children_ids;
			this.transforms = transforms;
			pMatchPairs = p_match_pairs;
		}
	}
	
	public static final void parseFontFile(String font_path) {
			try {
				RandomAccessFile fontFile = new RandomAccessFile(font_path,"r");
				FontMetaData fontMetaData = new FontMetaData(fontFile);
				
				HashMap<String,FontTableMetaData> fontTableMetaDatas = new HashMap<>();
				HashMap<Integer,Integer> unicodeGlyphIdMap = new HashMap<Integer,Integer>();
										
				if(fontMetaData.scalerType != 0x00010000) {
					fontFile.close();
					throw new IllegalArgumentException("Unsupported font format!");
				}
				
				for(int table_md = 0; table_md < fontMetaData.numTables; table_md ++) {
					FontTableMetaData fontTableMetaData = new FontTableMetaData(fontFile);
					fontTableMetaDatas.put(fontTableMetaData.tag,fontTableMetaData);
				}
				
				HEAD_Table headTable = new HEAD_Table(fontFile,fontTableMetaDatas.get(FontTableTags.head.toString()).offset);
				MAXP_Table maxpTable = new MAXP_Table(fontFile,fontTableMetaDatas.get(FontTableTags.maxp.toString()).offset);
				CMAP_Table cmapTable = new CMAP_Table(fontFile,fontTableMetaDatas.get(FontTableTags.cmap.toString()).offset);
						
				Encoding targetEncoding = selectBestEncoding(cmapTable);
				
				if(targetEncoding == null) {
					fontFile.close();
					throw new IllegalArgumentException("Unsupported font encoding!");
				}
				
				switch(targetEncoding.format) {
					case 4:
						FORMAT_4_ENCODING_Table formatFourEncodingTable = new FORMAT_4_ENCODING_Table(fontFile, targetEncoding.offset);
						long segStart = targetEncoding.offset + 14; 
						 for (int seg = 0; seg < formatFourEncodingTable.segCount; seg++) {
						        for (int unicode = formatFourEncodingTable.startCode[seg]; unicode <= formatFourEncodingTable.endCode[seg]; unicode++) {
						            int glyphId;
						            if (formatFourEncodingTable.idRangeOffset[seg] == 0) {
						                glyphId = (unicode + formatFourEncodingTable.idDelta[seg]) & 0xFFFF;
						            } else {
						                long idRangeOffsetAddress = segStart + (formatFourEncodingTable.segCount * 2L) + 2L + (formatFourEncodingTable.segCount * 2L) + (formatFourEncodingTable.segCount * 2L) + (seg * 2L);
						                long glyphIndexAddress = idRangeOffsetAddress + formatFourEncodingTable.idRangeOffset[seg] + 2L * (unicode - formatFourEncodingTable.startCode[seg]);

						                fontFile.seek(glyphIndexAddress);

						                int glyphIndex = fontFile.readUnsignedShort();

						                glyphId = (glyphIndex + formatFourEncodingTable.idDelta[seg]) & 0xFFFF;
						            }

						            if (glyphId != 0xFFFF) {
						                unicodeGlyphIdMap.put(unicode, glyphId);
						            }
						        }
						    }
						    break;
					case 12:
						FORMAT_12_ENCODING_Table formatTwelveEncodingTable = new FORMAT_12_ENCODING_Table(fontFile, targetEncoding.offset);
						for(int group = 0; group < formatTwelveEncodingTable.nGroups; group++) {
							for(int unicode = (int) formatTwelveEncodingTable.startCharCode[group]; unicode <= (int) formatTwelveEncodingTable.endCharCode[group]; unicode ++) {
								unicodeGlyphIdMap.put(unicode,(int) (formatTwelveEncodingTable.startGlyphId[group] + unicode - formatTwelveEncodingTable.startCharCode[group]));
							}
						}
						break;
					default:
						fontFile.close();
						throw new IllegalArgumentException("Unsupported font encoding!");
				}
				
				LOCA_Table locaTable = new LOCA_Table(fontFile,fontTableMetaDatas.get(FontTableTags.loca.toString()).offset,
													  unicodeGlyphIdMap,headTable.indexToLocFormat == 0);
							
				byte[] glyfRecord = new byte[(int) locaTable.maxGlyfRecordLength];
																		
				for(int unicode : unicodeGlyphIdMap.keySet()) {
					int glyphId = unicodeGlyphIdMap.get(unicode);
						
					long glyphGeometryStartOffset = locaTable.getGlyphStartPoint(glyphId);
					long glyphGeometryPointsLength = locaTable.getGlyphPointsLength(glyphId);
					
					if(glyphGeometryPointsLength == 0)
						continue;
					
					fontFile.seek(fontTableMetaDatas.get(FontTableTags.glyf.toString()).offset + glyphGeometryStartOffset);		
					
					fontFile.readFully(glyfRecord);
					ByteBuffer glyfRecordBuffer = ByteBuffer.wrap(glyfRecord,0,(int)glyphGeometryPointsLength);
					glyfRecordBuffer.order(ByteOrder.BIG_ENDIAN).position(0);		
					
					short numberOfContours = glyfRecordBuffer.getShort();
					
					if(numberOfContours == 0)
						continue;
					
					short xMin = glyfRecordBuffer.getShort();
					short yMin = glyfRecordBuffer.getShort();
					short xMax = glyfRecordBuffer.getShort();
					short yMax = glyfRecordBuffer.getShort();
										
					//Compount glyph
					if(numberOfContours < 0) {
						ArrayList<Integer> pMatchPairsArray = new ArrayList<Integer>();
						ArrayList<Matrix3f> transforms = new ArrayList<>();
						ArrayList<Integer> children = new ArrayList<>();
						
						boolean hasInstructions;
						boolean moreComponents;
						do {
						    int flags = glyfRecordBuffer.getShort() & 0xFFFF;
						    int glyphIndex = glyfRecordBuffer.getShort() & 0xFFFF;
						    
						    int arg1, arg2;

						    if ((flags & CompoundGlyfFlagMasks.ARG_1_AND_2_ARE_WORDS) != 0) {
						        arg1 = glyfRecordBuffer.getShort();
						        arg2 = glyfRecordBuffer.getShort();
						    } else {
						        arg1 = glyfRecordBuffer.get();
						        arg2 = glyfRecordBuffer.get();
						    }

						    Matrix3f transform = new Matrix3f();
						    
						    if ((flags & CompoundGlyfFlagMasks.ARGS_ARE_XY_VALUES) != 0) {
						        transform.m20(arg1);
						        transform.m21(arg2);
						        pMatchPairsArray.add(-1);
						        pMatchPairsArray.add(-1);
						    } else {
						    	pMatchPairsArray.add(arg1);
						    	pMatchPairsArray.add(arg2);
						    }

						    if ((flags & CompoundGlyfFlagMasks.WE_HAVE_A_SCALE) != 0) {
						        transform.m00 = glyfRecordBuffer.getShort() / 16384.0f;
						        transform.m11 = transform.m00;
						    }
						    else if ((flags & CompoundGlyfFlagMasks.WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
						    	transform.m00 = glyfRecordBuffer.getShort() / 16384.0f;
						    	transform.m11 = glyfRecordBuffer.getShort() / 16384.0f;
						    }
						    else if ((flags & CompoundGlyfFlagMasks.WE_HAVE_A_TWO_BY_TWO) != 0) {
						    	transform.m00 = glyfRecordBuffer.getShort() / 16384.0f;
						    	transform.m01 = glyfRecordBuffer.getShort() / 16384.0f;
						    	transform.m10 = glyfRecordBuffer.getShort() / 16384.0f;
						    	transform.m11 = glyfRecordBuffer.getShort() / 16384.0f;
						    }
						    transforms.add(transform);
						    children.add(glyphIndex);						    
						    hasInstructions = (flags & CompoundGlyfFlagMasks.WE_HAVE_INSTRUCTIONS)!=0;    
						    moreComponents = (flags & CompoundGlyfFlagMasks.MORE_COMPONENTS) != 0;
						} while (moreComponents);
						
						if (hasInstructions) {
						    int instructionLength = glyfRecordBuffer.getShort() & 0xFFFF;
						    glyfRecordBuffer.position(glyfRecordBuffer.position() + instructionLength);
						}
						
						unresolvedCompounds.put(glyphId,new UnresolvedCompoundGlyph(children.stream().mapToInt(Integer::intValue).toArray(),
																					transforms.toArray(new Matrix3f[0]),
																					pMatchPairsArray.stream().mapToInt(Integer::intValue).toArray()));
					//Simple glyph
					}else {
						int[] endPointsOfContours = new int[numberOfContours];
						for(int i =0; i < numberOfContours; i++)
							endPointsOfContours[i] = glyfRecordBuffer.getShort() & 0xFFFF; 
						
						int instructionLength = glyfRecordBuffer.getShort() & 0xFFFF;
						short[] instructions = new short[instructionLength];
						for(int i =0; i < instructionLength; i++)
							instructions[i] = (short) (glyfRecordBuffer.get() & 0xFF);
						
						int pointsCount = endPointsOfContours[numberOfContours-1] +1;
						
						int[] flags = new int[pointsCount];
						
						int flagCount = 0;
						int repeatTargetCount;
						
						while(flagCount < pointsCount) {
							int flag = glyfRecordBuffer.get() & 0xFF;
							flags[flagCount++] = flag;
							if((flag & SimpleGlyfFlagMasks.REPEAT) != 0) {
								repeatTargetCount = glyfRecordBuffer.get() & 0xFF;
								for(int i = 0; i < repeatTargetCount; i++)
									flags[flagCount++] = flag;
							}
						}
						
						boolean[] onCurvePoint = new boolean[pointsCount];
						int[] xDelta = new int[pointsCount];
						int[] yDelta = new int[pointsCount];
						
						for(int f = 0; f < pointsCount; f++) {
							if((flags[f] & SimpleGlyfFlagMasks.ON_OFF_CURVE_BIT) != 0)
								onCurvePoint[f] = true;
							else
								onCurvePoint[f] = false;
							
							if((flags[f] & SimpleGlyfFlagMasks.X_SHORT_DELTA) != 0) {
								if((flags[f] & SimpleGlyfFlagMasks.X_SHORT_DELTA_SIGN_OR_READ_DELTA)!=0) {
									xDelta[f] = glyfRecordBuffer.get() & 0xFF;
								}else{
									xDelta[f] = -(glyfRecordBuffer.get() & 0xFF);
								}
							}else {
								if((flags[f] & SimpleGlyfFlagMasks.X_SHORT_DELTA_SIGN_OR_READ_DELTA)!=0) {
									xDelta[f] = 0;
								}else{
									xDelta[f] = glyfRecordBuffer.getShort();
								}
							}
						}
						
						for(int f = 0; f < pointsCount; f++) {
							if((flags[f] & SimpleGlyfFlagMasks.Y_SHORT_DELTA) != 0) {
								if((flags[f] & SimpleGlyfFlagMasks.Y_SHORT_DELTA_SIGN_OR_READ_DELTA)!=0) {
									yDelta[f] = glyfRecordBuffer.get() & 0xFF;
								}else{
									yDelta[f] = -(glyfRecordBuffer.get() & 0xFF);
								}
							}else {
								if((flags[f] & SimpleGlyfFlagMasks.Y_SHORT_DELTA_SIGN_OR_READ_DELTA)!=0) {
									yDelta[f] = 0;
								}else{
									yDelta[f] = glyfRecordBuffer.getShort();
								}
							}
						}					

						int[] pointDataGlyph = new int[2*pointsCount];
						
						int x = 0;
						int y = 0;

						for (int i = 0; i < pointsCount; i++) {
						    x += xDelta[i];
						    y += yDelta[i];

						    pointDataGlyph[i * 2] = x;
						    pointDataGlyph[i * 2 + 1] = y;
						}
						
						int[] contourInfo = new int[numberOfContours * 2];

						for (int i = 0; i < numberOfContours; i++) {
						    int end = endPointsOfContours[i];

						    int start = (i == 0)
						        ? 0
						        : endPointsOfContours[i - 1] + 1;

						    contourInfo[i*2] = start;
						    contourInfo[i*2+1] = end - start + 1;
						}
						int instanceRangeStart = finalGlyphInstances.size();
						finalGlyphInstances.add(new GlyphInstance(new Matrix3f(),new GlyphGeometry(pointDataGlyph,contourInfo)));
						glyphInstanceRanges.put(glyphId,new GlyphInstanceRange(instanceRangeStart,1));					
					}
				}			
				solveCompoundGlyphs();				
				fontFile.close();
			}catch(Exception e) {e.printStackTrace();}
		}	
	
	private static final void solveCompoundGlyphs() {
		
	}
	
	private static final Encoding selectBestEncoding(CMAP_Table cmap_table) {
			Encoding target = null;
			int bestScore = 0;
			
			for(Encoding encoding : cmap_table.encodings) {
				int encodingScore = getEncodingScore(encoding);
	
				if(encodingScore > bestScore) {
					bestScore = encodingScore;
					target = encoding;
				}
			}
			return target;
		}
		
	private static int getEncodingScore(Encoding e) {
			if(e.platformID == 3 && e.encodingID == 10) {
				if(e.format == 12)
					return 9;
				else
					return 3;
			}
			
			if(e.platformID == 0) {
				if(e.format == 12)
					return 6;
				else
					return 2;
			}
			
			if(e.platformID == 3 && e.encodingID == 0) {
				if(e.format == 12)
					return 3;
				else
					return 1;
			}
			
			return 0;
		}
}
