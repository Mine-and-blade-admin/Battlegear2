package assets.battlegear2.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class BasicColourButton extends GuiButton{
	
	public Color colour;
	public static final ResourceLocation sigilGui = new ResourceLocation("battlegear2","/textures/gui/Sigil GUI.png");
	
	public BasicColourButton(int id, int x, int y, int width, int height, Color colour){
		super(id, x, y, width, height, "");
		this.colour = colour;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		 if (this.drawButton)
	     {
			 {
				 minecraft.renderEngine.func_110577_a(sigilGui);
				 GL11.glColor3ub((byte)colour.getRed(), (byte)colour.getGreen(), (byte)colour.getBlue());

				 int x = this.xPosition;
				 int y = this.yPosition;
				 
				 this.drawTexturedModalRect(x, y, 75, 247, 9, 9);
				 
				 GL11.glColor4f(1, 1, 1, 1);
				 this.drawTexturedModalRect(x, yPosition, 66, 247, 9, 9);
				 
			 }
			 
	     }
	}
}
