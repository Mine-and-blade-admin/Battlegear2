package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Defines a firing process, to be registered by {@link QuiverArrowRegistry#addArrowFireHandler(IArrowFireHandler)},
 * This will enable the default battlegear ItemQuiver to be crafted with, and fire said item with a compatible bow
 */
public interface IArrowFireHandler {

    /**
     * Called from QuiverArrowRegistry.getArrowType,
     * to decide if it is worth trying to build an EntityArrow
     * @param arrow the stack which should define the arrow as item
     * @param world
     * @param player player using a bow to fire an arrow
     * @param charge amount of charge in the bow
     * @return true to get called for #getFireArrow
     */
    public boolean canFireArrow(ItemStack arrow, World world, EntityPlayer player, float charge);

    /**
     * Called from QuiverArrowRegistry.getArrowType,
     * return null if the EntityArrow couldn't be built,
     * let pass to another IArrowFireHandler
     * @param arrow the stack which should define the arrow as item
     * @param world
     * @param player player using a bow to fire an arrow
     * @param charge amount of charge in the bow
     * @return the arrow to fire, or null if it couldn't be built
     */
    public EntityArrow getFiredArrow(ItemStack arrow, World world, EntityPlayer player, float charge);
}
