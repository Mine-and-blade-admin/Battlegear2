package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 15/09/2014.
 * The basic signature for {@link net.minecraft.item.Item}s to be used (with GameSettings#keyBindUseItem), basically a counterpart of weapons
 * By default can be hold in either hands of the player, but doesn't allow another usable item on the opposite hand.
 * See {@link IAllowItem} to change this behavior at your discretion (please consider game balance when doing so).
 *
 * {@link net.minecraft.item.Item#onItemUseFirst(ItemStack, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int, int, float, float, float)}
 * {@link net.minecraft.item.Item#onItemUse(ItemStack, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int, int, float, float, float)}
 * {@link net.minecraft.item.Item#onItemRightClick(ItemStack, net.minecraft.world.World, net.minecraft.entity.player.EntityPlayer)}
 * will be called when player press {@link net.minecraft.client.settings.GameSettings#keyBindUseItem}, no matter what hand wield the {@link ItemStack}
 * exactly as if (or sufficiently close) done by vanilla Minecraft
 * Note: {@link net.minecraft.entity.player.EntityPlayer#getCurrentEquippedItem()} is offset to the offhand, and player attributes are accurate with this situation
 *
 * Note:
 * @see {BattlegearUtils#usagePriorAttack(ItemStack, EntityPlayer, boolean)} for commonly usable items, though this implementation has priority
 *
 * Note: For more flexibility over your item usage in left hand
 * @see mods.battlegear2.api.PlayerEventChild.UseOffhandItemEvent
 * @see mods.battlegear2.api.PlayerEventChild.OffhandAttackEvent
 * @see IOffhandListener
 */
public interface IUsableItem {
    /**
     * Note: next version will include wielding player as second argument
     * @param itemStack which contain this item
     * @return true if this item prefer being used instead of swinging/attacking
     */
    public boolean isUsedOverAttack(ItemStack itemStack);
}
