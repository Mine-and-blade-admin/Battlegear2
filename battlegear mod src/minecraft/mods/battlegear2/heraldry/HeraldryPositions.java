package mods.battlegear2.heraldry;

public enum HeraldryPositions {
	
	SINGLE(1, new float[]{-.2F}, new float[]{-.2F}, 1.4F, new boolean[]{false}, new boolean[]{false}),
	SINGLE_FLIP(1, new float[]{-.2F}, new float[]{-.2F}, 1.4F, new boolean[]{true}, new boolean[]{false}),
	
	DOUBLE_HORIZ(2, new float[]{-1.25F, -1.25F}, new float[]{-0.75F, -0.75F}, 2.5F, new boolean[]{false, true}, new boolean[]{false, false}),
	DOUBLE_HORIZ_FLIP_COLOURS(2, new float[]{-1.25F, -1.25F}, new float[]{-0.75F, -0.75F}, 2.5F, new boolean[]{false, true}, new boolean[]{true, false}),
	
	DOUBLE_VERT(2, new float[]{-0.75F, -0.75F}, new float[]{-.25F, -1.25F}, 2.5F, new boolean[]{false, false}, new boolean[]{false, false}),
	DOUBLE_VERT_FLIP_COLOURS(2, new float[]{-0.75F, -0.75F}, new float[]{-.25F, -1.25F}, 2.5F, new boolean[]{false, false}, new boolean[]{false, true}),
	
	FOUR(4, new float[]{-1.25F, -1.25F, -1.25F, -1.25F},
			new float[]{-.25F, -1.25F, -1.25F, -.25F},
			2.5F, new boolean[] {false, true, false, true},
			new boolean[] {false, false, false, false}
	),
	
	FOUR_FLIP_COLOURS(4, new float[]{-1.25F, -1.25F, -1.25F, -1.25F},
			new float[]{-.25F, -1.25F, -1.25F, -.25F},
			2.5F, new boolean[] {false, true, false, true},
			new boolean[] {true, true, false, false}
	)
	;
	private int passess;
	private float[] sourceX;
	private float[] sourceY;
	private float width;
	private boolean[] patternFlip;
	private boolean[] altColours;
	
	private HeraldryPositions(int passess, float[] sourceX, float[] sourceY,
			float width, boolean[] patternFlip, boolean[] altColours) {
		this.passess = passess;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.width = width;
		this.patternFlip = patternFlip;
		this.altColours = altColours;
	}

	public int getPassess() {
		return passess;
	}
	
	public float getSourceX(int pass){
		return sourceX[pass];
	}
	
	public float getSourceY(int pass){
		return sourceY[pass];
	}
	
	public float getWidth(){
		return width;
	}
	
	public boolean getPatternFlip(int pass){
		return patternFlip[pass];
	}
	
	public boolean getAltColours(int pass){
		return altColours[pass];
	}
}
