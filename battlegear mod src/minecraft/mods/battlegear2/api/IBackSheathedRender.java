package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Flag an item instance for pre-process of rendering sheathed "on back"
 * @see ISheathed
 * @see RenderPlayerEventChild.PreRenderSheathed
 * Used by ItemSpear to render flipped (GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);)
 */
public interface IBackSheathedRender{

    /**
     * The first steps taken before rendering the item as sheathed "on the back"
     * @param itemStack that is selected for rendering
     * @param amountOnBack number of {@link ItemStack} that rendered under the same category before this item
     * @param event other rendering data, wrapped into this event
     * @param inMainHand true if this item is in "right" hand slots, false in "left"
     */
    @SideOnly(Side.CLIENT)
    void preRenderBackSheathed(ItemStack itemStack, int amountOnBack, RenderPlayerEvent event, boolean inMainHand);
}
