package mods.battlegear2.heraldry;

public enum HelaldyArmourPositions {
	
	SINGLE(1, new float[]{0}, new float[]{0}, new float[]{1}, new float[]{1}, new boolean[]{false}),
	SINGLE_FLIP(1, new float[]{1}, new float[]{0}, new float[]{0}, new float[]{1}, new boolean[]{false}),
	DOUBLE_HORIZ(2, new float[]{1, -1}, new float[]{-0.5F, -0.5F}, new float[]{-1, 1}, new float[]{1.5F, 1.5F}, new boolean[]{false, false}),
	DOUBLE_HORIZ_FLIP_COLOURS(2, new float[]{1, -1}, new float[]{-0.5F, -0.5F}, new float[]{-1, 1}, new float[]{1.5F, 1.5F}, new boolean[]{false, true}),
	DOUBLE_VERT(2, new float[]{-0.5F, -0.5F}, new float[]{0, -1}, new float[]{1.5F, 1.5F}, new float[]{2, 1}, new boolean[]{false, false}),
	DOUBLE_VERT_FLIP_COLOURS(2, new float[]{-0.5F, -0.5F}, new float[]{0, -1}, new float[]{1.5F, 1.5F}, new float[]{2, 1}, new boolean[]{false, true}),
	FOUR(4, new float[]{1, -1, 1, -1},
			new float[]{0, 0, -1F, -1F},
			new float[]{-1, 1, -1, 1},
			new float[]{2, 2, 1, 1},
			new boolean[] {false, false, false, false}
	),
	FOUR_FLIP_COLOURS(4, new float[]{1, -1, 1, -1},
			new float[]{0, 0, -1F, -1F},
			new float[]{-1, 1, -1, 1},
			new float[]{2, 2, 1, 1},
			new boolean[] {false, true, true, false}
	);
	
	private int passess;
	private float[] sourceX;
	private float[] sourceY;
	private float[] sourceXEnd;
	private float[] sourceYEnd;
	private boolean[] altColours;
	
	private HelaldyArmourPositions(int passess, float[] sourceX, float[] sourceY,
			float[] sourceXEnd, float[] sourceYEnd, boolean[] altColours) {
		this.passess = passess;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.sourceXEnd = sourceXEnd;
		this.sourceYEnd = sourceYEnd;
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
	
	public float getXEnd(int pass){
		return sourceXEnd[pass];
	}
	
	public float getYEnd(int pass){
		return sourceYEnd[pass];
	}
	
	public boolean getAltColours(int pass){
		return altColours[pass];
	}
}
