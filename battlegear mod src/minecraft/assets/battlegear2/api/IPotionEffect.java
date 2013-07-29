package assets.battlegear2.api;

import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public interface IPotionEffect {
	/**
	 * 
	 * @param entityHit The entity the effect will be applied to.
	 * @param entityHitting 
	 * @return A Map of {@link PotionEffect} with chance value ranging from 0 to 100, to be dealt to the entityHit
	 */
	public Map<PotionEffect,Short> getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting);
	
}
