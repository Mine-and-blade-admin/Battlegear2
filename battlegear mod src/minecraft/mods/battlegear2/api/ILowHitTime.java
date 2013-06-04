package mods.battlegear2.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

public interface ILowHitTime {
	/**
	 * 
	 * @param target 
	 * @return The duration of the hit state. 
	 */
	public int getHitTime(ItemStack stack, EntityLiving target);

}
