package battlegear2.common;

import battlegear2.api.IBattlegearWeapon;
import battlegear2.api.OffhandAttackEvent;
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
		if(event.entityPlayer.inventory.isBattlemode()){
			
			ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
			ItemStack offhandItem =  event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem+3);
			
			switch (event.action) {
			case LEFT_CLICK_BLOCK:
				break;
			case RIGHT_CLICK_BLOCK:
				
				if(offhandItem.getItem() instanceof IBattlegearWeapon){
					event.useItem = Result.DENY;
					boolean shouldSwing = ((IBattlegearWeapon)offhandItem.getItem()).offhandClickBlock(event, mainHandItem, offhandItem);
					
					if(shouldSwing){
						event.entityPlayer.swingOffItem();
					}

				}else{
					event.entityPlayer.swingOffItem();
				}
				break;
				
			case RIGHT_CLICK_AIR:
				if(mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem.itemID)){
					
					event.useItem = Result.DENY;
					event.setCanceled(true);
					
					if(offhandItem.getItem() instanceof IBattlegearWeapon){
						boolean shouldSwing = ((IBattlegearWeapon)offhandItem.getItem()).offhandClickAir(event, mainHandItem, offhandItem);
						
						if(shouldSwing){
							event.entityPlayer.swingOffItem();
						}

					}else{
						event.entityPlayer.swingOffItem();
					}
					break;
					

				}else{
						break;
				}
			}
		}
		
	}
	
	@ForgeSubscribe
	public void playerIntereactEntity(EntityInteractEvent event){
		if(event.entityPlayer.inventory.isBattlemode()){
			
			ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
			ItemStack offhandItem =  event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem+3);
			
			if(offhandItem.getItem() instanceof IBattlegearWeapon){
				
				OffhandAttackEvent offAttackEvent = new OffhandAttackEvent(event);
				
				((IBattlegearWeapon)offhandItem.getItem()).offhandAttackEntity(offAttackEvent, mainHandItem, offhandItem);
				
				if(offAttackEvent.swingOffhand){
					event.entityPlayer.swingOffItem();
				}
				
				if(offAttackEvent.shouldAttack){
					event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
				}

			}else{
				event.setCanceled(true);
				event.entityPlayer.swingOffItem();
				event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
			}
			
			
		}
	}
	
}
