package mods.battlegear2.api;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

/**
 * To be registered in net.minecraft.client.renderer.color.ItemColors for IDyable item instances
 */
public final class Colorable implements IItemColor {
    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        return tintIndex > 0 ? -1 :((IDyable)stack.getItem()).getColor(stack);
    }
}
