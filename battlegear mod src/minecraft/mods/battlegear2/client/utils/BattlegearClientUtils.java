package mods.battlegear2.client.utils;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.RenderPlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public final class BattlegearClientUtils {
    /**
     * Patch over EntityOtherPlayerMP#onUpdate() to update isItemInUse field
     * @param player the player whose #onUpdate method is triggered
     * @param isItemInUse the old value for isItemInUse field
     * @return the new value for isItemInUse field
     */
    public static boolean entityOtherPlayerIsItemInUseHook(EntityOtherPlayerMP player, boolean isItemInUse){
        ItemStack itemStack = player.getCurrentEquippedItem();
        if(BattlegearUtils.isPlayerInBattlemode(player)){
            ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
            if(offhand!=null && BattlegearUtils.usagePriorAttack(offhand))
                itemStack = offhand;
        }
        if (!isItemInUse && player.isEating() && itemStack != null){
            player.setItemInUse(itemStack, itemStack.getMaxItemUseDuration());
            return true;
        }else if (isItemInUse && !player.isEating()){
            player.clearItemInUse();
            return false;
        }else{
            return isItemInUse;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOffhandItem(RenderPlayerEventChild.PreRenderSheathed preRender){
        if(preRender.element!=null) {
            if (preRender.element.getItem() instanceof ItemBucket || preRender.element.getItem() instanceof ItemPotion) {
                flip(0.8F);
            }
        }
    }

    public static void flip(float scale){
        GL11.glScalef(scale, -scale, scale);
        GL11.glTranslatef(0, -1, 0);
    }
}
