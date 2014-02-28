package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

public class GuiColourToggleButton extends GuiToggleButton{

	private int colour;
	
	public GuiColourToggleButton(int id, int x, int y, int colour) {
		super(id, x, y, 15, 15, "");
		this.colour = colour;
		
		System.out.println(
				((float)((colour >> 24) & 0x000000FF)/255F)+", "+
        		((float)((colour >> 16) & 0x000000FF)/255F)+", "+
        		((float)((colour >>  8) & 0x000000FF)/255F)+", "+
        		((float)((colour >>  0) & 0x000000FF)/255F));
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		
		if (this.visible)
        {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.getTextureManager().bindTexture(GuiToggleButton.resourceLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, k*15, this.width, this.height);
            
            GL11.glColor4f(
            		((float)((colour >> 16) & 0x000000FF)/255F),
            		((float)((colour >>  8) & 0x000000FF)/255F),
            		((float)((colour >>  0) & 0x000000FF)/255F),
            		((float)((colour >>  24) & 0x000000FF)/255F));
            
            
            
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, 60, this.width, this.height);
            
            
            this.mouseDragged(par1Minecraft, par2, par3);
            int l = 14737632;

            if (!this.enabled)
            {
                l = -6250336;
            }
            else if (this.field_146123_n)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
	}

	public int getColour() {
		return colour;
	}
	public void setColour(int colour) {
		this.colour = colour;
	}

}
