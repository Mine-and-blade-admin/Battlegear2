package mods.battlegear2.api.heraldry;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;

public class HeraldryTextureSmall extends AbstractTexture {

    private HeraldryData heraldryData;

    public HeraldryTextureSmall(HeraldryData crest) {
        this.heraldryData = crest;
    }

    @Override
    public void loadTexture(IResourceManager resourcemanager) {
        int [][][][] patt = PatternStore.DEFAULT.patterns.get(heraldryData.getPatternIndex());
        BufferedImage image = new BufferedImage(patt[heraldryData.getPattern()][0].length, patt[heraldryData.getPattern()][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);

        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                image.setRGB(x, y, PatternStore.getBlendedSmallPixel(patt, heraldryData.getPattern(), x, y, heraldryData.getColour(0), heraldryData.getColour(1), heraldryData.getColour(2)));
            }
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(),  image);
    }
}


