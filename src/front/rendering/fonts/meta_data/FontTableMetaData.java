package front.rendering.fonts.meta_data;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FontTableMetaData {
	public String tag;
	public long checksum;
	public long offset;
	public long length;	
	
	public FontTableMetaData(DataInputStream font_file_stream) throws IOException {
		byte[] tagChars = new byte[4];
		font_file_stream.readFully(tagChars);
		tag = new String(tagChars,StandardCharsets.US_ASCII);
		System.out.println(tag);
		font_file_stream.readInt();
		font_file_stream.readInt();
		font_file_stream.readInt();
	}
}
