package mods.battlegear2.api.quiver;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 *
 * This interface gives custom {@link ItemBow}s greater control over the default Battlegear2 {@link ItemQuiver} behavior;
 * specifically, it allows bows to determine whether they may be drawn during the {@link ArrowNockEvent} and to
 * return custom lists of {@link IArrowFireHandler} during {@link QuiverArrowRegistry#getArrowType}.
 *
 * Those methods are solely for customizing behavior using the default ItemQuiver: custom
 * quiver implementations may or may not call these.
 *
 * Note: Can also serve as a flag for a custom {@link Item} to have a "bow" behavior, without extending {@link ItemBow},
 * that is,
 * -being detected by default {@link ItemQuiver}
 * -being rendered back-sheathed, though {@link ISheathed} can change this
 * -work with bow {@link BaseEnchantment} though {@link IEnchantable} can change this
 * -is used in priority, instead of attacking, though {@link IUsableItem} can change this
 *
 * @author coolAlias
 *
 */
public interface ISpecialBow {

  /**
   * Allows a bow to define the order and contents of the {@link IArrowFireHandler} list used when getting an
   * arrow type with {@link QuiverArrowRegistry#getArrowType}, which is used by the default quiver.
   * Other {@link IArrowContainer2 quiver} implementations may or may not call this method at their discretion.
   *
   * @param arrow   The arrow attempting to be fired
   * @param bow     The stack containing the instance of ISpecialBow being used to fire the arrow
   * @param player  The player firing the bow
   * @return A list of fire handlers to be used by {@link QuiverArrowRegistry#getArrowType} to be iterated through in the order of entry (FIFO)
   *  Returning null will cause the default fire handlers to be used.
   *  Returning an empty list will prevent any fire handlers from processing, effectively rendering the bow useless.
   */
  public List<IArrowFireHandler> getFireHandlers(ItemStack arrow, ItemStack bow, EntityPlayer player);

  /**
   * Allows the custom bow to determine if it may be nocked or not during the {@link ArrowNockEvent}.
   * This is almost entirely cosmetic, as a player using a quiver may hot-swap to any arrow,
   * but allows modders to signal immediately that the bow may not be fired in its current state.
   *
   * Note that this is before any arrow is determined by the nock event, so each implementation
   * must determine if an appropriate arrow is available or not, whether via the current quiver
   * item or some other inventory-searching algorithm.
   * 
   * If the nock event is ALLOWed or the DEFAULT behavior determines that it should be allowed,
   * then the bow will be set in use automatically.
   *
   * @param bow The stack containing the instance of ISpecialBow attempting to nock an arrow
   * @param player The player attempting to use the bow
   * @return Result.DEFAULT to use generic nocking behavior from BG2;
   * Result.DENY to prevent the bow from being nocked;
   * Result.ALLOW to allow the bow to be nocked, ignoring the
   * default BG2 nocking behavior.
   */
  public Result canDrawBow(ItemStack bow, EntityPlayer player);

}
