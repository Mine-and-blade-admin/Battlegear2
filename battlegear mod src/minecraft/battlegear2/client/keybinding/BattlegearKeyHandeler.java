package battlegear2.client.keybinding;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import battlegear2.common.BattleGear;
import battlegear2.common.BattlegearPacketHandeler;
import battlegear2.common.gui.BattlegearGUIHandeler;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BattlegearKeyHandeler extends KeyHandler{

	public static KeyBinding battleInv = new KeyBinding("Battle Inventory", Keyboard.KEY_I);
	public static KeyBinding drawWeapons = new KeyBinding("Draw Weapons", Keyboard.KEY_R);
	
	private static int previousNormal = 0;
	private static int previousBattlemode = InventoryPlayer.offset;
	
	public BattlegearKeyHandeler() {
		super(new KeyBinding[]{battleInv, drawWeapons}, new boolean[]{false, false});
	}

	@Override
	public String getLabel() {
		return "Battlegear2";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		
		if(FMLClientHandler.instance().getClient().currentScreen == null){
			
			EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
			
			if(kb.keyCode == battleInv.keyCode){
				//send packet to open container on server
				PacketDispatcher.sendPacketToServer(BattlegearPacketHandeler.generateGUIPacket(BattlegearGUIHandeler.equipID));
				//Also open on client
				player.openGui(
						BattleGear.instance, BattlegearGUIHandeler.equipID,
						FMLClientHandler.instance().getClient().theWorld,
						(int)player.posX, (int)player.posY, (int)player.posZ); 
				
			}else if (kb.keyCode == drawWeapons.keyCode && tickEnd){
				InventoryPlayer playerInventory = player.inventory;
				if(playerInventory.isBattlemode()){
					previousBattlemode = playerInventory.currentItem;
					playerInventory.currentItem = previousNormal;
				}else{
					previousNormal = playerInventory.currentItem;
					playerInventory.currentItem = previousBattlemode;
				}
				FMLClientHandler.instance().getClient().playerController.updateController();
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
    public EnumSet<TickType> ticks() {
            return EnumSet.of(TickType.CLIENT);
    }

}
