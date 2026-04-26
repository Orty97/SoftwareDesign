package front.rendering.fonts.tables;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class LOCA_Table {

	HashMap<Integer,Long> GlyphGeometryStartPoint = new HashMap<>();
	HashMap<Integer,Long> GlyphGeometryPointsLength = new HashMap<>();
	
	public long maxGlyfRecordLength = 0;
	
	public LOCA_Table(RandomAccessFile font_file, long offset,HashMap<Integer,Integer> unicode_glyf_id_map, boolean short_offset) throws IOException {		
		if(short_offset) {
			for(int unicode : unicode_glyf_id_map.keySet()) {
				font_file.seek(offset + unicode_glyf_id_map.get(unicode)*2);
				int startOffset = font_file.readUnsignedShort();
				int endOffset = font_file.readUnsignedShort();
				
				GlyphGeometryStartPoint.put(unicode_glyf_id_map.get(unicode),startOffset * 2L);
				long length = (endOffset - startOffset)*2L;
				if(length > maxGlyfRecordLength)
					maxGlyfRecordLength = length;
				GlyphGeometryPointsLength.put(unicode_glyf_id_map.get(unicode),(endOffset - startOffset)*2L);

			}			
		}else {
			for(int unicode : unicode_glyf_id_map.keySet()) {
				font_file.seek(offset + unicode_glyf_id_map.get(unicode)*4);
				long startOffset = Integer.toUnsignedLong(font_file.readInt());
				long endOffset = Integer.toUnsignedLong(font_file.readInt());
				
				GlyphGeometryStartPoint.put(unicode_glyf_id_map.get(unicode),startOffset);
				long length = endOffset - startOffset;
				if(length > maxGlyfRecordLength)
					maxGlyfRecordLength = length;
				GlyphGeometryPointsLength.put(unicode_glyf_id_map.get(unicode),endOffset - startOffset);
			}		
		}
	}
	
	public long getGlyphStartPoint(int glyph_id) {
		return GlyphGeometryStartPoint.get(glyph_id);
	}
	
	
	public long getGlyphPointsLength(int glyph_id) {
		return GlyphGeometryPointsLength.get(glyph_id);
	}
	
}
