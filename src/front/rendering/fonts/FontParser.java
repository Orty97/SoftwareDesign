package front.rendering.fonts;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import front.rendering.fonts.meta_data.FontMetaData;
import front.rendering.fonts.meta_data.FontTableMetaData;

public class FontParser {

	public static final Font_Old parseFontFile(String font_path) {
		try {
			DataInputStream fontFileStream = new DataInputStream(new FileInputStream(new File(font_path)));
			FontMetaData fontMetaData = new FontMetaData(fontFileStream);
			HashMap<String,FontTableMetaData> fontTableMetaDatas = new HashMap<>();
						
			if(fontMetaData.scalerType != 0x00010000) {
				fontFileStream.close();
				throw new IllegalArgumentException("Unsupported font format!");
			}
			
			for(int table_md = 0; table_md < fontMetaData.numTables; table_md ++) {
				FontTableMetaData fontTableMetaData = new FontTableMetaData(fontFileStream);
				fontTableMetaDatas.put(fontTableMetaData.tag,fontTableMetaData);
			}
			
			
			
			fontFileStream.close();
		}catch(Exception e) {e.printStackTrace();}
		
		
		
		
		return null;
	}
	
}
