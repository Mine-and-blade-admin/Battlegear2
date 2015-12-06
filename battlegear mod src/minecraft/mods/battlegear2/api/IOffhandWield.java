package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 27/09/2014.
 * To be implemented by items wishing to have select offhand wielding
 * Soon to be deprecated: {@see IWield}
 */
public interface IOffhandWield {
    /**
     * Returns true when this item can be considered for placement into the offhand slots (thus wield in left hand)
     * Note the compatibility will be determined externally against the mainhand item (in right hand), if any.
     * As such, returning true doesn't always guarantee access to left hand slot.
     * Called before {@link IAllowItem#allowOffhand(ItemStack, ItemStack, EntityPlayer)}, if implemented
     *
     * @param offhandStack The {@link ItemStack} which contain this item
     * @param wielder The {@link EntityPlayer} trying to wield this item
     * @return true if this item can be wield in the offhand (left hand)
     */
    public boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder);
}
