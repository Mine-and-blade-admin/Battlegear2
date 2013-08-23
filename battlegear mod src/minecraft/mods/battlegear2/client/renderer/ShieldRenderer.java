package mods.battlegear2.client.renderer;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.utils.EnumShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import net.minecraft.util.Icon;
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
            Icon icon = shield.getTrimIcon();

            int col = shield.getColor(item);
            float red = (float)(col >> 16 & 255) / 255.0F;
            float green = (float)(col >> 8 & 255) / 255.0F;
            float blue = (float)(col & 255) / 255.0F;

            icon = shield.getIconIndex(item);

            switch (type){
                case ENTITY:
                    GL11.glTranslatef(-0.5F, -0.25F, 0);

                    GL11.glColor3f(red, green, blue);
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), 16F/256F);
                    if(shield.enumShield != EnumShield.WOOD){
                        GL11.glColor3f(1,1,1);
                    }

                    GL11.glTranslatef(0, 0, -16F/256F);
                    icon = shield.getBackIcon();
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), 1F/256F);
                    GL11.glColor3f(1,1,1);

                    GL11.glTranslatef(0, 0, 24F/256F);
                    icon = shield.getTrimIcon();
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), (8F+16F)/256F);

                    if(item.isItemEnchanted())
                        SpearRenderer.renderEnchantmentEffects(tessellator);
                    break;
                case EQUIPPED:
                case EQUIPPED_FIRST_PERSON:

                    GL11.glColor3f(red, green, blue);
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), 16F/256F);

                    if(shield.enumShield != EnumShield.WOOD){
                        GL11.glColor3f(1,1,1);
                    }

                    GL11.glTranslatef(0, 0, 1F/256F);
                    icon = shield.getBackIcon();
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), 1F/256F);

                    GL11.glColor3f(1,1,1);

                    GL11.glTranslatef(0, 0, -1F/256F);
                    icon = shield.getTrimIcon();
                    RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                            icon.getMaxU(),
                            icon.getMinV(),
                            icon.getMinU(),
                            icon.getMaxV(),
                            icon.getOriginX(),
                            icon.getOriginY(), (8F+16F)/256F);

                    if(item.isItemEnchanted())
                        SpearRenderer.renderEnchantmentEffects(tessellator);

                    break;
                case INVENTORY:

                    GL11.glPushMatrix();
                    GL11.glColor3f(red, green, blue);
                    itemRenderer.renderIcon(0, 0, icon, 16, 16);
                    GL11.glColor3f(1, 1, 1);
                    icon = shield.getTrimIcon();
                    itemRenderer.renderIcon(0, 0, icon, 16, 16);

                    GL11.glPopMatrix();



                    break;
            }

            int arrowCount = shield.getArrowCount(item);
	    //Bounds checking (rendering this many is quite silly, any more would look VERY silly)
            if(arrowCount > 64)
                arrowCount = 64;
            //System.out.println(arrowCount);
            for(int i = 0; i < arrowCount; i++){
                BattlegearRenderHelper.renderArrow(type == ItemRenderType.ENTITY,
                        ItemShield.arrowX[i],ItemShield.arrowY[i], ItemShield.arrowDepth[i],
                        ItemShield.pitch[i]+90F, ItemShield.yaw[i]+45F);
            }
            GL11.glPopMatrix();

        }
    }



}
