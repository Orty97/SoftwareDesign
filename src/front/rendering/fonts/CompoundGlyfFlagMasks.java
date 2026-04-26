package front.rendering.fonts;

public class CompoundGlyfFlagMasks {
	static final int ARG_1_AND_2_ARE_WORDS = 0x01;
	static final int ARGS_ARE_XY_VALUES    = 0x02;
	static final int ROUND_XY_TO_GRID      = 0x04;
	static final int WE_HAVE_A_SCALE       = 0x08;
	static final int MORE_COMPONENTS       = 0x10;
	static final int WE_HAVE_AN_X_AND_Y_SCALE = 0x20;
	static final int WE_HAVE_A_TWO_BY_TWO  = 0x40;
	static final int WE_HAVE_INSTRUCTIONS  = 0x80;
	static final int USE_MY_METRICS        = 0x100;
	static final int OVERLAP_COMPOUND      = 0x200;
	static final int SCALED_COMPONENT_OFFSET = 0x400;
	static final int UNSCALED_COMPONENT_OFFSET = 0x800;
}
