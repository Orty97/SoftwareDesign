	package front.rendering.fonts.tables;
	
	import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import front.rendering.fonts.GlyphKernPair;
import front.rendering.fonts.PosPairValueFormatFlags;
	
	public class GPOS_Table {
	
		public final int majorVersion;
		public final int minorVersion;
		
		public final int ScriptListOffset;
		public final int FeatureListOffset;
		public final int LookupListOffset;
		
		public final HashMap<Long,GlyphKernPair> encodedKernPairs = new HashMap<>();
		
		public GPOS_Table(RandomAccessFile font_file, long offset) throws IOException {
			font_file.seek(offset);
			
			majorVersion = font_file.readUnsignedShort();
			minorVersion = font_file.readUnsignedShort();
			
			ScriptListOffset = font_file.readUnsignedShort();
			FeatureListOffset = font_file.readUnsignedShort();
			LookupListOffset = font_file.readUnsignedShort();
			
			font_file.seek(offset+LookupListOffset);
			
			int lookupCount = font_file.readUnsignedShort();
			int[] lookupOffsets = new int[lookupCount];
			
			for(int i = 0; i < lookupCount; i++ )
				lookupOffsets[i] = font_file.readUnsignedShort();
	
			for(int i = 0; i < lookupCount; i++ ) {
				font_file.seek(offset + LookupListOffset + lookupOffsets[i]);
				
				int lookupType = font_file.readUnsignedShort();
				/*int lookupFlags = */font_file.readUnsignedShort();
				int lookupSubtableCount = font_file.readUnsignedShort();
				int[] subTableOffsets = new int[lookupSubtableCount];
				
				if(lookupType == 2 || lookupType == 9) {
					switch(lookupType) {
						case 2:break;
						case 9:
							for(int j =0; j < lookupSubtableCount; j++)
								subTableOffsets[j] = font_file.readUnsignedShort();
							
							for(int j = 0; j < lookupSubtableCount; j++) {
								font_file.seek(offset + LookupListOffset + lookupOffsets[i]+subTableOffsets[j]);
								if(font_file.readUnsignedShort() != 1)
									continue;								
								if(font_file.readUnsignedShort() != 2)
									continue;								
								long ExtensionOffset = Integer.toUnsignedLong(font_file.readInt());
								font_file.seek(offset + LookupListOffset + lookupOffsets[i]+subTableOffsets[j]+ExtensionOffset);
								if(font_file.readUnsignedShort() != 1)
									continue;
								
								int coverageOffset = font_file.readUnsignedShort();
								
								int valueFormat1 = font_file.readUnsignedShort();
								int valueFormat2 = font_file.readUnsignedShort();
								int pairSetCount = font_file.readUnsignedShort();
								int[] pairSetOffset = new int[pairSetCount];
								
								for(int ps = 0; ps < pairSetCount; ps ++)
									pairSetOffset[ps] = font_file.readUnsignedShort();
								
								font_file.seek(offset + LookupListOffset + lookupOffsets[i]+subTableOffsets[j]+ExtensionOffset+coverageOffset);
								
								int coverageFormat = font_file.readUnsignedShort();
								int glyphCount = font_file.readUnsignedShort();
								
								ArrayList<Integer> coverageData = new ArrayList<>();
								int glyphCoverageArrayLength = 0;
								int[] glyphCoverageArray;
								
								if(coverageFormat == 1) {
									glyphCoverageArray = new int[glyphCount];
									for(int g = 0; g < glyphCount; g++)
										glyphCoverageArray[g] = font_file.readUnsignedShort();
								}else {
									for(int r = 0; r < glyphCount; r++) {
										int startGlyphId = font_file.readUnsignedShort();
										int endGlyphId = font_file.readUnsignedShort();
										int startCoverageIndex = font_file.readUnsignedShort();
										
										if(startCoverageIndex + (endGlyphId - startGlyphId + 1) > glyphCoverageArrayLength)
											glyphCoverageArrayLength = startCoverageIndex + (endGlyphId - startGlyphId + 1);
										
										coverageData .add(startGlyphId);
										coverageData .add(endGlyphId);
										coverageData .add(startCoverageIndex);
									}
									glyphCoverageArray = new int[glyphCoverageArrayLength];
									//TODO Don't forget range coverage when more time is available
								}
								
								for(int g1 = 0; g1 < glyphCoverageArray.length; g1 ++) {
									font_file.seek(offset + LookupListOffset + lookupOffsets[i]+subTableOffsets[j]+ExtensionOffset+pairSetOffset[g1]);
									int pairCount = font_file.readUnsignedShort();
									for(int pair = 0; pair < pairCount; pair++) {
										int secondGlyphId = font_file.readUnsignedShort();
										
										short xPlacementG1=0;
										short yPlacementG1=0;
										
										short xAdjustmentG1=0;
										short yAdjustmentG1=0;
										
										short xPlacementG2=0;
										short yPlacementG2=0;
										
										short xAdjustmentG2=0;
										short yAdjustmentG2=0;
										
										if((valueFormat1 & PosPairValueFormatFlags.X_PLACEMENT)!=0)
											xPlacementG1 = font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.Y_PLACEMENT)!=0)
											yPlacementG1 = font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.X_ADJUSTMENT)!=0)
											xAdjustmentG1 = font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.Y_ADJUSTMENT)!=0)
											yAdjustmentG1 = font_file.readShort();
										
										//unused stuff -> just consuming the data
										if((valueFormat1 & PosPairValueFormatFlags.X_PLA_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.Y_PLA_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.X_ADV_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat1 & PosPairValueFormatFlags.Y_ADV_DEVICE_OFFSET)!=0)
											font_file.readShort();
										
										if((valueFormat2 & PosPairValueFormatFlags.X_PLACEMENT)!=0)
											xPlacementG2 = font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.Y_PLACEMENT)!=0)
											yPlacementG2 = font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.X_ADJUSTMENT)!=0)
											xAdjustmentG2 = font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.Y_ADJUSTMENT)!=0)
											yAdjustmentG2 = font_file.readShort();
										
										//unused stuff -> just consuming the data
										if((valueFormat2 & PosPairValueFormatFlags.X_PLA_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.Y_PLA_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.X_ADV_DEVICE_OFFSET)!=0)
											font_file.readShort();
										if((valueFormat2 & PosPairValueFormatFlags.Y_ADV_DEVICE_OFFSET)!=0)
											font_file.readShort();
										
										long pairKey = ((long)glyphCoverageArray[g1] << 32) | (secondGlyphId & 0xFFFFFFFFL);
										if(encodedKernPairs.containsKey(pairKey))
										encodedKernPairs.put(pairKey,new GlyphKernPair(glyphCoverageArray[g1],
																					   secondGlyphId,
																					   xPlacementG1,
																					   yPlacementG1,
																					   xAdjustmentG1,
																					   yAdjustmentG1,
																					   xPlacementG2,
																					   yPlacementG2,
																					   xAdjustmentG2,
																					   yAdjustmentG2
																					   ));
										
									}
								}								
							}							
							break;
					}
				}else continue;
			}
		
		}	
	}
