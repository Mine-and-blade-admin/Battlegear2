package mods.battlegear2.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class BasicColourButton extends GuiButton{
	
	public Color colour;
	
	public BasicColourButton(int id, int x, int y, int width, int height, Color colour){
		super(id, x, y, width, height, "");
		this.colour = colour;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		 if (this.drawButton)
	     {
			 {
				 
				 minecraft.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI.png");
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
