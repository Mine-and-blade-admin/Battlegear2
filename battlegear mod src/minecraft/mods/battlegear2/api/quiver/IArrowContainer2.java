package mods.battlegear2.api.quiver;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * To implement in Item instances that manage arrows
 * Is by default wieldable in both hands in "battlemode"
 * @see IAllowItem to change this behavior
 */
public interface IArrowContainer2 {

    /**
     * Returns the maximum amount of slots in the arrow container
     * @param container {@link ItemStack} representing this item
     * @return the amount of slots
     */
    int getSlotCount(ItemStack container);

    /**
     * Returns the currently selected slot in the arrow container
     * @param container {@link ItemStack} representing this item
     * @return the currently selected slot
     */
    int getSelectedSlot(ItemStack container);

    /**
     * Sets the currently selected slot to the given value
     * @param container {@link ItemStack} representing this item
     * @param newSlot the new slot index
     */
    void setSelectedSlot(ItemStack container, int newSlot);

    /**
     * Returns the itemStack in the currently selected slot
     * @param container The {@link ItemStack} representing this item
     * @param slot the slot index
     * @return The {@link ItemStack} in the given slot.
     */
    ItemStack getStackInSlot(ItemStack container, int slot);

    /**
     * Sets places the given item stack in the given slot
     * @param container {@link ItemStack} representing this item
     * @param slot the slot index
     * @param stack {@link ItemStack} representing the new stack
     */
    void setStackInSlot(ItemStack container, int slot, ItemStack stack);

    /**
     *
     * @param container The {@link ItemStack} representing this item
     * @param bow The bow trying to use this container
     * @param player The {@link EntityPlayer} using the bow
     * @return true if the item contains at least one arrow in the selected slot
     */
    boolean hasArrowFor(ItemStack container, ItemStack bow, EntityPlayer player, int slot);

    /**
     * The arrow spawned when bow is used with this non empty container equipped
     * @param container The {@link ItemStack} representing this item
     * @param charge Amount of charge in the bow, ranging from 0.2F to 2.0F
     * @param player The {@link EntityPlayer} using the bow
     * @param world The world in which the arrow would spawn
     * @return the arrow entity to spawn when bow is used
     */
    EntityArrow getArrowType(ItemStack container, World world, EntityPlayer player, float charge);

    /**
     * Action to take after an arrow has been fired
     * Usually equal to removing an arrow from the container
     * @param player The {@link EntityPlayer} using the bow
     * @param world The world in which the arrow has spawned
     * @param container The {@link ItemStack} representing this item
     * @param bow The bow which fired
     * @param arrow the arrow fired
     */
    void onArrowFired(World world, EntityPlayer player, ItemStack container, ItemStack bow, EntityArrow arrow);

    /**
     * Called before the arrow is fired from this container
     * @param arrowEvent Used to decide bow damage, bow sound and arrow enchantment
     */
    void onPreArrowFired(PlayerEventChild.QuiverArrowEvent.Firing arrowEvent);

    /**
     * Called when the container is put on a crafting bench with other items
     * @param container The {@link ItemStack} representing this item
     * @param arrowStack The {@link ItemStack} representing other items
     * @return True to receive {@link #addArrows(ItemStack, ItemStack)}
     */
    boolean isCraftableWithArrows(ItemStack container, ItemStack arrowStack);

    /**
     * Crafts the item with the items from {@link #isCraftableWithArrows(ItemStack, ItemStack)}
     * @param container The {@link ItemStack} representing this item
     * @param newStack Another valid item on the crafting bench
     * @return Arrows that couldn't fit in
     */
    ItemStack addArrows(ItemStack container, ItemStack newStack);

    /**
     * Called through post rendering event on the player
     * @param container The {@link ItemStack} representing this item
     * @return true if the default quiver model can be rendered
     */
    boolean renderDefaultQuiverModel(ItemStack container);

}
