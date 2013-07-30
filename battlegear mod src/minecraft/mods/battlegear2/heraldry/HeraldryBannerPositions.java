package mods.battlegear2.heraldry;

public enum HeraldryBannerPositions {
	
	
	SINGLE(1, new float[]{-0.4F}, new float[]{.4F}, new float[]{1.4F}, new float[]{0.6F},  new boolean[]{false}, new boolean[]{false}),
	SINGLE_FLIP(1, new float[]{-0.4F}, new float[]{.4F}, new float[]{1.4F}, new float[]{0.6F},  new boolean[]{true}, new boolean[]{false}),
	
	DOUBLE_HORIZ(2, new float[]{-0.45F, 0.05F}, new float[]{0.05F, 0.45F}, new float[]{1.2F, 1.2F}, new float[]{0.8F, 0.8F},  new boolean[]{true, false}, new boolean[]{false, false}),
	DOUBLE_HORIZ_FLIP(2, new float[]{-0.45F, 0.05F}, new float[]{0.05F, 0.45F}, new float[]{1.2F, 1.2F}, new float[]{0.8F, 0.8F},  new boolean[]{true, false}, new boolean[]{false, true}),
	
	DOUBLE_VERT(2, new float[]{-0.25F, -0.25F}, new float[]{0.25F, 0.25F}, new float[]{1.55F, .95F}, new float[]{1.05F, 0.45F},  new boolean[]{false, false}, new boolean[]{false, false}),
	DOUBLE_VERT_FLIP(2, new float[]{-0.25F, -0.25F}, new float[]{0.25F, 0.25F}, new float[]{1.55F, .95F}, new float[]{1.05F, 0.45F},  new boolean[]{false, false}, new boolean[]{false, true}),
	
	FOUR(4,
			new float[]{-0.45F, 0.05F, -0.45F, 0.05F},
			new float[]{-0.05F, 0.45F,  -0.05F, 0.45F},
			new float[]{1.45F, 1.45F, .95F, .95F},
			new float[]{1.05F, 1.05F, .55F, .55F},
			new boolean[] {true, false, true, false},
			new boolean[] {false, false, false, false}),
			
	FOUR_FLIP(4,
			new float[]{-0.45F, 0.05F, -0.45F, 0.05F},
			new float[]{-0.05F, 0.45F,  -0.05F, 0.45F},
			new float[]{1.45F, 1.45F, .95F, .95F},
			new float[]{1.05F, 1.05F, .55F, .55F},
			new boolean[] {true, false, true, false},
			new boolean[] {false, true, true, false})
			
	;
	
	private int passess;
	private float[] sourceXStart;
	private float[] sourceXEnd;
	private float[] sourceYStart;
	private float[] sourceYEnd;
	private boolean[] patternFlip;
	private boolean[] altColours;
	
	private HeraldryBannerPositions(int passess, float[] sourceXStart,
			float[] sourceXEnd, float[] sourceYStart, float[] sourceYEnd,
			boolean[] patternFlip, boolean[] altColours) {
		this.passess = passess;
		this.sourceXStart = sourceXStart;
		this.sourceXEnd = sourceXEnd;
		this.sourceYStart = sourceYStart;
		this.sourceYEnd = sourceYEnd;
		this.patternFlip = patternFlip;
		this.altColours = altColours;
	}

	public int getPassess() {
		return passess;
	}

	public float[] getSourceXStart() {
		return sourceXStart;
	}

	public float[] getSourceXEnd() {
		return sourceXEnd;
	}

	public float[] getSourceYStart() {
		return sourceYStart;
	}

	public float[] getSourceYEnd() {
		return sourceYEnd;
	}

	public boolean[] getPatternFlip() {
		return patternFlip;
	}

	public boolean[] getAltColours() {
		return altColours;
	}
	
	
	
}
