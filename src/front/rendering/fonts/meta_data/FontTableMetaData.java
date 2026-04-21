package front.rendering.fonts.meta_data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FontTableMetaData {
	public String tag;
	public long checksum;
	public long offset;
	public long length;	
	
	public FontTableMetaData(RandomAccessFile font_file) throws IOException {
		byte[] tagChars = new byte[4];
		font_file.readFully(tagChars);
		tag = new String(tagChars,StandardCharsets.US_ASCII);
		checksum = Integer.toUnsignedLong(font_file.readInt());
		offset = Integer.toUnsignedLong(font_file.readInt());
		length = Integer.toUnsignedLong(font_file.readInt());
	}
}
