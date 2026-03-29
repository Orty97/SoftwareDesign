package front.rendering.fonts;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import front.util.FileReadHelper;

public class Font {
	
	
	
	
	public Font() {
		//TODO: generalize the hardcoded partition / OS folder
		File font = new File("C:\\Windows\\Fonts\\segoeui.ttf");
		
		HashMap<String,FontTableMetaData> fontTablesMetaData =
				new HashMap<String,FontTableMetaData>();
		
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
			
			
			
			
		} catch (IOException e){e.printStackTrace();}
		
		
		
		System.exit(0);
	}	
}
