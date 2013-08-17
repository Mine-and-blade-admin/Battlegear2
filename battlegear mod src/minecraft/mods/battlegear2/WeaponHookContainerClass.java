package mods.battlegear2;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Random;

import mods.battlegear2.api.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

/**
 * User: nerd-boy
 * Date: 30/07/13
 * Time: 12:36 PM
 * Events registered with MinecraftForge event bus on default priority: 
 * LivingAttackEvent, to perform weapons custom effects
 * ArrowLooseEvent, to perform bow calculations for the quiver item
 */
public class WeaponHookContainerClass {

	public static final float backstabFuzzy = 0.01F;
    @ForgeSubscribe
    public void onAttack(LivingAttackEvent event){

        /*boolean isBlockWithShield = false;
        if(event.entity instanceof EntityPlayer){
            isBlockWithShield = ((EntityPlayer) event.entity).isBlockingWithShield();
        }
        if(isBlockWithShield){
            event.setCanceled(true);
        }else*/{
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
                    ItemStack stack = entityHitting.getHeldItem();
                    if(stack!=null)
                    {
                        boolean hit=false;
                        if(stack.getItem() instanceof IPenetrateWeapon)
                        {
                            //Attack using the "generic" damage type (ignores armour)
                            entityHit.attackEntityFrom(DamageSource.generic, ((IPenetrateWeapon)stack.getItem()).getPenetratingPower(stack));
                            hit=true;
                        }
                        if(stack.getItem() instanceof IBackStabbable)
                        {
                            boolean tempHit = performBackStab(stack.getItem(), entityHit, entityHitting);
                            if(!hit)
                                hit = tempHit;
                        }
                        if(stack.getItem() instanceof ISpecialEffect)
                        {
                            boolean tempHit = ((ISpecialEffect)stack.getItem()).performEffects(entityHit,entityHitting);
                            if(!hit)
                                hit = tempHit;
                        }
                        if(stack.getItem() instanceof IPotionEffect)
                        {
                            performEffects(((IPotionEffect)stack.getItem()).getEffectsOnHit(entityHit, entityHitting), entityHit);
                        }
                        if(stack.getItem() instanceof IHitTimeModifier)
                        {
                            //If the hurt resistance time is under the modified hurt resistance time, set it to the modified hurt resistance time
                            if(entityHit.hurtResistantTime < (float)(entityHit.maxHurtResistantTime) * (0.5) + ((IHitTimeModifier)stack.getItem()).getHitTime(stack, entityHit)){
                                entityHit.hurtResistantTime += ((IHitTimeModifier)stack.getItem()).getHitTime(stack, entityHit);
                            }else{ //if not cancel the attack
                                event.setCanceled(true);
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
    }

    protected boolean performBackStab(Item item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
        //Get victim and murderer vector views at hit time
        double[] victimView = new double[]{entityHit.getLookVec().xCoord,entityHit.getLookVec().zCoord};
        double[] murdererView = new double[]{entityHitting.getLookVec().xCoord,entityHitting.getLookVec().zCoord};
        //back-stab conditions: vectors are closely enough aligned, (fuzzy parameter might need testing)
        //but not in opposite directions (face to face or sideways)
        if(Math.abs(victimView[0]*murdererView[1]-victimView[1]*murdererView[0])<backstabFuzzy &&
                Math.signum(victimView[0])==Math.signum(murdererView[0]) &&
                Math.signum(victimView[1])==Math.signum(murdererView[1])){
            return ((IBackStabbable)item).onBackStab(entityHit, entityHitting);//Perform back stab effect
        }
        return false;
    }

    protected void performEffects(Map<PotionEffect, Float> map, EntityLivingBase entityHit) {
        double roll =  Math.random();
        for(PotionEffect effect:map.keySet()){

            //add effects if they aren't already applied, with corresponding chance factor
            if(!entityHit.isPotionActive(effect.getPotionID()) && map.get(effect) < roll){
                entityHit.addPotionEffect(effect);
            }
        }
    }

    @ForgeSubscribe
    public void onBowFiring(ArrowLooseEvent event) {
    	if(!event.isCanceled()){
    		EntityPlayer player = event.entityPlayer;
    		ItemStack bow = event.bow;
    		//Don't need to do anything in those cases
    		if(player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) > 0){
    			return;
    		}
    		//Check if bow is charged enough
    		int j = event.charge;
			float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;
            if ((double)f < 0.1D)
            {
                return;
            }
            if (f > 1.0F)
            {
                f = 1.0F;
            }
            //Check for IArrowContainer in player main inventory
            ItemStack stack = null;
    		IArrowContainer quiver = null;
    		for(ItemStack item : player.inventory.mainInventory){
    			if(item!=null && item.getItem() instanceof IArrowContainer){
    				stack = item;
    				quiver = (IArrowContainer) item.getItem();
    				break;
    			}
    		}
    		if(quiver != null && quiver.hasArrow(stack)){
    			World world = player.worldObj;
    			Class arrowClazz = quiver.getArrowType(stack);
    			try {//We try to find a constructor close to one of EntityArrow
    				Constructor constructor = arrowClazz.getConstructor(World.class, EntityLivingBase.class, float.class);
					EntityArrow entityarrow = EntityArrow.class.cast(constructor.newInstance(world, player, f * 2.0F));
	                if (f == 1.0F)
	                {
	                    entityarrow.setIsCritical(true);
	                }
	
	                int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);
	
	                if (k > 0)
	                {
	                    entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
	                }
	
	                int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);
	
	                if (l > 0)
	                {
	                    entityarrow.setKnockbackStrength(l);
	                }
	
	                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
	                {
	                    entityarrow.setFire(100);
	                }
	
	                bow.damageItem(1, player);
	                world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
	
	                if (!world.isRemote)
	                {
	                    world.spawnEntityInWorld(entityarrow);
	                }
	                quiver.onArrowFired(world, player, stack, bow);
	                //Canceling the event, since we successfully fired our own arrow
	                event.setCanceled(true);
    			}catch(Exception e)
    			{
    				e.printStackTrace();
    				return;
    			}
    		}
    	}
    }
}
