package mods.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPenetrateWeapon {
	/**
	 * The amount of damage bypassing armor
	 * @param stack The {@link net.minecraft.item.ItemStack} representative of the item dealing the hit.
	 * @return
	 */
	public int getPenetratingPower(ItemStack stack);
}
