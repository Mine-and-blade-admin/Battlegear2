package mods.battlegear2.api.heraldry;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RefreshableTexture extends DynamicTexture{

    private int width;
    private int height;
    public RefreshableTexture(BufferedImage par1BufferedImage) {
        super(par1BufferedImage);
        this.width = par1BufferedImage.getWidth();
        this.height = par1BufferedImage.getHeight();
    }

    public RefreshableTexture(int par1, int par2) {
        super(par1, par2);
        this.width = par1;
        this.height = par2;
    }

    public void refreshWith(HeraldryData data, boolean scale){
        refreshWith(data.getPatternIndex(), data, scale);
    }

    public void refreshWith(int patternIndex, HeraldryData data, boolean scale){
        if(patternIndex>=0 && patternIndex<PatternStore.DEFAULT.patterns.size()){
            refreshWith(PatternStore.DEFAULT.patterns.get(patternIndex), data, scale);
        }
    }

    public void refreshWith(int[][][][] pattern, HeraldryData data, boolean scale){
        BufferedImage image = new BufferedImage(pattern[data.getPattern()][0].length, pattern[data.getPattern()][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                image.setRGB(x, y, PatternStore.getBlendedSmallPixel(pattern, data.getPattern(), x, y, data.getColour(0), data.getColour(1), data.getColour(2)));
            }
        }
        if(scale && (image.getHeight() != height || image.getWidth() != width)){
            image = (BufferedImage)image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
        int[] pixels = getTextureData();
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                pixels[x+y*image.getWidth()] = image.getRGB(x,y);
            }
        }
    }
}
