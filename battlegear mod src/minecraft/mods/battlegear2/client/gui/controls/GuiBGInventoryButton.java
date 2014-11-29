package mods.battlegear2.client.gui.controls;

import mods.battlegear2.client.gui.BattleEquipGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public final class GuiBGInventoryButton extends GuiPlaceableButton{

	public GuiBGInventoryButton(int par1) {
		super(par1, "BG");
	}

	@Override
	protected void openGui(Minecraft mc) {
		BattleEquipGUI.open(mc.thePlayer);
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattleEquipGUI.class;
	}

    @Override
    public GuiPlaceableButton copy(){
        return new GuiBGInventoryButton(this.id);
    }
}
