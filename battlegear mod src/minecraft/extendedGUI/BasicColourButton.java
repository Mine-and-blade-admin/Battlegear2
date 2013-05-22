package extendedGUI;

import org.lwjgl.opengl.GL11;

import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class BasicColourButton extends GuiButton{
	
	public int colour;
	
	public BasicColourButton(int id, int x, int y, int width, int height, int colour){
		super(id, x, y, width, height, "");
		this.colour = colour;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		 if (this.drawButton)
	     {
			 {
				 
				 minecraft.renderEngine.bindTexture("/extendedGUI/image/GUI Controls.png");
				 float[] colourarray = SigilHelper.convertColourToARGBArray(colour);
				 GL11.glColor4f(colourarray[2], colourarray[1], colourarray[0], 1);
				 
				 int x = this.xPosition + (width - 16) / 2;
				 int y = this.yPosition + (height - 16) /2;
				 
				 this.drawTexturedModalRect(x, y, 0, 180, 16, 16);
				 
				 GL11.glColor4f(1, 1, 1, 1);
				 this.drawTexturedModalRect(x, yPosition, 16, 180, 16, 16);
				 
			 }
			 
	     }
	}
}
