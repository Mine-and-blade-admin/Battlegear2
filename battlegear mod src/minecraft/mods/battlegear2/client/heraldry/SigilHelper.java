package mods.battlegear2.client.heraldry;

import java.awt.Color;

public class SigilHelper {
	
	public static final int defaultSigil = packSigil(2, 1, 17, 0, 6, 21, 0);
	
	public static final int[] colours_16 = new int[] {
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
	
	public static final int[] colours = new int[] {
		0x191919,
		0x4C4C4C,
		0x808080, //extra grey
		0x999999, //extra grey
		0xFFFFFF,
		0x800000,
		0xFF0000,
		0xFF8080,
		0xFF8000,
		0x808000,
		0xFFFF00,
		0xFFFF80,
		0x008000,
		0x00FF00,
		0x80FF00,
		0x80FF80,
		0x00FF80,
		0x008080,
		0x00FFFF,
		0x80FFFF,
		0x000080,
		0x0000FF,
		0x0080FF,
		0x8080FF,
		0x8000FF,
		0x800080,
		0xFF00FF,
		0xFF80FF,
		0xFF0080,
		0x804000, //extra brown
		0xc08000, //extra brown
		
		//still have room for 1 more colour, maybe transparent?
	};
	
	
	
	
	public static float[] convertColourToARGBArray(int value){
		return new float[]{
					(float)(value & 0x000000FF) / 256,
					(float)((value & 0x0000FF00) >> 8) / 256,
					(float) ((value & 0x00FF0000) >> 16) / 256
				};
	}

	
	/*
	 * Package is as follows (for 32 bit integer)
	 * 
	 * 0-3 = pattern (16)
	 * 4-8 = colour 1 (32)
	 * 9-13 = colour 2 (32)
	 * 14-18 = icon (32)
	 * 19-23 = icon colour 1 (32)
	 * 24-28 = icon colour 2 (32)
	 * 29-32 = icon pos (8)
	 * 
	 *  total combinations = 42,949,672,962
	 */
	public static int packSigil(int pattern, int colour1, int colour2,
			int icon, int iconColour1, int iconColour2, int iconPos){
		
		return pattern  |
				colour1 << 4 |
				colour2 << 9 |
				icon << 14 |
				iconColour1 << 19 |
				iconColour2 << 24 |
				iconPos << 29;
	}
	
	
	
	public static int extractBit(int code, int begin, int end){
		return (code << (31-end)) >>> (31+begin-end);
	}
	
	public static int getPattern(int code){
		return extractBit(code, 0, 3);
	}
	
	public static int getColour1(int code){
		return extractBit(code, 4, 8);
	}
	
	public static int getColour2(int code){
		return extractBit(code, 9, 13);
	}
	
	public static int getIcon(int code){
		return extractBit(code, 14, 18);
	}
	
	public static int getIconColour1(int code){
		return extractBit(code, 19, 23);
	}
	
	public static int getIconColour2(int code){
		return extractBit(code, 24, 28);
	}
	
	public static int getIconPos(int code){
		return extractBit(code, 29, 31);
	}
}
