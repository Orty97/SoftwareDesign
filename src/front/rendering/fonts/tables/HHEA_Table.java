package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HHEA_Table {

	public final int version;
	public final short ascender;
	public final short descender;
	public final short lineGap;
	public final int advanceWidthMax;
	public final short minLeftSideBearing;
	public final short minRightSideBearing;
	public final short xMaxExtent;
	public final short caretSlopeRise;
	public final short caretSlopeRun;
	public final short caretOffset;
	public final short metricDataFormat;
	public final int numberOfHMetrics;
	
	public HHEA_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		version = font_file.readInt();
		
		ascender = font_file.readShort();
		descender = font_file.readShort();
		
		lineGap = font_file.readShort();
		advanceWidthMax = font_file.readUnsignedShort();
		
		minLeftSideBearing = font_file.readShort();
		minRightSideBearing = font_file.readShort();
		
		xMaxExtent = font_file.readShort();
		caretSlopeRise = font_file.readShort();
		caretSlopeRun  = font_file.readShort();
		caretOffset = font_file.readShort();
		
		font_file.read(new byte[8]);
		metricDataFormat = font_file.readShort();
		numberOfHMetrics = font_file.readUnsignedShort();
	}
	
}
