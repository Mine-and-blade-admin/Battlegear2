package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public abstract class GuiPlaceableButton extends GuiButton {
	public GuiPlaceableButton(int par1, int par2, int par3, String name) {
		super(par1, par2, par3, 24, 20, name);
	}

	public void place(int count, int guiLeft, int guiTop) {
		this.xPosition = guiLeft + count * 24;
		this.yPosition = guiTop - 24;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean inWindow = super.mousePressed(mc, mouseX, mouseY);
		if (inWindow) {
			this.clicked(mc);
		}
		return inWindow;
	}

	protected abstract void clicked(Minecraft mc);
}
