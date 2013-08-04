package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IArrowContainer {

	/**
	 * 
	 * @param stack
	 * @return true if the item contains at least one arrow
	 */
	public boolean hasArrow(ItemStack stack);
	/**
	 * This class should at least contain a public constructor with (World,EntityLivingBase,float) as args
	 * @param stack
	 * @return the class of Arrows to spawn when bow is fired
	 */
	public Class<? extends EntityArrow> getArrowType(ItemStack stack);
	/**
	 * Action to take after an arrow has been fired
	 * @param player The player using the bow
	 * @param world 
	 * @param stack the stack contained this item
	 * @param bow the bow which fired
	 */
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow);
}
