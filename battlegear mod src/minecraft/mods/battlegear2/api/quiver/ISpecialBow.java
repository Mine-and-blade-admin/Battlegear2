package battlegear2.api.quiver;

import java.util.List;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.IArrowFireHandler;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event.Result;

/**
 * 
 * This interface provides custom ItemBows with several methods that allow customization
 * of the default Battlegear2 ItemQuiver behavior, such as determining whether an arrow
 * may be nocked during the ArrowNockEvent or designating IArrowFireHandlers different
 * from those registered to the QuiverArrowRegistry, or even simply re-ordered so the
 * default handler is not used first.
 * 
 * The purpose is solely for customizing behavior using the default ItemQuiver: custom
 * quiver implementations are free to do whatever they please.
 * 
 * @author coolAlias
 *
 */
public interface ISpecialBow {

	/**
	 * Allows a bow to circumvent the default {@link IArrowFireHandler} by returning a customized list of IArrowFireHandlers.
	 * Note that custom implementations of {@link IArrowContainer2} may define their own algorithms, possibly
	 * circumventing this method entirely.
	 * @param arrow	Perhaps unnecessary?
	 * @return	A list of fire handlers to be used by {@link QuiverArrowRegistry#getArrowType};
	 *			Returning null will cause the default fire handlers are used.
	 *			Returning an empty list will prevent any fire handlers from processing, effectively rendering the bow useless.
	 */
	public List<IArrowFireHandler> getFireHandlers(ItemStack arrow, ItemStack bow, EntityPlayer player);

	/**
	 * Allows the custom bow to determine if it may be nocked or not during the ArrowNockEvent.
	 * If the nock event is ALLOWed or the DEFAULT behavior determines that it should be allowed,
	 * then the bow will be set in use automatically.
	 * 
	 * @param bow		The ISpecialBow stack attempting to be nocked
	 * @param player	The player attempting to use the bow
	 * @return	Result.DEFAULT to use generic nocking behavior from BG2;
	 * 			Result.DENY to prevent the bow from being nocked;
	 * 			Result.ALLOW to allow the bow to be nocked, ignoring the
	 * 				default BG2 nocking behavior.
	 */
	public Result nockArrow(ItemStack bow, EntityPlayer player);

}
