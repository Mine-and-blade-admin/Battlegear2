package mods.battlegear2.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface ISpecialEffect {
	/**
	 * 
	 * @param entityHit The entity the effect will be applied to.
	 * @param entityHitting 
	 * @return The array of {@link net.minecraft.potion.PotionEffect} that will be dealt to the entityHit
	 */
	public PotionEffect[] getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
