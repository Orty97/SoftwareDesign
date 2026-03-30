package front.rendering.fonts;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import front.util.FileReadHelper;

public class Font {
	
	private final HashMap<Integer,Glyph> glyphs = new HashMap<Integer,Glyph>();
	
	
	@SuppressWarnings("unused")
	public Font() {
		//TODO: generalize the hardcoded partition / OS folder
		File font = new File("C:\\Windows\\Fonts\\segoeui.ttf");
		
		HashMap<String,FontTableMetaData> fontTablesMetaData =
				new HashMap<String,FontTableMetaData>();
		HashMap<Integer,Glyph> simpleGlyphs = new HashMap<Integer,Glyph>();		
		Glyph[] glyphs;
		
		short v_major;
		short v_minor;
		int tables;
		int searchRange;
		int entrySelector;
		int rangeShift;
		
		try {
			FileInputStream fontStream = new FileInputStream(font);
				
			v_major = FileReadHelper.readShort(fontStream);
			v_minor = FileReadHelper.readShort(fontStream);
			tables = FileReadHelper.readUnsignedShort(fontStream);
			searchRange = FileReadHelper.readUnsignedShort(fontStream);
			entrySelector = FileReadHelper.readUnsignedShort(fontStream);
			rangeShift = FileReadHelper.readUnsignedShort(fontStream);
			
			FontTableMetaData tableMetaData;
			
			for(int i = 0; i < tables; i++) {
				tableMetaData = new FontTableMetaData();
				byte[] tagBytes = new byte[4];
				int bytesRead = 0;
				while (bytesRead < 4) {
				    int n = fontStream.read(tagBytes, bytesRead, 4 - bytesRead);
				    if (n == -1) throw new EOFException();
				    bytesRead += n;
				}
				tableMetaData.tag = new String(tagBytes, StandardCharsets.US_ASCII);
				tableMetaData.checksum = FileReadHelper.readUnsignedInt(fontStream);
				tableMetaData.offset = FileReadHelper.readUnsignedInt(fontStream);
				tableMetaData.length = FileReadHelper.readUnsignedInt(fontStream);
				
				fontTablesMetaData.put(tableMetaData.tag,tableMetaData);
			}
			
			tableMetaData = fontTablesMetaData.get("head");
			
			fontStream.getChannel().position(tableMetaData.offset + 50);
			int indexToLocFormat = FileReadHelper.readUnsignedShort(fontStream);
			
			tableMetaData = fontTablesMetaData.get("maxp");
			fontStream.getChannel().position(tableMetaData.offset + 4);
			int numGlyphs = FileReadHelper.readUnsignedShort(fontStream);
			
			tableMetaData = fontTablesMetaData.get("loca");
			long[] glyphOffsets = new long[numGlyphs + 1];
			fontStream.getChannel().position(tableMetaData.offset);
			
			for (int i = 0; i <= numGlyphs; i++) {
			    if (indexToLocFormat == 0) {
			        glyphOffsets[i] = FileReadHelper.readUnsignedShort(fontStream) * 2L;
			    } else {
			        glyphOffsets[i] = FileReadHelper.readUnsignedInt(fontStream);
			    }
			}
			
			glyphs = new Glyph[numGlyphs];
			
			tableMetaData = fontTablesMetaData.get("glyf");
			
			for(int i = 0; i < numGlyphs; i++) {
				long glyphStart = glyphOffsets[i];
				long glyphEnd = glyphOffsets[i+1];
				long glyphLen = glyphEnd - glyphStart;
				
				fontStream.getChannel().position(tableMetaData.offset + glyphStart);
				
				short numberOfContours = FileReadHelper.readShort(fontStream);
				
				short minX = FileReadHelper.readShort(fontStream);
				short minY = FileReadHelper.readShort(fontStream);
				short maxX = FileReadHelper.readShort(fontStream);
				short maxY = FileReadHelper.readShort(fontStream);
								
				if(numberOfContours > 0) {
					int[] contourEnds = new int[numberOfContours];
					for(int j = 0; j < numberOfContours; j++)
						contourEnds[j] = FileReadHelper.readUnsignedShort(fontStream);
					
					int pointsCount = contourEnds[numberOfContours-1] +1;					
					int instructionsCount = FileReadHelper.readUnsignedShort(fontStream);
					
					for(int j = 0; j < instructionsCount; j++)
						fontStream.read();
					
					byte[] flags = new byte[pointsCount];
					int flagCount = 0;
					
					boolean[]onCurveFlags = new boolean[pointsCount];
					
					while(flagCount < pointsCount) {
						byte currentFlags = (byte)fontStream.read();
						boolean onCurve = (currentFlags & PointFlags.ON_CURVE ) != 0;
						onCurveFlags[flagCount] = onCurve;
						
						flags[flagCount++] = currentFlags;
						
						if((currentFlags & PointFlags.REPEAT) != 0) {
							int repeatCount = fontStream.read();
							
							for(int r = 0; r < repeatCount; r++) {
								onCurveFlags[flagCount] = onCurve;
								flags[flagCount++] = currentFlags;
							}
						}
					}
					
					int[] x = new int[pointsCount];
					int currentX = 0;
					
					for(int p = 0; p < pointsCount; p++) {
						int dx;
						if((flags[p] & PointFlags.X_SHORT_VECTOR) != 0) {
							if((flags[p] & PointFlags.X_IS_SAME_OR_POSITIVE) != 0)
								dx = fontStream.read();
							else 
								dx = -fontStream.read();
						}else {
							if((flags[p] & PointFlags.X_IS_SAME_OR_POSITIVE) != 0)
								dx = 0;
							else 
								dx = FileReadHelper.readShort(fontStream);
						}
						currentX += dx;
						x[p] = currentX;
					}
					int[] y = new int[pointsCount];
					int currentY = 0;
					
					
					for(int p = 0; p < pointsCount; p++) {
						int dy;
						if((flags[p] & PointFlags.Y_SHORT_VECTOR) != 0) {
							if((flags[p] & PointFlags.Y_IS_SAME_OR_POSITIVE) != 0)
								dy = fontStream.read();
							else 
								dy = -fontStream.read();
						}else {
							if((flags[p] & PointFlags.Y_IS_SAME_OR_POSITIVE) != 0)
								dy = 0;
							else 
								dy = FileReadHelper.readShort(fontStream);
						}
						currentY += dy;
						y[p] = currentY;
					}
					glyphs[i] = new Glyph(false,minX,maxX,minY,maxY,onCurveFlags,x,y,contourEnds);
				}
			}
			
			tableMetaData = fontTablesMetaData.get("cmap");
			fontStream.getChannel().position(tableMetaData.offset);
			
			
			FileReadHelper.readShort(fontStream);
			int numTables = FileReadHelper.readUnsignedShort(fontStream);
			
			boolean encodingFound = false;
			int encodingsChecked = 0;
			
			int platform;
			int encoding;
			long offset = 0;
			
			while(!encodingFound && encodingsChecked < numTables) {
				platform = FileReadHelper.readUnsignedShort(fontStream);
				encoding = FileReadHelper.readUnsignedShort(fontStream);
				offset = FileReadHelper.readUnsignedInt(fontStream);
				
				if(platform == 3 && encoding == 1)
					encodingFound = true;
				else
					encodingsChecked++;
			}	
			
			if(encodingFound = false)
				throw new IOException();
			
			fontStream.getChannel().position(tableMetaData.offset + offset);
			FileReadHelper.readUnsignedShort(fontStream);
			int subtableLen = FileReadHelper.readUnsignedShort(fontStream);
			int language = FileReadHelper.readUnsignedShort(fontStream);
			int segCountX2 = FileReadHelper.readUnsignedShort(fontStream);
			int subtableSearchRange = FileReadHelper.readUnsignedShort(fontStream);
			int subtableEntrySelector = FileReadHelper.readUnsignedShort(fontStream);
			int subtableRangeShift = FileReadHelper.readUnsignedShort(fontStream);
			
			int endCount[] = new int[segCountX2/2];	
			int startCount[] = new int[segCountX2/2];
			int idDelta[] = new int[segCountX2/2];
			int idRangeOffset[] = new int[segCountX2/2];
			int glyphIdArray[];
			
			for(int i = 0; i < segCountX2 / 2; i++) 
				endCount[i] = FileReadHelper.readUnsignedShort(fontStream);
			FileReadHelper.readUnsignedShort(fontStream);
			for(int i = 0; i < segCountX2 / 2; i++)
				startCount[i] = FileReadHelper.readUnsignedShort(fontStream);
			for(int i = 0; i < segCountX2 / 2; i++)
				idDelta[i] = FileReadHelper.readUnsignedShort(fontStream);
			for(int i =0; i < segCountX2 / 2; i++)
				idRangeOffset[i] = FileReadHelper.readUnsignedShort(fontStream);
			
			for (int unicode = 0x20; unicode <= 0x7E; unicode++) {
				
			    int segmentIndex = -1;
			    
			    for (int i = 0; i < endCount.length; i++) {
			        if (unicode >= startCount[i] && unicode <= endCount[i]) {
			            segmentIndex = i;
			            break;
			        }
			    }

			    int glyphIndex = 0; 
			    
			    if (segmentIndex != -1) {
			        if (idRangeOffset[segmentIndex] == 0) {
			            glyphIndex = (unicode + idDelta[segmentIndex]) & 0xFFFF;
			        } else {

			            long posBefore = fontStream.getChannel().position();
			            long offsetToGlyphArray = tableMetaData.offset + offset + 14
			                + (endCount.length * 2) 
			                + 2 
			                + (startCount.length * 2)
			                + (idDelta.length * 2)
			                + (idRangeOffset.length * 2)
			                + (idRangeOffset[segmentIndex]);

			            fontStream.getChannel().position(offsetToGlyphArray + 2 * (unicode - startCount[segmentIndex]));
			            
			            glyphIndex = FileReadHelper.readUnsignedShort(fontStream);
			            if (glyphIndex != 0)
			                glyphIndex = (glyphIndex + idDelta[segmentIndex]) & 0xFFFF;

			            fontStream.getChannel().position(posBefore);
			        }
			    }
			    simpleGlyphs.put(unicode,glyphs[glyphIndex]);
			}			
		} catch (IOException e){e.printStackTrace();}
	}	
	
	public class PointFlags{
		public static final byte ON_CURVE = 0x01;
		public static final byte X_SHORT_VECTOR = 0x02;
		public static final byte Y_SHORT_VECTOR = 0x04;
		public static final byte REPEAT = 0x08;
		public static final byte X_IS_SAME_OR_POSITIVE = 0x10;
		public static final byte Y_IS_SAME_OR_POSITIVE = 0x20;
	}	
	
}
