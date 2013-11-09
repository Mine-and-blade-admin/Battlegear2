package mods.battlegear2.client.gui.controls;

import mods.battlegear2.Battlegear;
import mods.battlegear2.client.gui.BattleEquipGUI;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiSigilButton extends GuiPlaceableButton {

	public GuiSigilButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "Sigil");
	}

	@Override
	protected void openGui(Minecraft mc) {
		//PacketDispatcher.sendPacketToServer(BattlegearGUIPacket.generatePacket(BattlegearGUIHandeler.sigilEditor));
		mc.thePlayer.openGui(Battlegear.INSTANCE, BattlegearGUIHandeler.sigilEditor, mc.theWorld,
				(int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
	}

	@Override
	protected Class<? extends GuiScreen> getGUIClass() {
		return BattlegearSigilGUI.class;
	}
}
