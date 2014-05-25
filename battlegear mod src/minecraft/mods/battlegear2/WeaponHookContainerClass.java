package mods.battlegear2;

import java.util.Map;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.weapons.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * User: nerd-boy
 * Date: 30/07/13
 * Time: 12:36 PM
 * Events registered with MinecraftForge event bus on default priority: 
 * LivingAttackEvent, to perform weapons custom effects
 */
public class WeaponHookContainerClass {

	public static final float backstabFuzzy = 0.01F;
    @SubscribeEvent
    public void onAttack(LivingAttackEvent event){

    	if(event.entityLiving instanceof EntityPlayer && ((EntityPlayer)event.entityLiving).capabilities.isCreativeMode)
    	{
    		return;//Fix vanilla bug with baby zombies being able to lead mobs to attack player
    		//in creative mode thus calling the event
    	}
        EntityLivingBase entityHit = event.entityLiving;
        //Record the hurt times
        int hurtTimeTemp = entityHit.hurtTime;
        int hurtResistanceTimeTemp = entityHit.hurtResistantTime;
        if(event.source instanceof EntityDamageSource && !event.source.damageType.startsWith(Battlegear.CUSTOM_DAMAGE_SOURCE) &&
                !(event.source instanceof EntityDamageSourceIndirect) )
        {
            Entity attacker = event.source.getEntity();
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

    protected static boolean performBackStab(Item item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
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

    protected static void performEffects(Map<PotionEffect, Float> map, EntityLivingBase entityHit) {
        double roll = Math.random();
        for(PotionEffect effect:map.keySet()){
            //add effects if they aren't already applied, with corresponding chance factor
            if(!entityHit.isPotionActive(effect.getPotionID()) && map.get(effect) > roll){
                entityHit.addPotionEffect(new PotionEffect(effect));
            }
        }
    }
}
