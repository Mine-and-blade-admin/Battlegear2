package mods.battlegear2.client.utils;

import mods.battlegear2.api.RenderPlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

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
            if(offhand!=null && BattlegearUtils.usagePriorAttack(offhand, player, true))
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

    /**
     * Flip the sheathed item
     * @param stack
     */
    private void onRenderSheathedItem(ItemStack stack){
        if (stack.getItem() instanceof ItemBucket || stack.getItem() instanceof ItemPotion || stack.getItem() instanceof IArrowContainer2) {
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOffhandItem(RenderPlayerEventChild.PreRenderPlayerElement preRender){
        if(preRender.element!=null) {
            if (preRender instanceof RenderPlayerEventChild.PreRenderSheathed) {
                onRenderSheathedItem(preRender.element);
            } else if(preRender.element.getItem() instanceof IArrowContainer2 && ((IArrowContainer2) preRender.element.getItem()).renderDefaultQuiverModel(preRender.element)){

                if (Arrays.binarySearch(BattlegearConfig.disabledRenderers, "quiver") < 0) {
                    ItemStack quiverStack = QuiverArrowRegistry.getArrowContainer(preRender.entityPlayer);
                    if (preRender.element == quiverStack) {
                        preRender.setCanceled(true);
                    }
                }
            }
        }
    }
}
