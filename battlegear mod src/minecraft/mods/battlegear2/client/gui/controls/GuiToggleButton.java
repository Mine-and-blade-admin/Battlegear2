package mods.battlegear2.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiToggleButton extends GuiButton{
	public static final ResourceLocation resourceLocation = new ResourceLocation("battlegear2", "textures/gui/widgets-extra.png");
	private boolean isSelected = false;
	private GuiToggleButton[] siblingButtons;

    public GuiToggleButton(int id, int x, int y, String label, FontRenderer font) {
        this(id, x, y, font.getStringWidth(label)+10, 20, label);
    }

	public GuiToggleButton(int id, int x, int y, int width, int height, String label) {
		super(id, x, y, width, height, label);
		siblingButtons = new GuiToggleButton[0];
	}
	
	public void setSelected(boolean b) {
		isSelected = b;
	}

	public boolean getSelected(){
		return isSelected;
	}
	
	public void setSiblings(GuiToggleButton[] buttons){
		this.siblingButtons = buttons;
	}
	
	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		if (this.visible)
        {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.getTextureManager().bindTexture(resourceLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2,  k * 20, this.width / 2, this.height);
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
	
	/**
     * Returns 
     * 0 if the button is disabled
     * 1 if the mouse is NOT hovering over this button (and it IS selected)
     * 2 if it IS hovering over this button.
     * 3 if the mouse is NOT hovering over this button (and it IS NOT selected)
     */
    @Override
    protected int getHoverState(boolean par1)
    {
        byte b0 = 3;

        if(isSelected){
        	b0 = 1;
        }else if (!this.enabled){
            b0 = 0;
        }
        else if (par1)
        {
            b0 = 2;
        }
        return b0;
    }

    public void toggleDisplayString(){
        int i = this.displayString.indexOf("true");
        if(i>0){
            this.displayString = this.displayString.substring(0, i)+"false";
        }else{
            i = this.displayString.indexOf("false");
            if(i>0)
                this.displayString = this.displayString.substring(0, i)+"true";
        }
    }

}
