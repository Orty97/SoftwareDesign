package front.rendering.fonts;

public class GlyphGeometry {
	public final boolean[] onCurve;
	public final int[] pointData;
	public final int[] contourData;
	
	public GlyphGeometry(int[] point_data, int[] contour_data, boolean[] on_curve) {
		pointData = point_data;
		contourData = contour_data;
		onCurve = on_curve;
	}
}
