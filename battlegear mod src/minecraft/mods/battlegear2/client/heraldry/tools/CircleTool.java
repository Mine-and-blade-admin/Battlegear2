package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.client.utils.ImageData;

/**
 * User: nerd-boy
 * Date: 12/08/13
 * Time: 12:04 PM
 * TODO: Add discription
 */
public class CircleTool extends RectangleTool {

    @Override
    public String getToolName() {
        return "tool.circle";
    }

    private void plotPoint(int x, int y, int[] pixels, int rgb){
        if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
            pixels[x+ImageData.IMAGE_RES*y] = rgb;
        }
    }

    //@Override
    protected void drawShape2(int minX, int minY, int maxX, int maxY, int[] pixels, int rgb) {
        //Formula = (x-h)^2/a^2 +(y-k)^2/b^2 = 1

        float x_mid = (maxX + minX) / 2F;
        float y_mid = (maxY + minY) / 2F;

        float x_rad = (maxX - minX) / 2F;
        float y_rad = (maxY - minY) / 2F;

        float x_rad_sq = x_rad * x_rad;
        float y_rad_sq = y_rad * y_rad;

        for(float time = 0; time < Math.PI/2; time += 0.05F){
            plotPoint((int)Math.round((x_mid + x_rad * Math.cos(time))), (int)Math.round(y_mid + y_rad * Math.sin(time)), pixels, rgb);
            plotPoint((int)Math.round((x_mid - x_rad * Math.cos(time))), (int)Math.round(y_mid + y_rad * Math.sin(time)), pixels, rgb);
            plotPoint((int)Math.round((x_mid + x_rad * Math.cos(time))), (int)Math.round(y_mid - y_rad * Math.sin(time)), pixels, rgb);
            plotPoint((int)Math.round((x_mid - x_rad * Math.cos(time))), (int)Math.round(y_mid - y_rad * Math.sin(time)), pixels, rgb);
        }

    }

    @Override
    protected void drawShape(int minX, int minY, int maxX, int maxY, int[] pixels, int rgb) {
        float x_mid = (maxX + minX) / 2F;
        float y_mid = (maxY + minY) / 2F;

        float x_rad = (maxX - minX) / 2F;
        float y_rad = (maxY - minY) / 2F;

        float x_r_sq = x_rad * x_rad;
        float y_r_sq = y_rad * y_rad;

        int last_x = 0;
        float last_y = y_rad;

        for(int x = 1; x <= x_rad; x++){
            float y = (float) (y_rad * Math.sqrt(x_r_sq - (x * x)) / x_rad);

            drawLine((int)Math.ceil(x_mid+last_x), (int)Math.ceil(x_mid+x), (int)Math.ceil(y_mid+last_y), (int)Math.ceil(y_mid+y), pixels, rgb);
            drawLine((int)Math.floor(x_mid-last_x), (int)Math.floor(x_mid-x), (int)Math.ceil(y_mid+last_y), (int)Math.ceil(y_mid+y), pixels, rgb);

            drawLine((int)Math.ceil(x_mid+last_x), (int)Math.ceil(x_mid+x), (int)Math.floor(y_mid-last_y), (int)Math.floor(y_mid-y), pixels, rgb);
            drawLine((int)Math.floor(x_mid-last_x), (int)Math.floor(x_mid-x), (int)Math.floor(y_mid-last_y), (int)Math.floor(y_mid-y), pixels, rgb);


            last_x = x;
            last_y = y;

        }


        drawLine((int)Math.floor(x_mid-last_x), (int)Math.floor(x_mid-last_x), (int)Math.floor(y_mid-last_y), (int)Math.ceil(y_mid+last_y), pixels, rgb);
        drawLine((int)Math.ceil(x_mid+last_x), (int)Math.ceil(x_mid+last_x), (int)Math.floor(y_mid-last_y), (int)Math.ceil(y_mid+last_y), pixels, rgb);


        //drawLine(Math.round(x_mid+last_x), Math.round(x_mid+last_x), Math.round(y_mid-last_y), Math.round(y_mid+last_y), pixels, rgb);

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
