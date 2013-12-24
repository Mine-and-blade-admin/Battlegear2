package mods.battlegear2.client.gui.controls;

import mods.battlegear2.client.gui.BattlegearSigilGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiSigilButton extends GuiPlaceableButton {

	public GuiSigilButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "Sigil");
	}

	@Override
	protected void openGui(Minecraft mc) {
		BattlegearSigilGUI.open(mc.thePlayer);
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattlegearSigilGUI.class;
	}
}
