package front.rendering.fonts.tables.encodings;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FORMAT_4_ENCODING_Table {
		
	public int format;
	public int length;
	public int language;
	
	public int segCountX2;
	public int segCount;
	
	public int searchRange;
	public int entrySelector;
	public int rangeShift;
	
	public int[] startCode;
	public int[] endCode;
	
	public int[] idDelta;
	public int[] idRangeOffset;
	public int[] glyphIdArray;
	
	public FORMAT_4_ENCODING_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		format = font_file.readUnsignedShort();
		length = font_file.readUnsignedShort();
		language = font_file.readUnsignedShort();
		
		segCountX2 = font_file.readUnsignedShort();
		segCount = (int) (segCountX2/2);
		
		searchRange = font_file.readUnsignedShort();
		entrySelector = font_file.readUnsignedShort();
		rangeShift = font_file.readUnsignedShort();
		
		startCode = new int[segCount];
		endCode = new int[segCount];
		
		idDelta = new int[segCount];
		idRangeOffset = new int[segCount];
		
		for(int s = 0; s < segCount; s++)
			endCode[s] = font_file.readUnsignedShort();
		
		//padding in the font file
		font_file.readUnsignedShort();

		for(int s = 0; s < segCount; s++)
			startCode[s] = font_file.readUnsignedShort();
		for(int s = 0; s < segCount; s++)
			idDelta[s] = font_file.readUnsignedShort();
		for(int s = 0; s < segCount; s++)
			idRangeOffset[s] = font_file.readUnsignedShort();
		
		int remainingShorts = (int) (offset + length - font_file.getChannel().position())/2;
		glyphIdArray = new int[remainingShorts];
		for(int s = 0; s < remainingShorts; s++)
			glyphIdArray[s] = font_file.readUnsignedShort();
	}

}
