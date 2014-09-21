package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IQuiverSelection {

    /**
     * Defines a quiver selection algorithm, to be added with {@link QuiverArrowRegistry#addQuiverSelection}
     * @param bow a possible stack that relates to {@link IArrowContainer2}, not necessarily a {@link ItemBow}
     * @param player
     * @return a stack holding a {@link IArrowContainer2} item or null if none is found to be compatible with
     */
    public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player);
}
