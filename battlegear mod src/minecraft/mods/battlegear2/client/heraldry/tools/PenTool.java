package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.api.heraldry.ITool;
import mods.battlegear2.client.utils.ImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Aaron on 3/08/13.
 */
public class PenTool implements ITool {

    private int last_x = ImageData.IMAGE_RES / 2;
    private int last_y = ImageData.IMAGE_RES / 2;


    @Override
    public String getToolName() {
        return "tool.pen";
    }

    @Override
    public ResourceLocation getToolImage() {
        return new ResourceLocation("battlegear2:textures/"+getToolName()+".png");
    }

    @Override
    public void drawOverlay(int x, int y, int[] pixelsCurrent, DynamicTexture overlay, int rgb, boolean shift) {
        int[] pixelsOverlay = overlay.getTextureData();

        System.arraycopy(pixelsCurrent, 0, pixelsOverlay, 0, pixelsOverlay.length);

        if(shift){
            drawLine(x, last_x, y, last_y, pixelsOverlay, rgb);
        }else{
            if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
                pixelsOverlay[x+ImageData.IMAGE_RES*y] = rgb;
            }
        }

        overlay.updateDynamicTexture();
    }

    @Override
    public void draw(int x, int y, int[] pixelsCurrent, int rgb, boolean shift) {
        if(shift){

            drawLine(x, last_x, y, last_y, pixelsCurrent, rgb);

        }else{
            if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
                pixelsCurrent[x+ImageData.IMAGE_RES*y] = rgb;
            }
        }

        last_x = x;
        last_y = y;
    }

    private void drawLine(int x0, int x1, int y0, int y1, int[] pixelsCurrent, int rgb) {

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0<x1 ? 1 : -1;
        int sy = y0<y1 ? 1 : -1;
        int err = dx - dy;


        boolean done = false;
        while(!done){
            if (x0 > -1 && x0 < ImageData.IMAGE_RES && y0 > -1 && y0 < ImageData.IMAGE_RES){
                pixelsCurrent[x0+ImageData.IMAGE_RES*y0] = rgb;
            }
            if(x0 == x1 && y0 == y1){
                done = true;
            }
            int e2 = 2*err;
            if(e2 > -dy && !done){
                err = err - dy;
                x0 = x0 + sx;
            }
            if(x0 == x1 && y0 == y1 && !done){
                if (x0 > -1 && x0 < ImageData.IMAGE_RES && y0 > -1 && y0 < ImageData.IMAGE_RES){
                    pixelsCurrent[x0+ImageData.IMAGE_RES*y0] = rgb;
                }
                done = true;
            }
            if(e2 < dx && !done){
                err = err + dx;
                y0 = y0 + sy;
            }
        }
    }


}
