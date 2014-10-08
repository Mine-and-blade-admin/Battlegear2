package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by GotoLink on 29/09/2014.
 * To be implemented by items wishing to receive click for the wielding hand without having to register listener classes - NOOP
 */
public interface IHandListener {
    /**
     * Perform any function when this item is held and the user clicks a block.
     * @param event a shallow copy of the interaction event that triggered,
     *              {@link net.minecraftforge.event.entity.player.PlayerInteractEvent#useItem} still decide whether to use the mainhand {@link net.minecraft.item.ItemStack} but is DENY by default
     *              cancelling this will not prevent the server side call, but will prevent block activation
     * @param mainhandStack content of the mainhand slot (right hand)
     * @param offhandStack content of the offhand slot (left hand)
     * @param isOffhand true if this item is in the offhand slot, false if it is in the mainhand slot
     * @return ALLOW to get the call on server side, but not perform the swing animation, DENY to prevent both, DEFAULT to get both
     */
    public Event.Result onClickBlock(PlayerInteractEvent event, ItemStack mainhandStack, ItemStack offhandStack, boolean isOffhand);
}
