package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import mods.battlegear2.Battlegear;
import mods.battlegear2.client.gui.BattleEquipGUI;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiBGInventoryButton extends GuiPlaceableButton{

	public GuiBGInventoryButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "BG");
	}

	@Override
	protected void openGui(Minecraft mc) {
		//send packet to open container on server
        PacketDispatcher.sendPacketToServer(new BattlegearGUIPacket(BattlegearGUIHandeler.equipID).generatePacket());
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattleEquipGUI.class;
	}
}
