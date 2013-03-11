package battlegear2.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
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
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			
			if(event.entityPlayer.inventory.isBattlemode()){
				Minecraft mc = FMLClientHandler.instance().getClient();
				switch (event.action) {
				case LEFT_CLICK_BLOCK:
					break;
				case RIGHT_CLICK_BLOCK:
					//TODO add support for tools
					event.entityPlayer.swingOffItem();
					event.setCanceled(true);
					break;
				case RIGHT_CLICK_AIR:
					event.entityPlayer.swingOffItem();
					event.setCanceled(true);
					break;
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void playerIntereactEntity(EntityInteractEvent event){
		if(event.entityPlayer.inventory.isBattlemode()){
			event.entityPlayer.swingOffItem();
			event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
		}
	}
	
}
