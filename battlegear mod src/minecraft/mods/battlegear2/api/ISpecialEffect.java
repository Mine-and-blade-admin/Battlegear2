package mods.battlegear2.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface ISpecialEffect {
	/**
	 * 
	 * @param item The {@link ItemStack} representative of the item dealing the effect.
	 * @param entityHit The entity the effect will be applied to.
	 * @return The array of {@link PotionEffect} that will be dealt to the entityHit
	 */
	public PotionEffect[] getEffectsOnHit(ItemStack item, EntityLiving entityHit);
}
