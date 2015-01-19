package mods.battlegear2.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Flag an item instance for pre-process of rendering sheathed "on back"
 * @see ISheathed
 * @see RenderPlayerEventChild.PreRenderSheathed
 * Used by ItemSpear to render flipped
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
    public void preRenderBackSheathed(ItemStack itemStack, int amountOnBack, RenderPlayerEvent event, boolean inMainHand);
}
