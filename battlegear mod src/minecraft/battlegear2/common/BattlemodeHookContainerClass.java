package battlegear2.common;

import battlegear2.common.utils.BattlegearUtils;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class BattlemodeHookContainerClass {

	@ForgeSubscribe
	public void playerInterect(PlayerInteractEvent event){
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){//we could put this into client proxy instead of checking the side
			
			if(event.entityPlayer.inventory.isBattlemode()){
				Minecraft mc = FMLClientHandler.instance().getClient();
				switch (event.action) {
				case LEFT_CLICK_BLOCK:
					System.out.println("clicked");
					break;
				case RIGHT_CLICK_BLOCK:
					//TODO add support for tools, although I think this could
					//require too many edits and is likely not viable
					event.entityPlayer.swingOffItem();
					event.useItem = Result.DENY;
					break;
				case RIGHT_CLICK_AIR:
					ItemStack mainHand = event.entityPlayer.getCurrentEquippedItem();
					if(mainHand == null || BattlegearUtils.isMainHand(mainHand.itemID)){
						event.entityPlayer.swingOffItem();
						event.setCanceled(true);
						break;
					}else{
							break;
					}
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void playerIntereactEntity(EntityInteractEvent event){
		if(event.entityPlayer.inventory.isBattlemode()){
			event.entityPlayer.swingOffItem();
			event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
			event.setCanceled(true);
		}
	}
	
}
