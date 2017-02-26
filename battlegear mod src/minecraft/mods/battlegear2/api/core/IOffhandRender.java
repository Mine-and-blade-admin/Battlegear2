package mods.battlegear2.api.core;

import net.minecraft.item.ItemStack;

/**
 * Interface added to {@link ItemRenderer} to support offhand rendering
 * Note that, they only provide access to added fields for the offhand, NOT the fields for the mainhand
 * @author GotoLink
 */
public interface IOffhandRender {

    ItemStack getItemToRender();

    void setItemToRender(ItemStack item);

    int getEquippedItemSlot();

    void setEquippedItemSlot(int slot);

    float getEquippedProgress();

    void setEquippedProgress(float progress);

    float getPrevEquippedProgress();

    void setPrevEquippedProgress(float progress);

}
