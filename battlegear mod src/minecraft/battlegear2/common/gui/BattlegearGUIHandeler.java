package battlegear2.common.gui;

import battlegear2.client.gui.BattleEquipGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class BattlegearGUIHandeler implements IGuiHandler{
	
	public static final int equipID = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID){
		case equipID:
			return new ContainerBattle(player.inventory, false, player);
		default:
			return null;
		}
		
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID){
		case equipID:
			return new BattleEquipGUI(player);	
		default:
			return null;
		}
	}

}
