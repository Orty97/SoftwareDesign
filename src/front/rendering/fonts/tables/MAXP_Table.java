package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MAXP_Table {

	public long version;
	
	public int numGlyphs;
	
	public int maxPoints;
	public int maxContours;
	
	public int maxCompositePoints;
	public int maxCompositeContours;
	
	public MAXP_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		version = font_file.readLong();
		
		numGlyphs = font_file.readUnsignedShort();
		
		maxPoints = font_file.readUnsignedShort();
		maxContours = font_file.readUnsignedShort();
		
		maxCompositePoints = font_file.readUnsignedShort();
		maxCompositeContours = font_file.readUnsignedShort();
	}
	
}
