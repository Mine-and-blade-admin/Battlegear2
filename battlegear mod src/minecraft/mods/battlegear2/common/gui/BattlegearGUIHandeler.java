package mods.battlegear2.common.gui;

import mods.battlegear2.client.gui.BattleEquipGUI;
import mods.battlegear2.common.BattlegearPacketHandeler;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BattlegearGUIHandeler implements IGuiHandler{
	
	public static final int equipID = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID){
		case equipID:
			return new ContainerBattle(player.inventory, !world.isRemote, player);
		default:
			return null;
		}
		
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID){
		case equipID:
			return new BattleEquipGUI(player, world.isRemote);	
		default:
			return null;
		}
	}

}
