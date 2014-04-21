package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.api.heraldry.ITool;
import mods.battlegear2.client.utils.ImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Aaron on 3/08/13.
 */
public class FloodFillTool implements ITool {

    public int threshold = 0;

    @Override
    public String getToolName() {
        return "tool.flood";
    }

    @Override
    public ResourceLocation getToolImage() {
        return new ResourceLocation("battlegear2:textures/"+getToolName()+".png");
    }

    @Override
    public void drawOverlay(int x, int y, int[] pixelsCurrent, DynamicTexture overlay, int rgb, boolean shift) {
        int[] pixelsOverlay = overlay.getTextureData();

        System.arraycopy(pixelsCurrent, 0, pixelsOverlay, 0, pixelsOverlay.length);

        if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
            pixelsOverlay[x+ImageData.IMAGE_RES*y] = rgb;
        }

        overlay.updateDynamicTexture();
    }

    @Override
    public void draw(int x, int y, int[] pixelsCurrent, int rgb, boolean shift) {

        if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
            if(shift){
                for(int i = 0; i < pixelsCurrent.length; i++){
                    pixelsCurrent[i] = rgb;
                }
            }else{

                int targetColour = pixelsCurrent[x+ImageData.IMAGE_RES*y];


                if(rgb != targetColour){
                    floodFill(x, y, pixelsCurrent, targetColour, rgb);
                }
            }
        }
    }

    private void floodFill(int x, int y, int[] pixals, int targetColour, int newColour) {

        if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){

            if(pixals[x+ImageData.IMAGE_RES*y] != newColour){
                if(isSame(pixals[x+ImageData.IMAGE_RES*y], targetColour)){
                    pixals[x+ImageData.IMAGE_RES*y] = newColour;

                    floodFill(x+1, y, pixals, targetColour, newColour);
                    floodFill(x-1, y, pixals, targetColour, newColour);
                    floodFill(x, y+1, pixals, targetColour, newColour);
                    floodFill(x, y-1, pixals, targetColour, newColour);
                }
            }

        }
    }


    private boolean isSame(int rgb1, int rgb2){

        /*
int diff = 0;
diff += (((rgb1>>24) & 0x000000FF) - ((rgb2>>24) & 0x000000FF));
diff += (((rgb1>>16) & 0x000000FF) - ((rgb2>>16) & 0x000000FF));
diff += (((rgb1>>8) & 0x000000FF) - ((rgb2>>8) & 0x000000FF));
diff += (((rgb1>>0) & 0x000000FF) - ((rgb2>>0) & 0x000000FF));

return diff <= threshold;
*/

        return rgb1 == rgb2;
    }
}
