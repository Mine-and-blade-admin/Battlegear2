package mods.battlegear2.client.renderer;

import mods.battlegear2.items.ItemShield;
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

    private static final ResourceLocation arrowTex = new ResourceLocation("textures/entity/arrow.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY &&
                (helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(item.getItem() instanceof ItemShield){
            ItemShield shield = (ItemShield)item.getItem();

            Tessellator tessellator = Tessellator.instance;
            Icon icon = shield.getTrimIcon();

            icon = shield.getIconIndex(item);
            RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                    icon.getMaxU(),
                    icon.getMinV(),
                    icon.getMinU(),
                    icon.getMaxV(),
                    icon.getOriginX(),
                    icon.getOriginY(), 16F/256F);

            GL11.glTranslatef(0, 0, 1F/256F);
            icon = shield.getBackIcon();
            RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                    icon.getMaxU(),
                    icon.getMinV(),
                    icon.getMinU(),
                    icon.getMaxV(),
                    icon.getOriginX(),
                    icon.getOriginY(), 1F/256F);

            GL11.glTranslatef(0, 0, -17F/256F);
            icon = shield.getTrimIcon();
            RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                    icon.getMaxU(),
                    icon.getMinV(),
                    icon.getMinU(),
                    icon.getMaxV(),
                    icon.getOriginX(),
                    icon.getOriginY(), 8F/256F);

            int arrowCount = shield.getArrowCount(item);
            //Bounds checking (rendering this many is quite silly, any more would look VERY silly)
            if(arrowCount > 64)
                arrowCount = 64;
            //System.out.println(arrowCount);
            for(int i = 0; i < arrowCount; i++){
                renderArrow(i);
            }

        }
    }


    private void renderArrow(int arrowNo){
        GL11.glPushMatrix();
        float x = ItemShield.arrowX[arrowNo];
        float y = ItemShield.arrowY[arrowNo];
        float depth =  ItemShield.arrowDepth[arrowNo];

        //depth = 1;

        Minecraft.getMinecraft().renderEngine.func_110577_a(arrowTex);



        Tessellator tessellator = Tessellator.instance;




        byte b0 = 0;
        float f2 = 12F/32F * depth;
        float f3 = 0F;
        float f4 = (float)(0 + b0 * 10) / 32.0F;
        float f5 = (float)(5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + b0 * 10) / 32.0F;
        float f9 = (float)(10 + b0 * 10) / 32.0F;
        float f10 = 0.05F;

        GL11.glScalef(f10, f10, f10);

        GL11.glTranslatef(x + 8 + 2.5F, y + 8 + 1.5F, 0);



        GL11.glRotatef(90.0F + ItemShield.pitch[arrowNo], 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(45.0F + ItemShield.yaw[arrowNo], 1.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);



        for (int i = 0; i < 2; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D * depth, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.addVertexWithUV(16.0D * depth, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(16.0D * depth, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(0.0D * depth, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.draw();

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.0D * depth, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.addVertexWithUV(16.0D * depth, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(16.0D * depth, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(0.0D * depth, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.draw();
        }

        GL11.glPopMatrix();


    }
}
