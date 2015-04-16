package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by GotoLink on 29/09/2014.
 * To be implemented by items wishing to receive click for the wielding hand without having to register listener classes
 */
public interface IHandListener {
    /**
     * Perform any function when this item is held and the user clicks a block in battlemode.
     *
     * @param event A shallow copy of the interaction event that triggered
     *              Cancelling the event will not prevent the server side call, but will prevent block activation.
     *              WHEN isOffhand is TRUE,
     *                  {@link PlayerInteractEvent#useItem} decide whether to use the offhandStack,
     *                  while the opposite (right) hand item interaction has been cancelled.
     *              WHEN isOffhand is FALSE,
     *                  {@link PlayerInteractEvent#useItem} decide whether to use the mainhandStack but is DENY by default
     *
     * @param mainhandStack content of the mainhand slot (right hand)
     * @param offhandStack content of the offhand slot (left hand)
     * @param isOffhand true if this item is in the offhand slot, false if it is in the mainhand slot
     * @return ALLOW to get the call on server side, but not perform the swing animation, DENY to prevent both, DEFAULT to get both
     */
    public Event.Result onClickBlock(PlayerInteractEvent event, ItemStack mainhandStack, ItemStack offhandStack, boolean isOffhand);
}
