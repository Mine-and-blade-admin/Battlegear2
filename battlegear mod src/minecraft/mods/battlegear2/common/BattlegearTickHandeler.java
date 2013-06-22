package mods.battlegear2.common;

import java.util.EnumSet;
import java.util.Random;

import mods.battlegear2.api.IBackStabbable;
import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.api.ILowHitTime;
import mods.battlegear2.api.ISpecialEffect;
import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import mods.battlegear2.common.utils.BattlegearUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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

	private void performBackStab(Item item, EntityLiving target, EntityPlayer entityPlayer) {
		//Get victim and murderer vector views at hit time
		double[] victimView = new double[]{target.getLookVec().xCoord,target.getLookVec().zCoord};
		double[] murdererView = new double[]{entityPlayer.getLookVec().xCoord,entityPlayer.getLookVec().zCoord};
		//back-stab conditions: vectors are closely enough aligned, (fuzzy parameter might need testing)
		//but not in opposite directions (face to face or sideways)
		if(Math.abs(victimView[0]*murdererView[1]-victimView[1]*murdererView[0])<0.01 && Math.signum(victimView[0])==Math.signum(murdererView[0]) && Math.signum(victimView[1])==Math.signum(murdererView[1]))
		//Perform back stab effect
		{//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
			target.attackEntityFrom(DamageSource.causePlayerDamage(entityPlayer), item.getDamageVsEntity(target));
		}
	}

	private void performEffects(ISpecialEffect item, ItemStack mainhand, EntityLiving target) {
		PotionEffect[] effects=((ISpecialEffect)mainhand.getItem()).getEffectsOnHit(mainhand, (EntityLiving) target);
		for(PotionEffect effect:effects){
			if(!((EntityLiving)target).isPotionActive(effect.getPotionID()) && new Random().nextFloat() * 10>9)
				((EntityLiving) target).addPotionEffect(effect);
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
