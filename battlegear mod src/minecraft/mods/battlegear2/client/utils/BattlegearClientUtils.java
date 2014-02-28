package mods.battlegear2.client.utils;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;

public class BattlegearClientUtils {

    public static boolean entityOtherPlayerIsItemInUseHook(EntityOtherPlayerMP player, boolean isItemInUse){
        ItemStack mainhand = player.getCurrentEquippedItem();
        if (!isItemInUse && player.isEating() && mainhand != null){
            //ItemStack itemstack = player.inventory.mainInventory[player.inventory.currentItem];
            player.setItemInUse(mainhand, mainhand.getMaxItemUseDuration());
            return true;
        }
        else if (isItemInUse && !player.isEating()){
            player.clearItemInUse();
            return false;
        }else{
            return isItemInUse;
        }
    }
}
