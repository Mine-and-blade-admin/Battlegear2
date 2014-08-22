package zeldaswordskills.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * 
 * This interface allows custom arrows to determine whether they may be fired
 * by a particular bow and / or player, such that an arrow may be added to the
 * QuiverArrowRegistry and used with the default ItemQuiver without also being
 * forced to allow all bows to use it.
 * 
 * @author coolAlias
 *
 */
public interface ISpecialArrow {

	/**
	 * Determine whether this arrow is usable by this player using this bow
	 * @return	False to prevent the player/bow from nocking/firing this arrow
	 */
	public boolean isUsableBy(ItemStack arrow, ItemStack bow, EntityPlayer player);

}
