package mods.battlegear2.client.utils;

import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.RenderPlayerEventChild;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class BattlegearClientUtils {

    /**
     * Flip the sheathed item
     * @param stack
     */
    private void onRenderSheathedItem(ItemStack stack){
        if (stack.getItem() instanceof ItemBucket || stack.getItem() instanceof ItemPotion || stack.getItem() instanceof IArrowContainer2) {
            GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderOffhandItem(RenderPlayerEventChild.PreRenderSheathed preRender){
        if(preRender.element != null) {
            onRenderSheathedItem(preRender.element);
        }
    }

    /**
     * Offset battle slots rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderBar(RenderItemBarEvent.BattleSlots event) {
        if(!event.isMainHand){
            event.xOffset += BattlegearConfig.battleBarOffset[0];
            event.yOffset += BattlegearConfig.battleBarOffset[1];
        }else{
            event.xOffset += BattlegearConfig.battleBarOffset[2];
            event.yOffset += BattlegearConfig.battleBarOffset[3];
        }
    }

    /**
     * Offset quiver slots rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderQuiver(RenderItemBarEvent.QuiverSlots event) {
        event.xOffset += BattlegearConfig.quiverBarOffset[0];
        event.yOffset += BattlegearConfig.quiverBarOffset[1];
    }

    /**
     * Offset shield stamina rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderShield(RenderItemBarEvent.ShieldBar event) {
        event.xOffset += BattlegearConfig.shieldBarOffset[0];
        event.yOffset += BattlegearConfig.shieldBarOffset[1];
    }
}
