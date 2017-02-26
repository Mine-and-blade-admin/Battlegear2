package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Implement into any Item instances to define possible combination of items wield
 */
public interface IAllowItem {

	/**
     * Whether this item {inhand, not necessarily the player right hand} allows the other (opposite hand) to be wielded simultaneously
	 * Called before both items are in place (though one can already be).
	 * Note also applies for empty hands (null arg).
	 * Note it is not necessary for the opposite hand item to implement this interface for this contract to be obeyed
	 * @param inhand this item (not null)
	 * @param oppositehand the other item (can be null if empty hand, can be of the same item instance, etc)
	 * @param player the player entity trying to wield (placing each item in dual slots)
	 * @return {
	 * false:
	 * If this item is already placed, cancel the other item placement (leave opposite hand in previous accepted state)
	 * If this item is not yet placed but the opposite is (or null), cancel this item own placement;
	 * true:
	 * If this item is already placed, allow the other item placement
	 * If this item is not yet placed but the opposite is (or null), allow this item own placement
	 * }
     */
    boolean allowOffhand(ItemStack inhand, ItemStack oppositehand, EntityPlayer player);
}
