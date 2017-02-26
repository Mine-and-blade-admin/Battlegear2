package mods.battlegear2.client.renderer;

import mods.battlegear2.client.utils.BattlegearRenderHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Render a player sheathed items
 */
public class LayerOffhandItem extends LayerPlayerBase {

    public LayerOffhandItem(RenderPlayer playerRenderer)
    {
        super(playerRenderer);
    }

    @Override
    protected void doRender(EntityPlayer player, float partialTicks, float scale) {
        BattlegearRenderHelper.renderItemIn3rdPerson(player, renderer, partialTicks);
    }
}
