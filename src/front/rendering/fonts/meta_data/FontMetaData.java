package front.rendering.fonts.meta_data;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FontMetaData {

	public int scalerType;
	public int numTables;
	
	/*Legacy binary search optimization artifact fields*/
	
	public int searchRange;
	public int entrySelector;
	public int rangeShift;
	
	public FontMetaData(RandomAccessFile font_file) throws IOException {
		scalerType = font_file.readInt();
		numTables = font_file.readUnsignedShort();
		
		searchRange = font_file.readUnsignedShort();
		entrySelector = font_file.readUnsignedShort();
		rangeShift = font_file.readUnsignedShort();
	}
	
}
