package mods.battlegear2.client.renderer;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.HeraldryTextureSmall;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.heraldry.RefreshableTexture;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class HeraldryCrestItemRenderer {
    private RenderItem itemRenderer;


    public static final ResourceLocation map_overlay = new ResourceLocation("battlegear2", "textures/heraldry/Background.png");

    private void doMapRendering(ItemStack item, HeraldryData heraldryData, IHeraldryItem item1) {
        glPushMatrix();

        //glDepthFunc(GL11.GL_EQUAL);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tess = Tessellator.getInstance();

        Minecraft.getMinecraft().getTextureManager().bindTexture(map_overlay);
        tess.getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        tess.getWorldRenderer().pos(-8, 136, -.01).tex(0, 1).endVertex();
        tess.getWorldRenderer().pos(136, 136, -.01).tex(1, 1).endVertex();
        tess.getWorldRenderer().pos(136, -8, -.01).tex(1, 0).endVertex();
        tess.getWorldRenderer().pos(-8, -8, -.01).tex(0, 0).endVertex();
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


        tess.getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        tess.getWorldRenderer().pos(8, 120, -0.015).tex(0, 1).endVertex();
        tess.getWorldRenderer().pos(120, 120, -0.015).tex(1, 1).endVertex();
        tess.getWorldRenderer().pos(120, 8, -0.015).tex(1, 0).endVertex();
        tess.getWorldRenderer().pos(8, 8, -0.015).tex(0, 0).endVertex();
        tess.draw();


        glPopMatrix();
    }

    private void doInventoryRendering(ItemStack item, HeraldryData heraldryData, IHeraldryItem heraldryItem) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(map_overlay);
        BattlegearRenderHelper.renderTexturedQuad(0, 0, itemRenderer.zLevel, 16, 16);


        //IIcon icon =  heraldryItem.getBaseIcon(item);
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


        BattlegearRenderHelper.renderTexturedQuad(2, 2, itemRenderer.zLevel, 12, 12);



        glPopMatrix();


        itemRenderer.zLevel -=100;
    }
}
