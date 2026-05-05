package front.rendering.fonts;

public class GlyphKernPair {

	public final int firstGlyphId;
	public final int secondGlyphId;
	
	public final short xPlacementG1;
	public final short yPlacementG1;
	
	public final short xAdjustmentG1;
	public final short yAdjustmentG1;
	
	public final short xPlacementG2;
	public final short yPlacementG2;
	
	public final short xAdjustmentG2;
	public final short yAdjustmentG2;
	
	public GlyphKernPair(int first_glyph_id,
						 int second_glyph_id,
						 short x_placement_g1,
						 short y_placement_g1,
						 short x_adjustment_g1,
						 short y_adjustment_g1,
						 short x_placement_g2,
						 short y_placement_g2,
						 short x_adjustment_g2,
						 short y_adjustment_g2) {
		firstGlyphId = first_glyph_id;
		secondGlyphId = second_glyph_id;
		xPlacementG1 = x_placement_g1;
		yPlacementG1 = y_placement_g1;
		xAdjustmentG1 = x_adjustment_g1;
		yAdjustmentG1 = y_adjustment_g1;
		xPlacementG2 = x_placement_g2;
		yPlacementG2 = y_placement_g2;
		xAdjustmentG2 = x_adjustment_g2;
		yAdjustmentG2 = y_adjustment_g2;
	}
}
