package mods.battlegear2.common;

import java.beans.FeatureDescriptor;
import java.util.EnumSet;

import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.common.utils.BattlegearUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class BattlegearTickHandeler implements ITickHandler{

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		if(type.contains(TickType.PLAYER)){
			
			EntityPlayer entityPlayer = (EntityPlayer) tickData[0];
			
			if(entityPlayer.worldObj instanceof WorldServer && entityPlayer.ticksExisted%5 ==0){
				/*
				((WorldServer)entityPlayer.worldObj)
				.getEntityTracker().sendPacketToAllPlayersTrackingEntity(
						entityPlayer, BattlegearPacketHandeler.generateSyncBattleItemsPacket(entityPlayer.username, entityPlayer.inventory)
						);
						*/
			}
			
			//If we JUST swung an Item
			if(entityPlayer.swingProgressInt == 1){
				ItemStack mainhand = entityPlayer.getCurrentEquippedItem();
				if(mainhand != null && mainhand.getItem() instanceof IExtendedReachWeapon){
					float extendedReach = ((IExtendedReachWeapon)mainhand.getItem()).getreachInBlocks(mainhand);
					MovingObjectPosition mouseOver = BattleGear.proxy.getMouseOver(0, extendedReach);
					if(mouseOver != null && mouseOver.typeOfHit == EnumMovingObjectType.ENTITY){
						Entity target = mouseOver.entityHit;
						if(target instanceof EntityLiving){
							if(target.hurtResistantTime != ((EntityLiving) target).maxHurtResistantTime){
								FMLClientHandler.instance().getClient().playerController.attackEntity(entityPlayer, target);
							}
						}
					}
				}
			}
		}
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "battlegear.ticks";
	}

}
