package mods.battlegear2.api.core;

import net.minecraft.item.ItemStack;

/**
 * Interface added to {@link ItemRenderer} to support offhand rendering
 * Note that, they only provide access to added fields for the offhand, NOT the fields for the mainhand
 * @author GotoLink
 */
public interface IOffhandRender {

    public ItemStack getItemToRender();

    public void setItemToRender(ItemStack item);

    public int getEquippedItemSlot();

    public void setEquippedItemSlot(int slot);

    public float getEquippedProgress();

    public void setEquippedProgress(float progress);

    public float getPrevEquippedProgress();

    public void setPrevEquippedProgress(float progress);

}
