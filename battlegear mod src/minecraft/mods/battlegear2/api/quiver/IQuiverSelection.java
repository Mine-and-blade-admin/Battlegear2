package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IQuiverSelection {

    /**
     * Defines a quiver selection algorithm, to be added with {@link #QuiverArrowRegistry.addQuiverSelection}
     * @param bow
     * @param player
     * @return a stack holding a {@link #IArrowContainer2} item or null if none is found
     */
    public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player);
}
