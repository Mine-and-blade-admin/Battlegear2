package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.api.heraldry.ITool;
import mods.battlegear2.client.utils.ImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: nerd-boy
 * Date: 13/08/13
 * Time: 2:57 PM
 * TODO: Add discription
 */
public class TextTool implements ITool {

    public int click_x= -1000;
    public int click_y = -1000;

    public String text = "";

    @Override
    public String getToolName() {
        return "tool.text";
    }

    @Override
    public ResourceLocation getToolImage() {
        return new ResourceLocation("battlegear2:textures/"+getToolName()+".png");
    }

    @Override
    public void drawOverlay(int x, int y, int[] pixals, DynamicTexture overlay, int rgb, boolean shift) {

        BufferedImage bi = new BufferedImage(ImageData.IMAGE_RES, ImageData.IMAGE_RES, BufferedImage.TYPE_4BYTE_ABGR);
        bi.setRGB(0,0,ImageData.IMAGE_RES, ImageData.IMAGE_RES, pixals, 0, ImageData.IMAGE_RES);


        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setColor(new Color(rgb));
        Font f = new Font( "Arial", Font.PLAIN, 10);
        g.setFont(f);
        g.drawString(text, click_x, click_y);

        int length = (int) f.getStringBounds(text, 0, text.length(), g.getFontRenderContext()).getMaxX();


        if((System.currentTimeMillis() / 500) % 2 == 0)
            g.drawLine(length+click_x+1, click_y, length+click_x+1, click_y-8);

        int[] pixelsOverlay = overlay.getTextureData();

        for(int x0 = 0; x0 < ImageData.IMAGE_RES; x0++){
            for(int y0 = 0; y0 < ImageData.IMAGE_RES; y0++){
                pixelsOverlay[x0+ImageData.IMAGE_RES*y0] = bi.getRGB(x0, y0);
            }
        }

        overlay.updateDynamicTexture();

    }

    public void pressEnter(int[] pixals, int rgb){

        BufferedImage bi = new BufferedImage(ImageData.IMAGE_RES, ImageData.IMAGE_RES, BufferedImage.TYPE_4BYTE_ABGR);
        bi.setRGB(0,0,ImageData.IMAGE_RES, ImageData.IMAGE_RES, pixals, 0, ImageData.IMAGE_RES);


        Graphics2D g = (Graphics2D) bi.getGraphics();
        g.setColor(new Color(rgb));
        Font f = new Font( "Arial", Font.PLAIN, 10);
        g.setFont(f);
        g.drawString(text, click_x, click_y);

        for(int x0 = 0; x0 < ImageData.IMAGE_RES; x0++){
            for(int y0 = 0; y0 < ImageData.IMAGE_RES; y0++){
                pixals[x0+ImageData.IMAGE_RES*y0] = bi.getRGB(x0, y0);
            }
        }

        click_x = -1000;
        click_y = -1000;

        text = "";

    }

    @Override
    public void draw(int x, int y, int[] pixels, int rgb, boolean shift) {
        click_x = x;
        click_y = y;
    }
}
