package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HEAD_Table {
	public int version;
	public int revision;
	
	public int checkSumAdjustment;
	public int magicNumber;
	private final int correctMagicNumber = 0x5F0F3CF5;
	
	public int flags;
	public int unitsPerEm;
	
	public long created;
	public long modified;
	
	public short xMin;
	public short yMin;
	public short xMax;
	public short yMax;
	
	public int macStyle;
	public int lowestRecPPEM;
	public int fontDirectionHint;
	public int indexToLocFormat;
	public int glyphDataFormat;
	
	public HEAD_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		version = font_file.readInt();
		revision = font_file.readInt();
		
		checkSumAdjustment = font_file.readInt();
		magicNumber = font_file.readInt();
		if(magicNumber != correctMagicNumber)
			throw new IllegalStateException("Head table magic number missmatch.");
		
		flags = font_file.readUnsignedShort();
		unitsPerEm = font_file.readUnsignedShort();
		
		created = font_file.readLong();
		modified = font_file.readLong();
		
		xMin = font_file.readShort();
		yMin = font_file.readShort();
		xMax = font_file.readShort();
		yMax = font_file.readShort();
		
		macStyle = font_file.readUnsignedShort();
		lowestRecPPEM = font_file.readUnsignedShort();
		fontDirectionHint = font_file.readShort();
		indexToLocFormat = font_file.readShort();
		glyphDataFormat = font_file.readShort();
	}
}
