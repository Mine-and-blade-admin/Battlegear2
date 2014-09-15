package mods.battlegear2.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public interface IEnchantable {

	/**
	 * If a Battlegear {@link BaseEnchantment} can be applied to this item, given the {@link ItemStack}
	 * @param baseEnchantment 
	 * @param stack holding this item, trying to be enchanted
	 * @return true to continue applying the enchantment
	 */
	public boolean isEnchantable(Enchantment baseEnchantment, ItemStack stack);
}
