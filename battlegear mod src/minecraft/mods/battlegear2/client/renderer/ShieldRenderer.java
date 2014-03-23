package mods.battlegear2.client.renderer;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.items.ItemShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class ShieldRenderer implements IItemRenderer{

    private Minecraft mc;
    private RenderItem itemRenderer;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY &&
                (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(item.getItem() instanceof ItemShield){

            if (mc == null) {
                mc = FMLClientHandler.instance().getClient();
                itemRenderer = new RenderItem();
            }

            ItemShield shield = (ItemShield)item.getItem();

            GL11.glPushMatrix();

            Tessellator tessellator = Tessellator.instance;

            int col = shield.getColor(item);
            float red = (float)(col >> 16 & 255) / 255.0F;
            float green = (float)(col >> 8 & 255) / 255.0F;
            float blue = (float)(col & 255) / 255.0F;

            IIcon icon = item.getItem().getIconIndex(item);

            switch (type){
                case ENTITY:
                    GL11.glTranslatef(-0.5F, -0.25F, 0);

                    GL11.glColor3f(red, green, blue);
                    ItemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), 16F / 256F);
                    if(!shield.enumShield.getName().equals("wood")){
                        GL11.glColor3f(1,1,1);
                    }

                    GL11.glTranslatef(0, 0, -16F/256F);
                    icon = shield.getBackIcon();
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), 1F/256F);
                    GL11.glColor3f(1,1,1);

                    GL11.glTranslatef(0, 0, 24F/256F);
                    icon = shield.getTrimIcon();
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), (8F+16F)/256F);

                    if(item.isItemEnchanted())
                        BattlegearRenderHelper.renderEnchantmentEffects(tessellator);
                    break;
                case EQUIPPED:
                case EQUIPPED_FIRST_PERSON:

                    GL11.glColor3f(red, green, blue);
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), 16F/256F);

                    if(!shield.enumShield.getName().equals("wood")){
                        GL11.glColor3f(1,1,1);
                    }

                    GL11.glTranslatef(0, 0, 1F/256F);
                    icon = shield.getBackIcon();
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), 1F/256F);

                    GL11.glColor3f(1,1,1);

                    GL11.glTranslatef(0, 0, -1F/256F);
                    icon = shield.getTrimIcon();
                    ItemRenderer.renderItemIn2D(tessellator,
                    		icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getIconWidth(),
                            icon.getIconHeight(), (8F+16F)/256F);

                    if(item.isItemEnchanted())
                        BattlegearRenderHelper.renderEnchantmentEffects(tessellator);

                    break;
                case INVENTORY:

                    GL11.glPushMatrix();
                    GL11.glColor3f(red, green, blue);
                    //MOJANG derp fixes:
                        GL11.glEnable(GL11.GL_ALPHA_TEST);
                        GL11.glEnable(GL11.GL_BLEND);
                    itemRenderer.renderIcon(0, 0, icon, 16, 16);
                    GL11.glColor3f(1, 1, 1);
                    icon = shield.getTrimIcon();
                    itemRenderer.renderIcon(0, 0, icon, 16, 16);
                    GL11.glPopMatrix();

                    break;
            }
            BattlegearRenderHelper.renderArrows(item, type == ItemRenderType.ENTITY);

            GL11.glPopMatrix();

        }
    }



}
