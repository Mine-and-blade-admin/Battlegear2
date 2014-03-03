package mods.battlegear2.client.gui.controls;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.item.ItemDye;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 12:20 PM
 * TODO: Add discription
 */
public class GuiColourPicker extends GuiButton {

    public static final int RES = 128;

    public static final DynamicTexture sb_buffer = new DynamicTexture(RES, RES);
    public static final DynamicTexture hue_buffer = new DynamicTexture(1, RES);
    public static final DynamicTexture background_buffer = new DynamicTexture(2, 2);
    public static final DynamicTexture default_colours = new DynamicTexture(8, 2);

    public static final int DEFAULT_COLOURS = 1;
    public static final int ALPHA_SELECTION = 2;
    public static final int COLOUR_DISPLAY = 4;

    private int dragState = 0;

    private static final int DRAG_NONE = 0;
    private static final int DRAG_SB = 1;
    private static final int DRAG_HUE = 2;
    private static final int DRAG_ALPHA = 3;
    
    
    public List<IControlListener> listeners = new ArrayList<IControlListener>();


    static{
        int[] pixels = hue_buffer.getTextureData();
        for(int i = 0; i < pixels.length; i++){
            pixels[i] = Color.getHSBColor((float)i / (float)RES, 1, 1).getRGB() | 0xFF000000;
        }

        pixels = background_buffer.getTextureData();
        pixels[0] = 0xFF666666;
        pixels[3] = 0xFF666666;

        pixels[1] = 0xFF999999;
        pixels[2] = 0xFF999999;

        pixels = default_colours.getTextureData();
        for(int i = 0; i < pixels.length; i++){
            pixels[i] = ItemDye.field_150922_c[i] | 0xFF000000;
        }
    }

    private int selectedRGB;
    private float[] selectedHSB;
    private float selected_alpha;

    private int sb_start_x;
    private int sb_start_y;

    private int hue_start_x;

    private int alpha_start_x;

    private int type;


    public GuiColourPicker(int id, int x, int y, int rgb, int type){
        super(id, x, y,
                (type&ALPHA_SELECTION)==ALPHA_SELECTION?80:64,
                48 + (type&DEFAULT_COLOURS)==DEFAULT_COLOURS?0:16 + (type&COLOUR_DISPLAY)==COLOUR_DISPLAY?0:16,
                ""
        );

        this.type = type;

        sb_start_x = x;
        sb_start_y = y;

        if(isSwitchOn(DEFAULT_COLOURS)){
            sb_start_y += 16;
        }

        hue_start_x = sb_start_x + 52;

        alpha_start_x = hue_start_x + 16;

        selectColour(rgb);
    }

    private void calculateBuffers() {
        int[] pixels = sb_buffer.getTextureData();
        for(int s = 0; s < RES; s++){
            for(int b = 0; b < RES; b++){
                pixels[s * RES + b] = Color.getHSBColor(selectedHSB[0], 1-(float)s / RES, (float)b / RES).getRGB() | 0xFF000000;
            }
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        super.mouseReleased(par1, par2);
        dragState = DRAG_NONE;
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int x, int y) {
        if(x >= hue_start_x && x < 12+hue_start_x && y >= sb_start_y && y < 48+sb_start_y){
            float hue = ((float)(y-sb_start_y) / 48F);
            hue = Math.max(hue, 0);
            hue = Math.min(hue, 1);

            selectColour(hue, selectedHSB[1], selectedHSB[2], selected_alpha);

            dragState = DRAG_HUE;
            return true;
        }


        if( x >= sb_start_x &&
                x < sb_start_x+48 &&
                y >= sb_start_y &&
                y < sb_start_y+48){

            float sat = 1-((float)(y-sb_start_y) / 48F);
            sat = Math.max(sat, 0);
            sat = Math.min(sat, 1);

            float bright = (float)(x-sb_start_x) / 48F;
            bright = Math.max(bright, 0);
            bright = Math.min(bright, 1);

            selectColour(selectedHSB[0], sat, bright, selected_alpha);

            dragState = DRAG_SB;

            return true;
        }



        if(isSwitchOn(ALPHA_SELECTION) &&
                x >= alpha_start_x && x < 12+alpha_start_x && y >= sb_start_y && y < 48+sb_start_y){
                float alpha = 1 - ((float)(y-sb_start_y) / 48F);
                alpha = Math.max(alpha, 0);
                alpha = Math.min(alpha, 1);

                selectColour(selectedHSB[0], selectedHSB[1], selectedHSB[2], alpha);

                dragState = DRAG_ALPHA;
            return true;
        }



        if(isSwitchOn(DEFAULT_COLOURS)){
            if(x >= xPosition && x < 48+xPosition && y >= yPosition && y < 12+yPosition){
                selectColour(ItemDye.field_150922_c[((x - xPosition) / 6) + (((y - yPosition) / 6) * 8)] | 0xFF000000);
                return true;
            }
        }


        return super.mousePressed(par1Minecraft, x, y);
    }

    @Override
    public void drawButton(Minecraft mc, int mouse_x, int mouse_y) {
        //super.drawButton(mc, mouse_x, mouse_y);

    	if(visible){
	        GL11.glColor3f(1,1,1);
	
	        GL11.glPushMatrix();
	
	        //Draw the saturation / brightness square
	        sb_buffer.updateDynamicTexture();
	        this.drawTexturedModalRect(sb_start_x, sb_start_y, 48, 48, 0,0, 1,1);
	
	        //Draw the hue square
	        hue_buffer.updateDynamicTexture();
	        this.drawTexturedModalRect(hue_start_x, sb_start_y, 12, 48, 0,0, 1,1);
	
	        if(isSwitchOn(ALPHA_SELECTION)){
	            background_buffer.updateDynamicTexture();
	            this.drawTexturedModalRect(alpha_start_x, sb_start_y, 12, 48, 0,0, 2, 8);
	
	            this.drawGradientRect(alpha_start_x, sb_start_y, alpha_start_x+12, sb_start_y+48, selectedRGB | 0xFF000000, selectedRGB & 0x00FFFFFF);
	        }
	
	        if(isSwitchOn(DEFAULT_COLOURS)){
	            default_colours.updateDynamicTexture();
	            this.drawTexturedModalRect(xPosition, yPosition, 48, 12, 0,0, 1, 1 );
	        }
	
	        if(isSwitchOn(COLOUR_DISPLAY)){
	            background_buffer.updateDynamicTexture();
	            this.drawTexturedModalRect(sb_start_x, sb_start_y+52, 48, 12, 0,0, 8, 2);
	
	            drawRect(sb_start_x, sb_start_y+52, sb_start_x+48, sb_start_y+64, selectedRGB);
	            GL11.glColor3f(1,1,1);
	        }
	
	        GL11.glPushMatrix();
	        GL11.glColor3f(1,1,1);
	
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
	        //Saturation Line (Horiz)
	        drawRect2(sb_start_x, sb_start_y + (int)((1-selectedHSB[1]) * 48),sb_start_x+48, sb_start_y + (int)((1-selectedHSB[1]) * 48)+1, 0xFFFFFFFF);
	
	        //Brightness Line (Vertical)
	        drawRect2(sb_start_x+ (int)((selectedHSB[2]) * 48), sb_start_y ,sb_start_x+(int)((selectedHSB[2]) * 48)+1, sb_start_y + 48, 0xFFFFFFFF);
	
	        //Hue Line
	        drawRect2(hue_start_x, sb_start_y + (int)(selectedHSB[0] * 48), hue_start_x+12, sb_start_y + (int)(selectedHSB[0] * 48)+1, 0xFFFFFFFF);
	        
	        //Alpha line
	        if(isSwitchOn(ALPHA_SELECTION)){
	        	drawRect2(alpha_start_x, sb_start_y + (int)((1-selected_alpha) * 48), alpha_start_x+12, sb_start_y + (int)((1-selected_alpha) * 48)+1, 0xFFFFFFFF);
	        }
	        
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glPopMatrix();
	
	        GL11.glPopMatrix();
	
	
	        if(Mouse.isButtonDown(0) && enabled){
	
	            switch (dragState){
	                case DRAG_HUE:
	                    float hue = ((float)(mouse_y-sb_start_y) / 48F);
	                    hue = Math.max(hue, 0);
	                    hue = Math.min(hue, 1);
	
	                    selectColour(hue, selectedHSB[1], selectedHSB[2], selected_alpha);
	                    break;
	                case DRAG_ALPHA:
	                    float alpha = 1-((float)(mouse_y-sb_start_y) / 48F);
	                    alpha = Math.max(alpha, 0);
	                    alpha = Math.min(alpha, 1);
	
	                    selectColour(selectedHSB[0], selectedHSB[1], selectedHSB[2], alpha);
	                    break;
	                case DRAG_SB:
	                    float sat = 1-((float)(mouse_y-sb_start_y) / 48F);
	                    sat = Math.max(sat, 0);
	                    sat = Math.min(sat, 1);
	
	                    float bright = (float)(mouse_x-sb_start_x) / 48F;
	                    bright = Math.max(bright, 0);
	                    bright = Math.min(bright, 1);
	
	                    selectColour(selectedHSB[0], sat, bright, selected_alpha);
	                    break;
	            }
	
	        }else{
	        	dragState = DRAG_NONE;
	        }
    	}
    }

    public void selectColour(int rgb) {
    	
        this.selectedRGB = rgb;
        this.selectedHSB = Color.RGBtoHSB((rgb&0x00FF0000) >> 16, (rgb&0x0000FF00) >> 8, (rgb&0x000000FF), new float[3]);
        selected_alpha = ((float)((rgb & 0xFF000000) >>> 24)) / 255F;

        if(enabled && visible){
	        for(IControlListener l:listeners){
	        	l.actionPreformed(this);
	        }
        }
        
        calculateBuffers();

    }

    public void selectColour(float hue, float sat, float bright, float alpha) {
        this.selectedHSB = new float[]{hue, sat, bright};
        this.selected_alpha = alpha;
        this.selectedRGB = (Color.HSBtoRGB(hue, sat, bright) & 0x00FFFFFF) | (((int)(alpha * 255)) << 24);

        if(enabled && visible){
	        for(IControlListener l:listeners){
	        	l.actionPreformed(this);
	        }
        }
        
        calculateBuffers();
    }

    private boolean isSwitchOn(int switchMask){
        return (type & switchMask) == switchMask;
    }
    
    public void addListener(IControlListener listener){
    	listeners.add(listener);
    }

    public void drawTexturedModalRect(int x, int y, int width, int height, int tex_x, int tex_y, int tex_width, int tex_height)
    {
        //float f = 0.00390625F;
        //float f1 = 0.00390625F;

        float f = 1F;
        float f1 = 1F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + 0) * f), (double)((float)(tex_y + tex_height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + tex_width) * f), (double)((float)(tex_y + tex_height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + tex_width) * f), (double)((float)(tex_y + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + 0) * f), (double)((float)(tex_y + 0) * f1));
        tessellator.draw();
    }


    public static void drawRect2(int x1, int y1, int x2, int y2, int colour)
    {
        int j1;

        if (x1 < x2)
        {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2)
        {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }

        float f = (float)(colour >> 24 & 255) / 255.0F;
        float f1 = (float)(colour >> 16 & 255) / 255.0F;
        float f2 = (float)(colour >> 8 & 255) / 255.0F;
        float f3 = (float)(colour & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)x1, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y1, 0.0D);
        tessellator.addVertex((double)x1, (double)y1, 0.0D);
        tessellator.draw();
    }

    public int getRGB() {
        return selectedRGB;
    }
}
