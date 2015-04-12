package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

/**
 * Implement into "right hand" Item instances to define possible combination of items wield
 *
 */
public interface IAllowItem {

	/**
     * Returns true if this mainhand {@link ItemStack} allows the offhand {@link ItemStack} to be placed in the partner offhand slot
     */
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand);
}
