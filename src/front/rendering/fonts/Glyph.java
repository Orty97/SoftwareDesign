package front.rendering.fonts;

import org.joml.Vector2i;

public class Glyph {

	private final boolean composite;
	
	private final Vector2i min;
	private final Vector2i max;
		
	private final boolean[] onCurve;
	private final int[] x;
	private final int[] y;
	private final int[] contourEnds;
	
	public Glyph(boolean composite, int min_x, int max_x, 
									int min_y, int max_y,
				 boolean[] on_curve, int[] x, int[] y,
				 int[] contour_ends) {
		
		this.composite = composite;
		min = new Vector2i(min_x,min_y);
		max = new Vector2i(max_x,max_y);
		
		onCurve = on_curve;
		this.x = x;
		this.y = y;
		contourEnds = contour_ends;
	}
	
	public boolean isComposite() {
		return composite;
	}	
}
