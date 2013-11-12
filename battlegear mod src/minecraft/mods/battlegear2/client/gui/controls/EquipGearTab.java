package mods.battlegear2.client.gui.controls;

import cpw.mods.fml.common.network.PacketDispatcher;
import mods.battlegear2.client.gui.BattleEquipGUI;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tconstruct.client.tabs.AbstractTab;
//Tinker's Construct support class, for gear inventory
public class EquipGearTab extends AbstractTab {

	public EquipGearTab() {
		super(0, 0, 0, new ItemStack(BattlegearConfig.heradricItem));
	}

	@Override
	public void onTabClicked() {
		BattleEquipGUI.open(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public boolean shouldAddToList() {
		return true;
	}

}
