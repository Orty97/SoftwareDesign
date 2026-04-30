package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Set;

public class LOCA_Table {

	HashMap<Integer,Long> GlyphGeometryStartPoint = new HashMap<>();
	HashMap<Integer,Long> GlyphGeometryPointsLength = new HashMap<>();
	
	public long maxGlyfRecordLength = 0;
	
	public LOCA_Table(RandomAccessFile font_file, long table_offset,int glyph_count, boolean short_offset) throws IOException {		
		
		font_file.seek(table_offset);
		byte[] locaTableBuffer;
		int entrySize = short_offset?2:4;
		
		long[] offsets = new long[glyph_count + 1];
		
		locaTableBuffer = new byte[(glyph_count+1)*entrySize];
		font_file.readFully(locaTableBuffer);
		ByteBuffer locaTableWrapper =  ByteBuffer.wrap(locaTableBuffer);
		locaTableWrapper.order(ByteOrder.BIG_ENDIAN);
		
		for(int i = 0; i < glyph_count + 1; i++)
			if(short_offset) {
				int offset = locaTableWrapper.getShort() & 0xFFFF;
			    offsets[i] = offset * 2L;
			}else {
			    long offset = locaTableWrapper.getInt() & 0xFFFFFFFFL;
			    offsets[i] = offset;
			}
		
		for(int i = 0; i < glyph_count; i++) {
			GlyphGeometryStartPoint.put(i,offsets[i]);
			long length = offsets[i+1]-offsets[i];
			if(length > maxGlyfRecordLength)
				maxGlyfRecordLength = length;
			GlyphGeometryPointsLength.put(i,length);
		}
	}
	
	public Set<Integer> getGlyphIdSet() {
		return GlyphGeometryStartPoint.keySet();
	}
	
	public long getGlyphStartPoint(int glyph_id) {
		return GlyphGeometryStartPoint.get(glyph_id);
	}
	
	
	public long getGlyphPointsLength(int glyph_id) {
		return GlyphGeometryPointsLength.get(glyph_id);
	}
	
}
