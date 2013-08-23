package mods.battlegear2.client.renderer;

import mods.battlegear2.client.utils.BattlegearRenderHelper;
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

            GL11.glPushMatrix();

            Tessellator tessellator = Tessellator.instance;
            Icon icon = shield.getTrimIcon();

            if(type == ItemRenderType.ENTITY){
                GL11.glTranslatef(-0.5F, -0.25F, 0);

                icon = shield.getIconIndex(item);
                RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                        icon.getMaxU(),
                        icon.getMinV(),
                        icon.getMinU(),
                        icon.getMaxV(),
                        icon.getOriginX(),
                        icon.getOriginY(), 16F/256F);

                GL11.glTranslatef(0, 0, -16F/256F);
                icon = shield.getBackIcon();
                RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                        icon.getMaxU(),
                        icon.getMinV(),
                        icon.getMinU(),
                        icon.getMaxV(),
                        icon.getOriginX(),
                        icon.getOriginY(), 1F/256F);

                GL11.glTranslatef(0, 0, 24F/256F);
                icon = shield.getTrimIcon();
                RenderManager.instance.itemRenderer.renderItemIn2D(tessellator,
                        icon.getMaxU(),
                        icon.getMinV(),
                        icon.getMinU(),
                        icon.getMaxV(),
                        icon.getOriginX(),
                        icon.getOriginY(), 8F/256F);



            }else{



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
