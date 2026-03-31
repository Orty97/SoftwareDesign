package front.rendering.fonts;

import org.joml.Vector2f;
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
	
	public boolean pointOnCurve(int point_index) {
		return onCurve[point_index];
	}

	public int getPointsCount() {
		return onCurve.length;
	}
	
	public int getPointX(int point_index) {
		return x[point_index];
	}
	
	public int getPointY(int point_index) {
		return y[point_index];
	}
	
	public int getContoursCount() {
		return contourEnds.length;
	}
	
	public int getContourEnd(int contour_index) {
		return contourEnds[contour_index];
	}
	
	public int getContourStart(int contour_index) {
		return contour_index == 0 ? 0 : contourEnds[contour_index - 1] +1;
	}

	public Vector2i getGlyphMin() {
		return min;
	}
	
	public Vector2i getGlyphMax() {
		return max;
	}

	public float getGlyphScale() {
		float glyphWidth = max.x - min.x;
		float glyphHeight = max.y - min.y;
		
		float scaleX = 2.0f / glyphWidth;
		float scaleY = 2.0f / glyphHeight;
		
		return Math.min(scaleX,scaleY);
	}
	
	public Vector2f getGlyphOffset() {
		float xOffset = -(min.x + (max.x - min.x) / 2) * getGlyphScale();
		float yOffset = -(min.y + (max.y - min.x) / 2) * getGlyphScale();
		return new Vector2f(xOffset,yOffset);
	}
}
