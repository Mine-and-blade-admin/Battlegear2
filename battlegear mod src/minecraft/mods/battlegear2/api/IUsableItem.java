package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

/**
 * Created by GotoLink on 15/09/2014.
 * The basic signature for {@link net.minecraft.item.Item}s to be used (with GameSettings#keyBindUseItem), basically a counterpart of weapons
 * By default can be hold in either hands of the player, but doesn't allow another usable item on the opposite hand.
 * See {@link IAllowItem} to change this behavior at your discretion.
 *
 * {@link net.minecraft.item.Item#onItemUseFirst(ItemStack, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int, int, float, float, float)}
 * {@link net.minecraft.item.Item#onItemUse(ItemStack, net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, int, int, int, int, float, float, float)}
 * {@link net.minecraft.item.Item#onItemRightClick(ItemStack, net.minecraft.world.World, net.minecraft.entity.player.EntityPlayer)}
 * will be called when player press {@link net.minecraft.client.settings.GameSettings#keyBindUseItem}, no matter what hand wield the {@link ItemStack}
 * exactly as if (or sufficiently close) done by vanilla Minecraft
 *
 * Note: {@link net.minecraft.entity.player.EntityPlayer#getCurrentEquippedItem()} and {@link net.minecraft.entity.player.EntityPlayer#getHeldItem()} will still return the {@link ItemStack} held in right hand,
 * and can therefore be used to differentiate between left and right hand wielding by comparing with the given {@link ItemStack} argument in each method
 *
 * Note: ItemBow, ItemBlock, ItemHoe, ItemPotion, ItemFood and ItemBucket instances are already considered usable, though this implementation has priority
 *
 * Note: For more flexibility over your item usage in left hand
 * @see mods.battlegear2.api.PlayerEventChild.UseOffhandItemEvent
 * @see mods.battlegear2.api.PlayerEventChild.OffhandAttackEvent
 */
public interface IUsableItem {
    /**
     *
     * @param itemStack holding this item
     * @return true if this item prefer being used instead of swinging/attacking
     */
    public boolean isUsedOverAttack(ItemStack itemStack);
}
