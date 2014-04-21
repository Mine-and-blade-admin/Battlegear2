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
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import static org.lwjgl.opengl.GL11.*;

public class HeraldryCrestItemRenderer implements IItemRenderer{
    private RenderItem itemRenderer;


    public static final ResourceLocation map_overlay = new ResourceLocation("battlegear2", "textures/heraldry/Background.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return item.getItem() instanceof IHeraldryItem &&
                ((IHeraldryItem)item.getItem()).hasHeraldry(item) &&
                (type == ItemRenderType.INVENTORY || type == ItemRenderType.FIRST_PERSON_MAP);
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
            case FIRST_PERSON_MAP:
                doMapRendering(item, new HeraldryData(heraldryData), ((IHeraldryItem)item.getItem()));
        }
    }

    private void doMapRendering(ItemStack item, HeraldryData heraldryData, IHeraldryItem item1) {
        glPushMatrix();

        //glDepthFunc(GL11.GL_EQUAL);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tess = Tessellator.instance;

        Minecraft.getMinecraft().getTextureManager().bindTexture(map_overlay);
        tess.startDrawingQuads();
        tess.addVertexWithUV(-8,136,-.01,0,1);
        tess.addVertexWithUV(136,136,-.01,1,1);
        tess.addVertexWithUV(136,-8,-.01,1,0);
        tess.addVertexWithUV(-8,-8,-.01,0,0);
        tess.draw();

        //glDisable(GL_BLEND);
        //glDepthFunc(GL11.GL_LEQUAL);
        RefreshableTexture currentCrest = new RefreshableTexture(32, 32);
        currentCrest.refreshWith(heraldryData, false);
        ResourceLocation crestLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_crest", currentCrest);
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(crestLocation);
        if(texture == null){
            texture = new HeraldryTextureSmall(heraldryData);
            Minecraft.getMinecraft().getTextureManager().loadTexture(crestLocation, texture);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(crestLocation);


        tess.startDrawingQuads();
        tess.addVertexWithUV(8,120,-0.015,0,1);
        tess.addVertexWithUV(120,120,-0.015,1,1);
        tess.addVertexWithUV(120,8,-0.015,1,0);
        tess.addVertexWithUV(8,8,-0.015,0,0);
        tess.draw();


        glPopMatrix();
    }

    private void doInventoryRendering(ItemStack item, HeraldryData heraldryData, IHeraldryItem heraldryItem) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(map_overlay);
        renderTexturedQuad(0, 0, itemRenderer.zLevel, 16, 16);


        IIcon icon =  heraldryItem.getBaseIcon(item);
        itemRenderer.zLevel += 100;
        glPushMatrix();
        RefreshableTexture currentCrest = new RefreshableTexture(32, 32);
        currentCrest.refreshWith(heraldryData, false);
        ResourceLocation crestLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_crest", currentCrest);
        ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(crestLocation);
        if(texture == null){
            texture = new HeraldryTextureSmall(heraldryData);
            Minecraft.getMinecraft().getTextureManager().loadTexture(crestLocation, texture);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(crestLocation);


        renderTexturedQuad(2, 2, itemRenderer.zLevel, 12, 12);



        glPopMatrix();


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
