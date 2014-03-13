package mods.battlegear2.client.renderer;

import mods.battlegear2.items.ItemQuiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class QuiverItremRenderer implements IItemRenderer{

    private Minecraft mc;
    private RenderItem itemRenderer;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP && item != null && item.getItem() instanceof ItemQuiver;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY &&
                (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        if (mc == null) {
            mc = FMLClientHandler.instance().getClient();
            itemRenderer = new RenderItem();
        }

        this.mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);

        ItemQuiver quiver = (ItemQuiver)item.getItem();
        int col = quiver.getColor(item);
        float red = (float)(col >> 16 & 255) / 255.0F;
        float green = (float)(col >> 8 & 255) / 255.0F;
        float blue = (float)(col & 255) / 255.0F;
        boolean hasArrows = false;
        int maxStack = quiver.getSlotCount(item);
        for(int i = 0; i < maxStack && !hasArrows; i++){
            hasArrows = quiver.getStackInSlot(item, i) != null;
        }

        IIcon icon =  item.getIconIndex();
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        switch (type){
            case INVENTORY:
                GL11.glColor3f(red,green,blue);
                //MOJANG derp fixes:
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                itemRenderer.renderIcon(0, 0, icon, 16, 16);
                GL11.glColor3f(1, 1, 1);
                icon = quiver.quiverDetails;
                itemRenderer.renderIcon(0, 0, icon, 16, 16);
                if(hasArrows){
                    icon = quiver.quiverArrows;
                    itemRenderer.renderIcon(0, 0, icon, 16, 16);
                }
                break;
        	case ENTITY:
        		GL11.glTranslatef(-0.5F, -0.25F, 0);
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glColor3f(red,green,blue);
                ItemRenderer.renderItemIn2D(tessellator,
                        icon.getMaxU(),
                        icon.getMinV(),
                        icon.getMinU(),
                        icon.getMaxV(),
                        icon.getIconWidth(),
                        icon.getIconHeight(), 16F / 256F);

                GL11.glColor3f(1, 1, 1);
                icon = quiver.quiverDetails;
                ItemRenderer.renderItemIn2D(tessellator,
                        icon.getMaxU(),
                        icon.getMinV(),
                        icon.getMinU(),
                        icon.getMaxV(),
                        icon.getIconWidth(),
                        icon.getIconHeight(), 16F / 256F);

                if(hasArrows){
                    GL11.glTranslated(0, 0, -4F/256F);
                    icon = quiver.quiverArrows;
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), 8F / 256F);
                }
                break;
        }
        GL11.glPopMatrix();
    }
}
