package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.HeraldryPattern;

import java.awt.Color;

public class SigilHelper {
	
	public static final int COLOUR_PRIMARY = 0;
	public static final int COLOUR_SECONDARY = 1;
	public static final int COLOUR_SIGIL_PRIMARY = 2;
	public static final int COLOUR_SIGIL_SECONDARY = 3;
	
	public static byte[] getDefault(){
		return HeraldryData.getDefault().getByteArray();
	}
	
	public static final int length = 8;

	/*
	 * Package as follows (for 64 bit long)
	 * 
	 * 0-3   Pattern (16)
	 * 4-15  Primary Colour (4096)
	 * 16-27 Secondary Colour (4096)
	 * 28-32 Sigil (32)
	 * 33-35 Free
	 * 36-38 Sigil Position (8)
	 * 39    Free (Future use to increase Sigil Positions to 16)
	 * 40-51 Sigil Primary Colour (4096)
	 * 52-63 Sigil Secondary Colour (4096)
	 *
	public static byte[] packSigil(byte pattern, byte helm, byte banner, 
			short colour1, short colour2,
			byte sigil, short sigilPos, short sigilColour1, short sigilColour2){
		
		byte[] bytes = new byte[length];
		bytes[0] = (byte)(pattern << 4 & 0xF0 | (helm << 2 & 12 | banner & 3)); //Pattern + helm + banner (12 = 0b1100, 3 = 0b11)
		//bytes[1] = (byte)(colour1 >> 4 & 0xFF); //Colour 1 (R&G)
		//bytes[2] = (byte)(colour1<<4 & 0xF0 | colour1>>8 & 0x0F); //colour 1 (B) & Colour 2(R)
		//bytes[3] = (byte)(colour2 & 0xFF); //colour 2 (G&B)
		
		bytes[4] = (byte)(sigil<<3 & 248 | sigilPos & 7);   //(248 = 0b11111000, 7 = 0b00000111)
		//bytes[5] = (byte)(sigilColour1 >> 4 & 0xFF); //Colour 3 (R&G)
		//bytes[6] = (byte)(sigilColour1<<4 & 0xF0 | sigilColour2>>8 & 0xF); //colour 3 (B) & Colour 4(R)
		//bytes[7] = (byte)(sigilColour2 & 0xFF); //colour 3 (G&B)
		
		updateColour(c, rgb, colour)
		
		return bytes;
		
		
	}*/
	
	public static short get12bitRGB(int rgb){
		return (short)(
				(rgb & 0x00f00000) >> 12 | 
	            (rgb & 0x0000f000) >> 8  | 
	            (rgb & 0x000000f0) >> 4);
	}
	
	public static int extractBit(long code, int begin, int end){
		return (int)((code << (63-end)) >>> (63+begin-end));
	}
	
	private static Color getColourFromBeginIndex(long code, int beginIndex){
		return new Color(
				((extractBit(code, beginIndex+8, beginIndex+11)+1)*16 - 1),
				((extractBit(code, beginIndex+4, beginIndex+7)+1)*16 - 1),
				((extractBit(code, beginIndex, beginIndex+3)+1)*16 - 1)
				);
	}
	
	//TODO: Still have to test this
	public static byte getHelm(byte[] code){
		return (byte)(code[0] >> 2 & 3);
	}
	
	//TODO: Still have to test this
	public static byte getBanner(byte[] code){
		return (byte)(code[0] & 3);
	}
	
	public static Color getPrimaryColour(byte[] code){
		return new Color(
				(code[1] >> 4 & 0x0F)*16,
				(code[1] & 0x0F)*16,
				(code[2] >> 4 & 0x0F)*16);
	}
	
	public static float[] getPrimaryColourArray(byte[] code){
		return new float[]{
				(float)(code[1] >> 4& 0x0F) / 16F,
				(float)(code[1] & 0x0F) / 16F,
				(float)(code[2] >> 4 & 0x0F) / 16F
		};
	}
	
	public static Color getSecondaryColour(byte[] code){
		return new Color(
				(code[2] & 0x0F)*16,
				(code[3] >> 4 & 0x0F)*16,
				(code[3] & 0x0F)*16);
	}
	
	public static float[] getSecondaryColourArray(byte[] code){
		return new float[]{
				(float)(code[2] & 0x0F)/16F,
				(float)(code[3] >> 4 & 0x0F)/16F,
				(float)(code[3] & 0x0F)/16F
		};
	}
	
	public static HeraldryIcon getSigil(byte[] code){
		return HeraldryIcon.values()[code[4] >> 3 & 31]; //248 = 0b11111000
	}
	
	public static HeraldryPositions getSigilPosition(byte[] code){
		return HeraldryPositions.values()[code[4] & 7]; //7 = 0b111
	}
	
	public static Color getSigilPrimaryColour(byte[] code){
		return new Color(
				(code[5] >> 4 & 0x0F)*16,
				(code[5] & 0x0F)*16,
				(code[6] >> 4 & 0x0F)*16);
	}
	
	public static float[] getSigilPrimaryColourArray(byte[] code){
		return new float[]{
				(float)(code[5] >> 4& 0x0F) / 16F,
				(float)(code[5] & 0x0F) / 16F,
				(float)(code[6] >> 4 & 0x0F) / 16F
		};
	}
	
	public static Color getSigilSecondaryColour(byte[] code){
		return new Color(
				(code[6] & 0x0F)*16,
				(code[7] >> 4 & 0x0F)*16,
				(code[7] & 0x0F)*16);
	}
	
	public static float[] getSigilSecondaryColourArray(byte[] code){
		return new float[]{
				(float)(code[6] & 0x0F)/16F,
				(float)(code[7] >> 4 & 0x0F)/16F,
				(float)(code[7] & 0x0F)/16F
		};
	}
	
	public static Color getColour(byte[] code, int colour){
		switch(colour){
		case COLOUR_PRIMARY:
			return getPrimaryColour(code);
		case COLOUR_SECONDARY:
			return getSecondaryColour(code);
		case COLOUR_SIGIL_PRIMARY:
			return getSigilPrimaryColour(code);
		case COLOUR_SIGIL_SECONDARY:
			return getSigilSecondaryColour(code);
			default:
				return Color.WHITE;
		}
	}
	
	public static float[] getColourArray(byte[] code, int colour){
		switch(colour){
		case COLOUR_PRIMARY:
			return getPrimaryColourArray(code);
		case COLOUR_SECONDARY:
			return getSecondaryColourArray(code);
		case COLOUR_SIGIL_PRIMARY:
			return getSigilPrimaryColourArray(code);
		case COLOUR_SIGIL_SECONDARY:
			return getSigilSecondaryColourArray(code);
			default:
				return new float[]{1F, 1F, 1F};
		}
	}
	
	public static byte[] updatePattern(byte[] code, HeraldryPattern newPatern){
		code[0] = (byte)(HeraldryPattern.patterns.indexOf(newPatern) << 4 & 0xF0 | code[0] & 0x0F);
		return code;
	}
	
	//TODO Test this
	public static byte[] updateHelm(byte[] code, byte helm){
		code[0] = (byte)((code[0] & 243) | (helm << 2 & 12)); //243 = 0b11110011, 12 = 0b00001100
		return code;
	}
	
	//TODO Test this
	public static byte[] updateBanner(byte[] code, byte banner){
		code[0] = (byte)((code[0] & 252) | (banner & 3)); //252 = 0b11111100, 3= 0b00000011
		return code;
	}
	
	public static byte[] updateSigil(byte[] code, HeraldryIcon newIcon){
		code[4] = (byte)(newIcon.ordinal() << 3 & 248 | code[4] & 7);//248 = 0b11111000, 7 = 0b0000011
		return code;
	}
	
	public static byte[] updateSigilPos(byte[] code, HeraldryPositions newPos){
		code[4] = (byte)(code[4] & 248 | newPos.ordinal() & 7);//248 = 0b11111000, 7 = 0b0000011
		return code;
	}
	
	public static byte[] updateColour(byte[] code, int rgb, int colour){
		switch(colour){
		case COLOUR_PRIMARY:
			code[1] = (byte)((rgb >> 16) & 0xF0 | (rgb  >> 12 & 0x0F));
			code[2] = (byte)((rgb) & 0xF0 | (code[2] & 0x0F));
			break;
		case COLOUR_SECONDARY:
			code[2] = (byte)((rgb >> 20) & 0x0F | (code[2] & 0xF0));
			code[3] = (byte)((rgb >> 8) & 0xF0 | (rgb  >> 4 & 0x0F));
			break;
		case COLOUR_SIGIL_PRIMARY:
			code[5] = (byte)((rgb >> 16) & 0xF0 | (rgb  >> 12 & 0x0F));
			code[6] = (byte)((rgb) & 0xF0 | (code[6] & 0x0F));
			break;
		case COLOUR_SIGIL_SECONDARY:
			code[6] = (byte)((rgb >> 20) & 0x0F | (code[6] & 0xF0));
			code[7] = (byte)((rgb >> 8) & 0xF0 | (rgb  >> 4 & 0x0F));
			break;
		}
		return code;
	}

	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public static int[] colourTranslationMap = new int[]{
		0x0F0F0F,
		0x4F4F4F,
		0x8F8F8F, //extra grey
		0xCFCFCF, //extra grey
		0xFFFFFF,
		0x7F0F0F,
		0xFF0000,
		0xFF7F7F,
		0xFF7F0F,
		0x7F7F0F,
		0xFFFF0F,
		0xFFFF80,
		0x0F7F0F,
		0x0FFF0F,
		0x7FFF0F,
		0x7FFF8F,
		0x0FFF7F,
		0x0F7F7F,
		0x0FFFFF,
		0x7FFFFF,
		0x0F0F7F,
		0x0F0FFF,
		0x0F7FFF,
		0x7F7FFF,
		0x7F0FFF,
		0x7F0F7F,
		0xFF0FFF,
		0xFF7FFF,
		0xFF0F7F,
		0x7F4F0F, //extra brown
		0xCF7F0F //extra brown
	};
	
	public static int extractBitInt(int code, int begin, int end){
		return (code << (31-end)) >>> (31+begin-end);
	}
}
