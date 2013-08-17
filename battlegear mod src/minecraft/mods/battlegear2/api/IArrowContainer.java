package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IArrowContainer {

	/**
	 * 
	 * @param stack The {@link #ItemStack} representing this item
	 * @return true if the item contains at least one arrow
	 */
	public boolean hasArrow(ItemStack stack);
	/**
	 * The arrow spawned when bow is used with this non empty container equipped
	 * @param stack The {@link #ItemStack} representing this item
	 * @param charge Amount of charge in the bow, ranging from 0.2F to 2.0F
	 * @param player The {@link #EntityPlayer} using the bow
	 * @param world 
	 * @return the arrow entity to spawn when bow is used
	 */
	public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge);
	/**
	 * Action to take after an arrow has been fired
	 * @param player The {@link #EntityPlayer} using the bow
	 * @param world 
	 * @param stack The {@link #ItemStack} representing this item
	 * @param bow The bow which fired
	 * @param arrow the arrow fired
	 */
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow);
}
