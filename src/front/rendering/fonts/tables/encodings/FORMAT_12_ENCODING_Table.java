package front.rendering.fonts.tables.encodings;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FORMAT_12_ENCODING_Table {

	public int format;
	public int reserved;
	
	public long length;
	public long language;
	public long nGroups;
	
	public long[] startCharCode;
	public long[] endCharCode;
	public long[] startGlyphId;
	
	
	public FORMAT_12_ENCODING_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		format = font_file.readUnsignedShort();
		reserved = font_file.readUnsignedShort();
		
		length = Integer.toUnsignedLong(font_file.readInt()); 
		language = Integer.toUnsignedLong(font_file.readInt()); 
		nGroups = Integer.toUnsignedLong(font_file.readInt()); 
		
		startCharCode = new long[(int) nGroups];
		endCharCode = new long[(int) nGroups];
		startGlyphId = new long[(int) nGroups];
		
		for(long g = 0; g < nGroups; g++) {
			startCharCode[(int) g] = Integer.toUnsignedLong(font_file.readInt()); 
			endCharCode[(int) g] = Integer.toUnsignedLong(font_file.readInt()); 
			startGlyphId[(int) g] = Integer.toUnsignedLong(font_file.readInt()); 
		}
	}
	
}
