package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

//This is a tempory fix until we get the heraldry system up and running
public interface IDyable {

    /**
     * Return whether the specified ItemStack has a color.
     */
    boolean hasColor(ItemStack par1ItemStack);

    /**
     * Return the color for the specified ItemStack.
     */
    int getColor(ItemStack par1ItemStack);

    void setColor(ItemStack dyable, int rgb);

    /**
     * Remove the color from the specified ItemStack.
     */
    void removeColor(ItemStack par1ItemStack);

    int getDefaultColor(ItemStack par1ItemStack);

}
