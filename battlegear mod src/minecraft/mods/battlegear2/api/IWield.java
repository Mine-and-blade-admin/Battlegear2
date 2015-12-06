package mods.battlegear2.api;

import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 06/12/2015.
 * To be implemented by items wishing to have select wielding
 * Only apply for dual wielding slots, not Minecraft hotbar slots
 */
public interface IWield {
    /**
     * Define which hand this item can be wield with
     * BOTH: Symmetrical handling (default for most items)
     * RIGHT: The main hand (exclusively right-handed)
     * LEFT: The off hand (exclusively left-handed)
     * Note the compatibility will be determined externally against the opposite hand item, if any.
     * As such, returning a side doesn't always guarantee access to corresponding hand slot.
     * Called before {@link IAllowItem#allowOffhand(ItemStack, ItemStack, EntityPlayer)}, if implemented
     *
     * @param itemStack contain this item (not null)
     * @param player trying to wield this item
     * @return the wielding hand
     */
    WeaponRegistry.Wield getWieldStyle(ItemStack itemStack, EntityPlayer player);
}
