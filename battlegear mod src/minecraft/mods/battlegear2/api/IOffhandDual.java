package mods.battlegear2.api;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IOffhandDual {
	/**
     * Returns true if this item can be dual wielded in the offhand slot
     * @deprecated See {@link IOffhandWield#isOffhandWieldable(ItemStack, EntityPlayer)}
     * @param off The {@link ItemStack} holding this item
     */
    public boolean isOffhandHandDual(ItemStack off);
    
    /**
     * Perform any function when this item is held in the offhand (left) and the user right clicks an entity.
     * This is generally used to apply an effect on attacking the entity with the offhand item.
     * If this is the case the {@link PlayerEventChild.OffhandAttackEvent#cancelParent} field should be left at true,
     * and the event should NOT be cancelled, to prevent default right clicking events:
     * (Eg PlayerInteractEvent, Item#onItemRightClick) being called for the item in right hand.
     * Leave {@link PlayerEventChild.OffhandAttackEvent#shouldAttack} field at true,
     * to perform the "vanilla" attack procedure and receive
     * #onLeftClickEntity(ItemStack, EntityPlayer, Entity) in addition.
     * Interactions with the entity have already been checked out, and cancelling the given event will only let the
     * right hand item be used.
     *
     * @deprecated See {@link IOffhandListener#onAttackEntity(PlayerEventChild.OffhandAttackEvent)}
     * @param event        the OffhandAttackEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandAttackEntity(PlayerEventChild.OffhandAttackEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks "Air".
     * Note: Called first on client-side, then on server side if {@link PlayerEventChild.UseOffhandItemEvent} is not cancelled and offhandItem is not null,
     * following Forge rules for PlayerInteractEvent with Action==RIGHT_CLICK_AIR
     * Note: PlayerInteractEvent is already a shallow copy
     * @deprecated See {@link IOffhandListener#onClickAir(EntityPlayer, ItemStack, ItemStack)} for better control
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandClickAir(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    /**
     * Perform any function when this item is held in the offhand and the user right clicks a block.
     * Note: this will happen prior to the activation of any functions of blocks
     * Note: Called first on client-side, then on server side if {@link UseOffhandItemEvent} is not cancelled
     * Note: {@link PlayerInteractEvent#useItem} is already set on {@link Event.Result#DENY} before reaching this method,
     * in order to avoid mainhandItem usage
     *
     * @deprecated See {@link IHandListener#onClickBlock(PlayerInteractEvent, ItemStack, ItemStack, boolean)}
     * @param event        the PlayerInteractEvent that was generated
     * @param mainhandItem the {@link ItemStack} currently being held in the right hand
     * @param offhandItem  the {@link ItemStack} currently being held in the left hand, holding this item
     * @return true if the off hand swing animation should be performed
     */
    public boolean offhandClickBlock(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

    @SuppressWarnings("unused")
    /**
     * No-OP
     * @See {Item#onUpdate(ItemStack, World, Entity, int, boolean)}
     */
    public void performPassiveEffects(Side effectiveSide, ItemStack mainhandItem, ItemStack offhandItem);
}
