package mods.battlegear2.client.utils;

import java.awt.Color;

public class SigilHelper {
	
	public static final int[] colours = new int[] {
		0x191919,
		0xCC4C4C,
		0x667F33,
		0x7F664C,
		0x3366CC, 
		0xB266E5,
		0x4C99B2,
		0x999999,
		0x4C4C4C,
		0xF2B2CC,
		0x7FCC19,
		0xE5E533,
		0x99B2F2,
		0xE57FD8,
		0xF2B233,
		0xFFFFFF
	};
	
	public static float[] convertColourToARGBArray(int value){
		return new float[]{
					(float)(value & 0x000000FF) / 256,
					(float)((value & 0x0000FF00) >> 8) / 256,
					(float) ((value & 0x00FF0000) >> 16) / 256
				};
	}
	
	
	
	

}
