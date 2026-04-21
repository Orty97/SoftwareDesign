package front.rendering.fonts;

import java.io.RandomAccessFile;
import java.util.HashMap;

import front.rendering.fonts.meta_data.FontMetaData;
import front.rendering.fonts.meta_data.FontTableMetaData;
import front.rendering.fonts.meta_data.FontTableTags;
import front.rendering.fonts.tables.CMAP_Table;
import front.rendering.fonts.tables.CMAP_Table.Encoding;
import front.rendering.fonts.tables.HEAD_Table;
import front.rendering.fonts.tables.MAXP_Table;
import front.rendering.fonts.tables.encodings.FORMAT_12_ENCODING_Table;
import front.rendering.fonts.tables.encodings.FORMAT_4_ENCODING_Table;

public class FontParser {

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
					
					for(int seg = 0; seg < formatFourEncodingTable.segCount; seg ++) {
						for(int unicode = formatFourEncodingTable.startCode[seg]; unicode <= formatFourEncodingTable.endCode[seg]; unicode++) {
							if(formatFourEncodingTable.idRangeOffset[seg] == 0)
								unicodeGlyphIdMap.put(unicode,unicode + formatFourEncodingTable.idDelta[seg]);
							else 
								unicodeGlyphIdMap.put(unicode,formatFourEncodingTable.glyphIdArray[
								                              formatFourEncodingTable.idRangeOffset[seg]/2 + 
								                              unicode - 
								                              formatFourEncodingTable.startCode[seg]]
								                            + formatFourEncodingTable.idDelta[seg]);
						}
					}	
					unicodeGlyphIdMap.forEach((cp, glyph) -> {
					    System.out.println(
					        "U+" + Integer.toHexString(cp) +
					        " '" + new String(Character.toChars(cp)) + "'" +
					        " -> " + glyph
					    );
					});
					
					break;
				case 12:
					FORMAT_12_ENCODING_Table formatTwelveEncodingTable = new FORMAT_12_ENCODING_Table(fontFile, targetEncoding.offset);
					for(int group = 0; group < formatTwelveEncodingTable.nGroups; group++) {
						for(int unicode = (int) formatTwelveEncodingTable.startCharCode[group]; unicode <= (int) formatTwelveEncodingTable.endCharCode[group]; unicode ++) {
							unicodeGlyphIdMap.put(unicode,(int) (formatTwelveEncodingTable.startGlyphId[group] + unicode - formatTwelveEncodingTable.startCharCode[group]));
						}
					}
					
					unicodeGlyphIdMap.forEach((cp, glyph) -> {
					    System.out.println(
					        "U+" + Integer.toHexString(cp) +
					        " '" + new String(Character.toChars(cp)) + "'" +
					        " -> " + glyph
					    );
					});
					break;
				default:
					fontFile.close();
					throw new IllegalArgumentException("Unsupported font encoding!");
			}
			
			fontFile.close();
		}catch(Exception e) {e.printStackTrace();}
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
