package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 15/09/2014.
 * The basic signature for {@link net.minecraft.item.Item}s to be used (with GameSettings#keyBindUseItem), basically a counterpart of weapons
 * By default can be hold in either hands of the player, but doesn't allow another usable item on the opposite hand.
 * See {@link IAllowItem} to change this behavior at your discretion (please consider game balance when doing so).
 *
 * {@link net.minecraft.item.Item#onItemUseFirst(EntityPlayer, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.util.EnumFacing, float, float, float, net.minecraft.util.EnumHand)}
 * {@link net.minecraft.item.Item#onItemUse(EntityPlayer, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.util.EnumHand, net.minecraft.util.EnumFacing, float, float, float)}
 * {@link net.minecraft.item.Item#onItemRightClick(net.minecraft.world.World, EntityPlayer, net.minecraft.util.EnumHand)}
 * will be called when player press {@link net.minecraft.client.settings.GameSettings#keyBindUseItem}, depending on which hand wields the {@link ItemStack} as done by vanilla Minecraft
 *
 * Note:
 * {@link mods.battlegear2.api.core.BattlegearUtils#usagePriorAttack(ItemStack, EntityPlayer, boolean)} for commonly usable items, though this implementation has priority
 *
 * Note: For more flexibility over your item usage in left hand
 * @see mods.battlegear2.api.PlayerEventChild.OffhandAttackEvent
 * @see IOffhandListener
 */
public interface IUsableItem {
    /**
     * @param player  wielding player
     * @param itemStack which contain this item
     * @return true if this item prefer being used instead of swinging/attacking
     */
    boolean isUsedOverAttack(ItemStack itemStack, EntityPlayer player);
}
