package mods.battlegear2.client.heraldry;

import mods.battlegear2.api.heraldry.HeraldryData;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextureBackground extends AbstractTexture {

    private HeraldryData heraldryData;

    public TextureBackground(HeraldryData crest) {
        this.heraldryData = crest;
    }

    @Override
    public void loadTexture(ResourceManager resourcemanager) throws IOException {
        BufferedImage image = null;

        image = new BufferedImage(PatternStore.small_rgbs[heraldryData.getPattern()][0].length, PatternStore.small_rgbs[heraldryData.getPattern()][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);

        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                image.setRGB(x, y, PatternStore.getBlendedSmallPixel(heraldryData.getPattern(), x, y, heraldryData.getColour(0), heraldryData.getColour(1), heraldryData.getColour(2)));
            }
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), image);
    }
}
