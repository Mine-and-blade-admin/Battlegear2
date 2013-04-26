package mods.battlegear2.client.heraldry;

public enum HeraldryPositions {
	
	SINGLE(1, new float[]{.15F}, new float[]{.15F}, .7F, new boolean[]{false}, new boolean[]{false}),
	SINGLE_FLIP(1, new float[]{.15F}, new float[]{.15F}, .7F, new boolean[]{true}, new boolean[]{false}),
	
	DOUBLE_HORIZ(2, new float[]{.8F, .8F}, new float[]{0F, 0F}, 1F, new boolean[]{false, true}, new boolean[]{false, false}),
	DOUBLE_HORIZ_FLIP_COLOURS(2, new float[]{.8F, .8F}, new float[]{0F, 0F}, 1F, new boolean[]{false, true}, new boolean[]{true, false}),
	
	DOUBLE_VERT(2, new float[]{.0F, .0F}, new float[]{.2F, -.2F}, 1F, new boolean[]{false, false}, new boolean[]{false, false}),
	DOUBLE_VERT_FLIP_COLOURS(2, new float[]{.0F, .0F}, new float[]{.2F, -.2F}, 1F, new boolean[]{false, false}, new boolean[]{false, true}),
	
	FOUR(4, new float[]{.8F, .8F, .8F, .8F},
			new float[]{.2F, .2F, -.2F, -.2F},
			1F, new boolean[] {false, true, false, true},
			new boolean[] {false, false, false, false}
	),
	
	FOUR_FLIP_COLOURS(4, new float[]{.8F, .8F, .8F, .8F},
			new float[]{.2F, .2F, -.2F, -.2F},
			1F, new boolean[] {false, true, false, true},
			new boolean[] {false, true, true, false}
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
