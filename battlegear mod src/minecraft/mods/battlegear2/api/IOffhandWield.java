package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 27/09/2014.
 * To be implemented by items wishing to have select offhand wielding - NOOP
 */
public interface IOffhandWield {
    /**
     * Returns true if this item can be added to the offhand slots (thus wield in left hand),
     * though the compatibility will be determined against the mainhand item (in right hand), if any
     * @param offhandStack The {@link ItemStack} holding this item
     */
    public boolean isOffhandWieldable(ItemStack offhandStack);
}
