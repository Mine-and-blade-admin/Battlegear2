package mods.battlegear2.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.api.OffhandAttackEvent;
import mods.battlegear2.common.utils.BattlegearUtils;
import mods.battlegear2.common.utils.EnumBGAnimations;
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
		
		if(event.entityPlayer.isBattlemode()){
			ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
			ItemStack offhandItem =  event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem+3);
			
			switch (event.action) {
			case LEFT_CLICK_BLOCK:
				break;
			case RIGHT_CLICK_BLOCK:
				
				if(offhandItem != null && offhandItem.getItem() instanceof IBattlegearWeapon){
					event.useItem = Result.DENY;
					boolean shouldSwing = ((IBattlegearWeapon)offhandItem.getItem()).offhandClickBlock(event, mainHandItem, offhandItem);
					
					if(shouldSwing){
						event.entityPlayer.swingOffItem();
						BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
					}

				}else{
					event.entityPlayer.swingOffItem();
					BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
				}
				break;
				
			case RIGHT_CLICK_AIR:
				if(mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem.itemID)){
					
					event.useItem = Result.DENY;
					event.setCanceled(true);
					
					if(offhandItem!=null && offhandItem.getItem() instanceof IBattlegearWeapon){
						boolean shouldSwing = ((IBattlegearWeapon)offhandItem.getItem()).offhandClickAir(event, mainHandItem, offhandItem);
						
						if(shouldSwing){
							event.entityPlayer.swingOffItem();
							BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
						}

					}else{
						event.entityPlayer.swingOffItem();
						BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
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
		if(event.entityPlayer.isBattlemode()){
			
			ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
			ItemStack offhandItem =  event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem+3);
			
			if(offhandItem != null && offhandItem.getItem() instanceof IBattlegearWeapon){
				
				OffhandAttackEvent offAttackEvent = new OffhandAttackEvent(event);
				
				((IBattlegearWeapon)offhandItem.getItem()).offhandAttackEntity(offAttackEvent, mainHandItem, offhandItem);
				
				if(offAttackEvent.swingOffhand){
					event.entityPlayer.swingOffItem();
					BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
				}
				
				if(offAttackEvent.shouldAttack){
					event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
				}

			}else{
				event.setCanceled(true);
				event.entityPlayer.swingOffItem();
				event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
				BattleGear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
			}
			
			
		}
	}
	
}
