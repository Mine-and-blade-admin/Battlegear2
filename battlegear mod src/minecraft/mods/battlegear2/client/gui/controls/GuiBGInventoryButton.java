package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiBGInventoryButton extends GuiPlaceableButton{

	public GuiBGInventoryButton(int par1, int par2, int par3) {
		super(par1, par2, par3, "BG");
	}

	@Override
	protected void clicked(Minecraft mc) {
		//send packet to open container on server
        PacketDispatcher.sendPacketToServer(BattlegearGUIPacket.generatePacket(BattlegearGUIHandeler.equipID));
        //Also open on client
        mc.thePlayer.openGui(
                Battlegear.INSTANCE, BattlegearGUIHandeler.equipID, mc.theWorld,
                (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ);
	}
}
