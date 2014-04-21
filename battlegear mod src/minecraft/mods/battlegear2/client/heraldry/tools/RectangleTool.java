package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.api.heraldry.ITool;
import mods.battlegear2.client.utils.ImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

/**
 * User: nerd-boy
 * Date: 12/08/13
 * Time: 11:27 AM
 * TODO: Add discription
 */
public class RectangleTool implements ITool {

    public int last_x = -1000;
    public int last_y = -1000;

    @Override
    public String getToolName() {
        return "tool.rectangle";
    }

    @Override
    public ResourceLocation getToolImage() {
        return new ResourceLocation("battlegear2:textures/"+getToolName()+".png");
    }

    @Override
    public void drawOverlay(int x, int y, int[] pixelsCurrent, DynamicTexture overlay, int rgb, boolean shift) {

        int[] pixelsOverlay = overlay.getTextureData();
        System.arraycopy(pixelsCurrent, 0, pixelsOverlay, 0, pixelsOverlay.length);

        if(Mouse.isButtonDown(0) && last_x > -1000 && last_y > -1000){
            if(shift){
                if(last_y > y)
                    y = last_y - Math.abs(last_x - x);
                else
                    y = last_y + Math.abs(last_x - x);
            }

            drawShape(Math.min(last_x, x),
                    Math.min(last_y, y),
                    Math.max(last_x, x),
                    Math.max(last_y, y),
                    pixelsOverlay,
                    rgb);
        }
    }

    @Override
    public void draw(int x, int y, int[] pixels, int rgb, boolean shift) {
        if(Mouse.getEventButton() == 0 &&(!Mouse.getEventButtonState()) && last_x > -1000 && last_y > -1000){
            if(shift){
                if(last_y > y)
                    y = last_y - Math.abs(last_x - x);
                else
                    y = last_y + Math.abs(last_x - x);
            }

            drawShape(Math.min(last_x, x),
                    Math.min(last_y, y),
                    Math.max(last_x, x),
                    Math.max(last_y, y),
                    pixels,
                    rgb);

        }
    }

    protected void drawShape(int minX, int minY, int maxX, int maxY, int[] pixels, int rgb){

        for(int i = minX; i <= maxX; i++){
            if (i > -1 && i < ImageData.IMAGE_RES ){
                if(minY > -1 && minY < ImageData.IMAGE_RES){
                    pixels[i+ImageData.IMAGE_RES*minY] = rgb;
                }
                if(maxY > -1 && maxY < ImageData.IMAGE_RES){
                    pixels[i+ImageData.IMAGE_RES*maxY] = rgb;
                }
            }
        }

        for(int i = minY; i <= maxY; i++){
            if (i > -1 && i < ImageData.IMAGE_RES ){
                if(minX > -1 && minX < ImageData.IMAGE_RES){
                    pixels[minX+ImageData.IMAGE_RES*i] = rgb;
                }
                if(maxX > -1 && maxX < ImageData.IMAGE_RES){
                    pixels[maxX+ImageData.IMAGE_RES*i] = rgb;
                }
            }
        }
    }
}
