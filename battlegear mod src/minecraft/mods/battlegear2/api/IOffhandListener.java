package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 27/09/2014.
 * To be implemented by items wishing to receive common events for the offhand (left hand) without having to register listener classes - NOOP
 * TODO: Deprecate IOffhandDual similar methods
 */
public interface IOffhandListener extends IHandListener{
    /**
     * Perform any function when this item is held in the offhand and the user right clicks an entity.
     * This is generally used to attack an entity with the offhand item.
     * If this is the case the {@link PlayerEventChild.OffhandAttackEvent#parent} field should be canceled
     * (or {@link PlayerEventChild.OffhandAttackEvent#cancelParent} field left at true, to prevent any default right clicking events (Eg Villager Trading)
     *
     * @param event the OffhandAttackEvent that was generated
     * @return ALLOW to get the call on server side, but not perform the swing animation, DENY to prevent both, DEFAULT to get both
     */
    public Event.Result onAttackEntity(PlayerEventChild.OffhandAttackEvent event);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks "Air".
     * @param player the player entity
     * @param mainhandStack the {@link ItemStack} currently being held in the right hand, can be null
     * @param offhandStack  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return ALLOW to get the call on server side, but not perform the swing animation, DENY to prevent both, DEFAULT to get both
     */
    public Event.Result onClickAir(EntityPlayer player, ItemStack mainhandStack, ItemStack offhandStack);

}
