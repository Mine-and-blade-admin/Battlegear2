package mods.battlegear2.api.shield;

import net.minecraft.item.ItemStack;

/**
 * Defines an {@link Item} that can hold arrows as an internal variable
 * Used by {@link ItemShield} to display blocked arrows
 * @author GotoLink
 *
 */
public interface IArrowDisplay {
    void setArrowCount(ItemStack stack, int count);
    int getArrowCount(ItemStack stack);
}
