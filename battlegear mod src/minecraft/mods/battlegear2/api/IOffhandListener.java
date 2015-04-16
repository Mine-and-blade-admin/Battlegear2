package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 27/09/2014.
 * To be implemented by items wishing to receive common events for the offhand (left hand) without having to register listener classes
 */
public interface IOffhandListener extends IHandListener{
    /**
     * Perform any function when this item is held in either hand (depends on {@code isOffhand}) and the user right clicks an entity.
     * This is generally used to apply an effect on attacking the entity with the offhand item.
     * If this is the case the {@link PlayerEventChild.OffhandAttackEvent#cancelParent} field should be left at true,
     * and the event should NOT be cancelled, to prevent default right clicking events:
     * (Eg PlayerInteractEvent, Item#onItemRightClick) being called for the item in right hand.
     * Leave {@link PlayerEventChild.OffhandAttackEvent#shouldAttack} field at true,
     * to perform the "vanilla" attack procedure with the offhand item and receive
     * #onLeftClickEntity(ItemStack, EntityPlayer, Entity) in addition.
     * Interactions with the entity have already been checked out, and cancelling the given event will only let the
     * right hand item try to be "used".
     * The event may already have been cancelled, if this item is in the mainhand, and the offhand item cancelled it.
     *
     * @param event the OffhandAttackEvent that was generated
     * @param isOffhand True if this item is actually in the offhand slot, False otherwise
     */
    public void onAttackEntity(PlayerEventChild.OffhandAttackEvent event, boolean isOffhand);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks "Air".
     * Called after the method above, if the default right clicking events were successfully cancelled,
     * or the item in the main (right) hand couldn't be used.
     *
     * @param player the player entity
     * @param mainhandStack the {@link ItemStack} currently being held in the right hand, can be null
     * @param offhandStack  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return ALLOW to get the call on server side, but not perform the swing animation, DENY to prevent both, DEFAULT to get both
     */
    public Event.Result onClickAir(EntityPlayer player, ItemStack mainhandStack, ItemStack offhandStack);

}
