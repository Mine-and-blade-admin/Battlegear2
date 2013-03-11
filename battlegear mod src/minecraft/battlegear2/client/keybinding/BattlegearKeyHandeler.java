package battlegear2.client.keybinding;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

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
		Minecraft client=FMLClientHandler.instance().getClient();
		if(client!=null && client.thePlayer!=null && client.currentScreen == null){//null checks to prevent any crash outside the world
			if(kb.keyCode == battleInv.keyCode){
				 client.thePlayer.swingOffItem();
				 //can send a packet here instead of inside the upper method
				 //i'd go with a: commonproxy.sendPacket(client.thePlayer,new Packet18Animation(client.thePlayer, 1));
				 //then the proxy would decide what to do depending on the effective side and type of entityplayer
			}else if (kb.keyCode == drawWeapons.keyCode && tickEnd){
				InventoryPlayer playerInventory = client.thePlayer.inventory;
				if(playerInventory.isBattlemode()){
					previousBattlemode = playerInventory.currentItem;
					playerInventory.currentItem = previousNormal;
				}else{  //i'd use int bounds check (0-8) for the item, just in case
					previousNormal = playerInventory.currentItem;
					playerInventory.currentItem = previousBattlemode;
				}
				client.playerController.updateController();
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
