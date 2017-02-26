package mods.battlegear2.client.renderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Olivier on 11/06/2015.
 */
public abstract class LayerPlayerBase implements LayerRenderer<AbstractClientPlayer>{

    protected final RenderPlayer renderer;

    public LayerPlayerBase(RenderPlayer livingEntityRendererIn)
    {
        this.renderer = livingEntityRendererIn;
    }

    @Override
    public final void doRenderLayer(AbstractClientPlayer clientPlayer, float partialLimbSwing, float partialLimbSwingAmount, float partialTicks, float partialTickExisted, float partialRotationYaw, float partialRotationPitch, float scale) {
        doRender(clientPlayer, partialTicks, scale);
    }

    protected abstract void doRender(EntityPlayer player, float partialTicks, float scale);

    @Override
    public final boolean shouldCombineTextures() {
        return false;
    }
}
