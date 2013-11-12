package mods.battlegear2.client.gui.controls;

import mods.battlegear2.Battlegear;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tconstruct.client.tabs.AbstractTab;
//Tinker's Construct support class, for heradlry
public class SigilTab extends AbstractTab {

	public SigilTab() {
		super(1, 0, 0, new ItemStack(1,1,0));
	}

	@Override
	public void onTabClicked() {
		BattlegearSigilGUI.open(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}

}
