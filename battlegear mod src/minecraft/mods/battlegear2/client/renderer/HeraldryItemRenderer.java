package mods.battlegear2.client.renderer;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.heraldry.HeraldryTextureSmall;
import mods.battlegear2.api.heraldry.RefreshableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import static org.lwjgl.opengl.GL11.*;

import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

public class HeraldryItemRenderer implements IItemRenderer{

    private RenderItem itemRenderer;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return item.getItem() instanceof IHeraldryItem &&
                ((IHeraldryItem)item.getItem()).hasHeraldry(item) &&
                type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if(itemRenderer == null){
            itemRenderer = new RenderItem();
        }

        byte[] heraldryData = ((IHeraldryItem)item.getItem()).getHeraldry(item);

        switch (type){
            case INVENTORY:
                doInventoryRendering(item, new HeraldryData(heraldryData), ((IHeraldryItem)item.getItem()));
                break;
        }
    }

    private void doInventoryRendering(ItemStack item, HeraldryData heraldryData, IHeraldryItem heraldryItem) {

        IIcon icon =  heraldryItem.getBaseIcon(item);

        itemRenderer.zLevel += 100;
        if(heraldryItem.shouldDoPass(IHeraldryItem.HeraldyRenderPassess.Pattern) && icon != null){
            glPushMatrix();

            glColor3f(1, 1, 1);
            itemRenderer.renderIcon(0, 0, icon, 16, 16);
            RefreshableTexture currentCrest = new RefreshableTexture(32, 32);
            currentCrest.refreshWith(heraldryData, false);
            ResourceLocation crestLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_crest", currentCrest);
            ITextureObject texture = Minecraft.getMinecraft().renderEngine.getTexture(crestLocation);
            if(texture == null){
                texture = new HeraldryTextureSmall(heraldryData);
                Minecraft.getMinecraft().renderEngine.loadTexture(crestLocation, texture);
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(crestLocation);

            glDepthFunc(GL11.GL_EQUAL);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            renderTexturedQuad(0, 0, itemRenderer.zLevel, 16, 16);

            glDisable(GL_BLEND);
            GL11.glDepthFunc(GL11.GL_LEQUAL);


            glPopMatrix();
        }

        icon = heraldryItem.getPostRenderIcon(item);
        if(heraldryItem.shouldDoPass(IHeraldryItem.HeraldyRenderPassess.PostRenderIcon) && icon != null){
            glPushMatrix();
            glColor3f(1, 1, 1);
            itemRenderer.renderIcon(0, 0, icon, 16, 16);
            glPopMatrix();
        }
        itemRenderer.zLevel -=100;
    }

    public void renderTexturedQuad(int x, int y, float z, int width, int height)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)z, 0D, 1D);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)z, 1D, 1D);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)z, 1D, 0D);
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)z, 0D, 0D);
        tessellator.draw();
    }
}
