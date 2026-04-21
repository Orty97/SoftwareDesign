package front.rendering.fonts.meta_data;

import java.io.DataInputStream;
import java.io.IOException;

public class FontMetaData {

	public int scalerType;
	public int numTables;
	
	/*Legacy binary search optimization artifact fields*/
	
	public int searchRange;
	public int entrySelector;
	public int rangeShift;
	
	public FontMetaData(DataInputStream font_file_stream) throws IOException {
		scalerType = font_file_stream.readInt();
		numTables = font_file_stream.readUnsignedShort();
		
		searchRange = font_file_stream.readUnsignedShort();
		entrySelector = font_file_stream.readUnsignedShort();
		rangeShift = font_file_stream.readUnsignedShort();
	}
	
}
