package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CMAP_Table {

	public class Encoding{
		public int platformID;
		public int encodingID;
		public long offset;
		
		public int format;
		
		public Encoding(RandomAccessFile font_file, long offset) throws IOException {
			platformID = font_file.readUnsignedShort();
			encodingID = font_file.readUnsignedShort();
			
			this.offset = offset + Integer.toUnsignedLong(font_file.readInt());		
		}
		
		protected void setFormat(int format) {
			this.format = format;
		}
	}
	
	public int version;
	public int numTables;
	
	public Encoding[] encodings;
	
	public CMAP_Table(RandomAccessFile font_file, long offset) throws IOException {
		font_file.seek(offset);
		
		version = font_file.readUnsignedShort();
		numTables = font_file.readUnsignedShort();
		
		encodings = new Encoding[numTables];
		
		for(int t = 0; t < numTables; t++)
			encodings[t] = new Encoding(font_file, offset);
		
		for(Encoding encoding : encodings) {
			font_file.seek(encoding.offset);
			encoding.setFormat(font_file.readUnsignedShort());
		}
	}
}
