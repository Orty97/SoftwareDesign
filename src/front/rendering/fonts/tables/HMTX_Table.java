package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HMTX_Table {

	public final int[] advanceWidth;
	public final short[] leftSideBearing;
	
	public HMTX_Table(RandomAccessFile font_file, long offset, int number_of_h_metrics, int glyph_count) throws IOException {
		font_file.seek(offset);
		
		advanceWidth = new int[glyph_count];
		leftSideBearing = new short[glyph_count];
		
		for(int i = 0; i < number_of_h_metrics; i++) {
			advanceWidth[i] = font_file.readUnsignedShort();
			leftSideBearing[i] = font_file.readShort();
		}
				
		for(int i = number_of_h_metrics; i < glyph_count; i++) {
			advanceWidth[i] = advanceWidth[number_of_h_metrics-1];
			leftSideBearing[i] = font_file.readShort();
		}
		
	}
	
}
