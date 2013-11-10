package mods.battlegear2.client.gui.controls;

import mods.battlegear2.client.gui.BattlegearSigilGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public abstract class GuiPlaceableButton extends GuiButton {
	public static final int HEIGHT = 20;
	public GuiPlaceableButton(int par1, int par2, int par3, String name) {
		super(par1, par2, par3, 24, HEIGHT, name);
	}

	public void place(int count, int guiLeft, int guiTop) {
		this.xPosition = guiLeft;
		this.yPosition = guiTop + count * HEIGHT - 24;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean inWindow = super.mousePressed(mc, mouseX, mouseY);
		if (inWindow && !isInGui(mc.currentScreen)) {
			this.openGui(mc);
		}
		return inWindow;
	}
	
	private boolean isInGui(GuiScreen currentScreen) {
		return currentScreen.getClass()==getGUIClass();
	}

	@Override
	protected int getHoverState(boolean isMouseOver)
    {
		if(!isInGui(Minecraft.getMinecraft().currentScreen)){
			return super.getHoverState(isMouseOver);
		}
		return 0;
    }

	protected abstract Class<? extends GuiScreen> getGUIClass();

	protected abstract void openGui(Minecraft mc);
}
