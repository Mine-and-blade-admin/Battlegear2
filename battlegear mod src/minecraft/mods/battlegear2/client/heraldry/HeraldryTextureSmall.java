package mods.battlegear2.client.heraldry;

import mods.battlegear2.api.heraldry.HeraldryData;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HeraldryTextureSmall extends AbstractTexture {

    private HeraldryData crest;

    public HeraldryTextureSmall(HeraldryData crest) {
        this.crest = crest;
    }

    @Override
    public void func_110551_a(ResourceManager resourcemanager) throws IOException {
        BufferedImage image = null;

        image = new BufferedImage(PatternStore.small_rgbs[crest.getPattern()][0].length, PatternStore.small_rgbs[crest.getPattern()][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);

        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                image.setRGB(x,y,PatternStore.getBlendedSmallPixel(crest.getPattern(), x, y, crest.getColour(0), crest.getColour(1), crest.getColour(2)));
            }
        }

        CrestImages crestImage = CrestImages.images[crest.getCrest()];
        if(crestImage.getId() != 0 && crestImage != null){
        	System.out.println(crestImage);
        	Graphics g = image.getGraphics();
        	//g.setColor(new Color(0x00000000));
            g.drawImage(crestImage.getImage(crest.getColour(3)), image.getWidth() / 4, image.getHeight() / 4, image.getWidth()/2, image.getHeight()/2, null);
            g.dispose();
            
            System.out.println(Integer.toHexString(crest.getColour(3)));
        }


        TextureUtil.func_110987_a(this.func_110552_b(), image);
    }
}


