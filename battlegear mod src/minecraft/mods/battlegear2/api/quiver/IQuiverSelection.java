package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IQuiverSelection {

    /**
     * Defines a quiver selection algorithm, to be added with {@link QuiverArrowRegistry#addQuiverSelection}
     * @param bow a possible stack that relates to {@link IArrowContainer2}, not necessarily a {@link ItemBow}
     * @param player entity holding this bow
     * @return a stack holding a {@link IArrowContainer2} item or ItemStack.EMPTY if none is found to be compatible with
     */
    ItemStack getQuiverFor(ItemStack bow, EntityPlayer player);
}
