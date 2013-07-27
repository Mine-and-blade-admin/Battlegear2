package assets.battlegear2.common;

import java.util.Random;

import assets.battlegear2.api.IBackStabbable;
import assets.battlegear2.api.IBattlegearWeapon;
import assets.battlegear2.api.ILowHitTime;
import assets.battlegear2.api.IPenetrateWeapon;
import assets.battlegear2.api.ISpecialEffect;
import assets.battlegear2.api.OffhandAttackEvent;
import assets.battlegear2.common.utils.BattlegearUtils;
import assets.battlegear2.common.utils.EnumBGAnimations;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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
	
	@ForgeSubscribe
	public void onAttack(LivingAttackEvent event){
		EntityLivingBase entityHit = event.entityLiving;
		//Record the hurt times
		int hurtTimeTemp = entityHit.hurtTime;
		int hurtResistanceTimeTemp = entityHit.hurtResistantTime;
		if(event.source instanceof EntityDamageSource && !(event.source instanceof EntityDamageSourceIndirect))
		{
			Entity attacker = ((EntityDamageSource)event.source).getEntity();
			if(attacker instanceof EntityLivingBase)
			{
				EntityLivingBase entityHitting = (EntityLivingBase)attacker;
				ItemStack weapon = entityHitting.getHeldItem();
				if(weapon!=null)
				{
					boolean hit=false;
					if(weapon.getItem() instanceof IPenetrateWeapon)
					{
						//Attack using the "generic" damage type (ignores armour)
						entityHit.attackEntityFrom(DamageSource.generic, ((IPenetrateWeapon)weapon.getItem()).getPenetratingPower(weapon, entityHit, entityHitting));
						hit=true;
					}
					if(weapon.getItem() instanceof IBackStabbable)
					{
						boolean tempHit = performBackStab(weapon.getItem(), entityHit, entityHitting);
						if(!hit)
							hit = tempHit;
					}
					if(weapon.getItem() instanceof ISpecialEffect)
					{
						performEffects((ISpecialEffect)weapon.getItem(), entityHit, entityHitting);
					}
					if(weapon.getItem() instanceof ILowHitTime)
					{
						//The usual is less than half the max hurt resistance time
						if(entityHit.hurtResistantTime < (float)(entityHit.maxHurtResistantTime) * 0.75F)
						{
							entityHit.hurtResistantTime = ((ILowHitTime)weapon.getItem()).getHitTime(weapon, entityHit);		
						}
					}
					else if(hit)
					{
						//Re-apply the saved values
						entityHit.hurtTime = hurtTimeTemp;
						entityHit.hurtResistantTime = hurtResistanceTimeTemp;
					}
				}
			}
		}
	}
	
	protected boolean performBackStab(Item item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		//Get victim and murderer vector views at hit time
		double[] victimView = new double[]{entityHit.getLookVec().xCoord,entityHit.getLookVec().zCoord};
		double[] murdererView = new double[]{entityHitting.getLookVec().xCoord,entityHitting.getLookVec().zCoord};
		//back-stab conditions: vectors are closely enough aligned, (fuzzy parameter might need testing)
		//but not in opposite directions (face to face or sideways)
		if(Math.abs(victimView[0]*murdererView[1]-victimView[1]*murdererView[0])<0.01 && Math.signum(victimView[0])==Math.signum(murdererView[0]) && Math.signum(victimView[1])==Math.signum(murdererView[1]))
		{
			return ((IBackStabbable)item).onBackStab(entityHit, entityHitting);//Perform back stab effect
		}
		return false;
	}

	protected void performEffects(ISpecialEffect item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		PotionEffect[] effects= item.getEffectsOnHit(entityHit, entityHit);
		for(PotionEffect effect:effects){
			//add effects if they aren't already applied, with a 10% chance
			if(!entityHit.isPotionActive(effect.getPotionID()) && new Random().nextFloat() * 10>9)
				entityHit.addPotionEffect(effect);
		}
	}
}
