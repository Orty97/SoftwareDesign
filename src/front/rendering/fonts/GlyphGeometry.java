package front.rendering.fonts;

public class GlyphGeometry {
	public final int[] pointData;
	public final int[] contourData;
	
	public GlyphGeometry(int[] point_data, int[] contour_data) {
		pointData = point_data;
		contourData = contour_data;
	}
}
