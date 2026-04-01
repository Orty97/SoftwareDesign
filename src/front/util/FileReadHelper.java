package front.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileReadHelper {

	public static final short readShort(FileInputStream file_stream) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
		int file_byte;
		int count = 0;
		
		while (count < Short.BYTES) {
		    file_byte = file_stream.read();
		    if (file_byte == -1)
		        throw new EOFException();
		    buffer.put((byte)file_byte);
		    count++;
		}
		return buffer.flip().getShort();
	}
	
	public static final int readUnsignedShort(FileInputStream file_stream) throws IOException {
		int b1 = file_stream.read();
	    int b2 = file_stream.read();
	    if ((b1 | b2) < 0) throw new EOFException();
	    return (b1 << 8) | b2;
	}
	
	public static final int readInt(FileInputStream file_stream) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
		int file_byte;
		int count = 0;
		
		while (count < Integer.BYTES) {
		    file_byte = file_stream.read();
		    if (file_byte == -1)
		        throw new EOFException();
		    buffer.put((byte)file_byte);
		    count++;
		}
		return buffer.flip().getInt();
	}
	
	public static final long readUnsignedInt(FileInputStream stream) throws IOException {
	    long b1 = stream.read();
	    long b2 = stream.read();
	    long b3 = stream.read();
	    long b4 = stream.read();
	    
	    if ((b1 | b2 | b3 | b4) < 0) throw new EOFException();

	    return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
	}

	public static String loadShaderCode(String shader_code_path) {
		StringBuilder sourceBuilder = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(shader_code_path));
			String line;
			
			while((line = reader.readLine())!=null)
				sourceBuilder.append(line).append("\n");
			
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sourceBuilder.toString();
	}
	
}
