package guiToolkit;

import java.awt.Color;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

public class GuiHSBColourPicker extends GuiButton{
	
	private float[] hsb = new float[3];
	private byte dragState = 0; //0 = none, 1 = hue, 2 = bright/sat
	
	private static final byte DRAG_NONE = 0;
	private static final byte DRAG_HUE = 1;
	private static final byte DRAG_SAT_BRIGHT = 2;
	
	public GuiHSBColourPicker(int id, int xPos, int yPos, Color colour) {
		super(id, xPos, yPos, 64, 50, "");
		hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), hsb);
	}
	
	public GuiHSBColourPicker(int id, int xPos, int yPos, int rgb){
		this(id, xPos, yPos, new Color(rgb));
	}
	
	public GuiHSBColourPicker(int id, int xPos, int yPos){
		this(id, xPos, yPos, Color.BLACK);
	}
	
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (this.drawButton){
			FontRenderer var4 = minecraft.fontRenderer;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/guiToolkit/Toolkit.png"));
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 178,143, this.width, this.height);			
			GL11.glPushMatrix();
			
			this.drawGradientRectSaturation(xPosition+1, yPosition+1, xPosition+49, yPosition+49, Color.HSBtoRGB(hsb[0], 1, 1));
			
			this.drawGradientRectValue(xPosition+1, yPosition+1, xPosition+49, yPosition+49);

			 if(Mouse.isButtonDown(0))
				 this.mouseDragged(minecraft, mouseX, mouseY);
			 else{
				 mouseReleased(mouseX, mouseY);
			 }
			 
			 drawMousePos(mouseX, mouseY, minecraft);
			 
			 GL11.glPopMatrix();
		}
	}
	
	private void drawMousePos(int mouseX, int mouseY, Minecraft mc) {
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/guiToolkit/Toolkit.png"));
		
		this.drawTexturedModalRect(2*(this.xPosition+52), 2*((int)(this.yPosition+hsb[0]*47)), 218, 193, 24, 7);
		
		this.drawTexturedModalRect(2*((int)(this.xPosition+hsb[2]*47))-1, 2*((int)(this.yPosition+(1-hsb[1])*47))-1, 179, 193, 7, 7);
		GL11.glPopMatrix();
	}

	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY) {
		if(super.mousePressed(par1Minecraft, mouseX, mouseY)){
			int x = mouseX - xPosition;
			int y = mouseY - yPosition;
			
			if (y > 0 && y < 64){
				if(x > 52 && x < 64){
					hsb[0] = selectHue(x,y);
					dragState = dragState == DRAG_NONE ? DRAG_HUE : DRAG_NONE;
				}else if (x > 0 && x < 50){
					hsb[1] = selectSaturation(x, y);
					hsb[2] = selectBrightness(x, y);
					dragState = dragState == DRAG_NONE ? DRAG_SAT_BRIGHT : DRAG_NONE;
				}
			}
			
			return true;
		}else{
			return false;
		}
	}
	
	@Override
    public void mouseReleased(int par1, int par2)
    {
       this.dragState = DRAG_NONE;
    }
	
	@Override
	protected void mouseDragged(Minecraft par1Minecraft, int mouseX, int mouseY) {
		
		int x = mouseX - xPosition;
		int y = mouseY - yPosition;
		if(enabled){
			if(dragState == DRAG_HUE){
				hsb[0] = selectHue(x, y);
			}else if (dragState == DRAG_SAT_BRIGHT){
				hsb[1] = selectSaturation(x, y);
				hsb[2] = selectBrightness(x, y);
			}
		}
	}

	private float selectHue(int x, int y) {
		y = y - 1;
		y = Math.min(46, y);
		y = Math.max(0, y);
		
		return (float)y / 46F;
	}
	
	private float selectSaturation(int x, int y) {
		y = y - 1;
		y = Math.min(46, y);
		y = Math.max(0, y);
		
		return 1 - ((float)y / 46F);
	}
	
	private float selectBrightness(int x, int y){
		x = x-1;
		x = Math.min(46, x);
		x = Math.max(0, x);
		
		return ((float)x / 46F);
	}
	

	/**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    protected void drawGradientRectSaturation(int x1, int y1, int x2, int y2, int col)
    {
    	int par6 = 0xFFFFFFFF; //White
        float f = (float)(col >> 24 & 255) / 255.0F;
        float f1 = (float)(col >> 16 & 255) / 255.0F;
        float f2 = (float)(col >> 8 & 255) / 255.0F;
        float f3 = (float)(col & 255) / 255.0F;
        float f4 = (float)(par6 >> 24 & 255) / 255.0F;
        float f5 = (float)(par6 >> 16 & 255) / 255.0F;
        float f6 = (float)(par6 >> 8 & 255) / 255.0F;
        float f7 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)x2, (double)y1, (double)this.zLevel);
        tessellator.addVertex((double)x1, (double)y1, (double)this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)x1, (double)y2, (double)this.zLevel);
        tessellator.addVertex((double)x2, (double)y2, (double)this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    protected void drawGradientRectValue(int x1, int y1, int x2, int y2)
    {
    	int col = 0xFF000000; //black
    	int par6 = 0x00FFFFFF; //white
        float f = (float)(col >> 24 & 255) / 255.0F;
        float f1 = (float)(col >> 16 & 255) / 255.0F;
        float f2 = (float)(col >> 8 & 255) / 255.0F;
        float f3 = (float)(col & 255) / 255.0F;
        float f4 = (float)(par6 >> 24 & 255) / 255.0F;
        float f5 = (float)(par6 >> 16 & 255) / 255.0F;
        float f6 = (float)(par6 >> 8 & 255) / 255.0F;
        float f7 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)x1, (double)y1, (double)this.zLevel);
        tessellator.addVertex((double)x1, (double)y2, (double)this.zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)x2, (double)y2, (double)this.zLevel);
        tessellator.addVertex((double)x2, (double)y1, (double)this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
	
	
	public int getRGB(){
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}

	public void setColour(int rgb) {
		setColour(new Color(rgb));
	}
	
	public void setColour(Color rgb) {
		hsb = Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), hsb);
	}
}
