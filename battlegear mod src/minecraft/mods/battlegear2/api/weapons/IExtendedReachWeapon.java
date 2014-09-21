package mods.battlegear2.api.weapons;

import net.minecraft.item.ItemStack;

public interface IExtendedReachWeapon {
	/**
	 * The distance the weapon will hit
     * Note: a positive value, ie more reach, will <strong>only</strong> work for main hand weapons
	 * @param stack
	 * @return
	 */
	public float getReachModifierInBlocks(ItemStack stack);
}
