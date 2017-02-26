package mods.battlegear2.client.renderer;

import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.client.BattlegearClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;

/**
 * Render a quiver on player back
 */
public class LayerQuiver extends LayerPlayerBase{
    public LayerQuiver(RenderPlayer playerRenderer)
    {
        super(playerRenderer);
    }

    @Override
    protected void doRender(EntityPlayer player, float partialTicks, float scale) {
        ItemStack quiverStack = QuiverArrowRegistry.getArrowContainer(player);
        if (!quiverStack.isEmpty() && ((IArrowContainer2) quiverStack.getItem()).renderDefaultQuiverModel(quiverStack)) {

            IArrowContainer2 quiver = (IArrowContainer2) quiverStack.getItem();
            int maxStack = quiver.getSlotCount(quiverStack);
            int arrowCount = 0;
            for (int i = 0; i < maxStack; i++) {
                arrowCount += quiver.getStackInSlot(quiverStack, i).isEmpty()? 0 : 1;
            }
            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(BattlegearClientEvents.INSTANCE.quiverDetails);
            if(player.isWearing(EnumPlayerModelParts.JACKET)){//chest armor
                GlStateManager.translate(0, 0, scale);
            }
            renderer.getMainModel().bipedBody.postRender(scale);
            GlStateManager.scale(1.05F, 1.05F, 1.05F);
            BattlegearClientEvents.INSTANCE.quiverModel.render(arrowCount, scale);

            Minecraft.getMinecraft().getTextureManager().bindTexture(BattlegearClientEvents.INSTANCE.quiverBase);
            if(quiverStack.getItem() instanceof IDyable){
                int col = ((IDyable)quiver).getColor(quiverStack);
                float red = (float) (col >> 16 & 255) / 255.0F;
                float green = (float) (col >> 8 & 255) / 255.0F;
                float blue = (float) (col & 255) / 255.0F;
                GlStateManager.color(red, green, blue);
            }
            BattlegearClientEvents.INSTANCE.quiverModel.render(0, scale);
            GlStateManager.color(1, 1, 1);

            GlStateManager.popMatrix();
        }
    }
}
