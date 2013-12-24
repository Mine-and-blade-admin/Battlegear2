package mods.battlegear2.client.gui.controls;

import mods.battlegear2.client.gui.BattleEquipGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiBGInventoryButton extends GuiPlaceableButton{

	public GuiBGInventoryButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "BG");
	}

	@Override
	protected void openGui(Minecraft mc) {
		BattleEquipGUI.open(mc.thePlayer);
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattleEquipGUI.class;
	}
}
