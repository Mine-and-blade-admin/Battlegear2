package mods.battlegear2.client.gui;

import cpw.mods.fml.client.GuiScrollingList;
import mods.battlegear2.api.heraldry.Crest;
import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.client.gui.controls.GUIScrollList;
import mods.battlegear2.client.heraldry.PatternStore;
import mods.battlegear2.client.heraldry.TextureBackground;
import mods.mud.UpdateEntry;
import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GUICrestElementList extends GUIScrollList {


    private BattlegearSigilGUI parent;
    private List<Crest> entries;

    private DynamicTexture[] dynamicTextures = new DynamicTexture[HeraldryData.MAX_CRESTS+1];
    private boolean[] dirtyTextures = new boolean[HeraldryData.MAX_CRESTS+1];

    public GUICrestElementList(BattlegearSigilGUI parent, int listWidth, int x)
    {
        super(Minecraft.getMinecraft(), listWidth, parent.height-64, 30, parent.height - 30, x, 25);

        this.parent=parent;
        this.entries = new ArrayList<Crest>();

        for(int i = 0; i < dynamicTextures.length; i++){
            dynamicTextures[i] = new DynamicTexture(32,32);
            dirtyTextures[i] = true;
        }
    }
    @Override
    protected int getSize() {
        return 1+entries.size();
    }

    public void addNewCrest(){
        entries.add(new Crest(new int[]{0x00000000, 0xFFFFFFFF}, 0, (byte)16, (byte)4, (byte)4));
    }

    public void removeCrest(int index){
        entries.remove(index-1);
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        parent.select(index);
    }

    @Override
    protected boolean isSelected(int index) {
        return index == parent.getSelectedIndex();
    }

    @Override
    protected void drawBackground() {
        drawRect(left, top, left+listWidth, bottom, 0xAA000000);
        drawRect(left, parent.height, left+listWidth, 0, 0x44000000);
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        if(dirtyTextures[listIndex]){
            if(listIndex == 0){
                HeraldryData heraldryData = parent.getCurrentData();
                BufferedImage image = new BufferedImage(PatternStore.small_rgbs[heraldryData.getPattern()][0].length, PatternStore.small_rgbs[heraldryData.getPattern()][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);
                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                        image.setRGB(x, y, PatternStore.getBlendedSmallPixel(heraldryData.getPattern(), x, y, heraldryData.getColour(0), heraldryData.getColour(1), heraldryData.getColour(2)));
                    }
                }
                if(image.getHeight() != 32 || image.getWidth() != 32){
                    image = (BufferedImage)image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                }
                int[] pixels = dynamicTextures[0].getTextureData();

                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                        pixels[x+y*image.getWidth()] = image.getRGB(x,y);
                    }
                }
                dirtyTextures[listIndex] = false;
            }else{

            }
        }

        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);


        dynamicTextures[listIndex].updateDynamicTexture();
        ResourceLocation rl = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_dynamic_sigil_"+listIndex, dynamicTextures[listIndex]);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        drawTexturedModalRect(var5, var2-listWidth+9, var3, 21, 21, 0);

        if(listIndex == 0){
            parent.getFontRenderer().drawString(StatCollector.translateToLocal("gui.sigil.pattern"), var2-listWidth+9+25, var3+4, isSelected(listIndex)?0xFFFFFF00:0xFFFFFFFF);
        }else{
            parent.getFontRenderer().drawString(StatCollector.translateToLocal("gui.sigil.crest")+" "+listIndex, var2-listWidth+9+25, var3+4, isSelected(listIndex)?0xFFFFFF00:0xFFFFFFFF);
        }


        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);

    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(Tessellator tessellator, int x, int y, int width, int height, int zLevel)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)zLevel, 0F, 1F);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)zLevel, 1F, 1F);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)zLevel, 1F, 0F);
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)zLevel, 0F, 0F);
        tessellator.draw();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int x1, int y1, int x2, int y2, int col)
    {
        int j1;

        if (x1 < x2)
        {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2)
        {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }

        float f = (float)(col >> 24 & 255) / 255.0F;
        float f1 = (float)(col >> 16 & 255) / 255.0F;
        float f2 = (float)(col >> 8 & 255) / 255.0F;
        float f3 = (float)(col & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)x1, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y2, 0.0D);
        tessellator.addVertex((double)x2, (double)y1, 0.0D);
        tessellator.addVertex((double)x1, (double)y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }


}
