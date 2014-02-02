package mods.battlegear2.api.shield;

import net.minecraft.item.ItemStack;
/**
 * Defines an item that can hold arrows has an internal variable
 * Used by ItemShield to display blocked arrows to an extent
 * @author GotoLink
 *
 */
public interface IArrowCatcher {
	public void setArrowCount(ItemStack stack, int count);
	public int getArrowCount(ItemStack stack);
}
