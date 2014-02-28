package mods.mud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiModUpdateButton extends GuiButton{


    RenderItem renderItem = new RenderItem();
    ItemStack icon = new ItemStack(Blocks.farmland);
    List<String> text = null;
    private GuiScreen parent;
    public GuiModUpdateButton(int id, int x, int y, GuiScreen parent) {
        super(id, x, y, 100, 20, "");
        this.parent = parent;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        super.drawButton(par1Minecraft, par2, par3);

        if(text == null){
            text = par1Minecraft.fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal("mud.name"), 80);
        }

        renderItem.renderItemIntoGUI(par1Minecraft.fontRenderer, par1Minecraft.getTextureManager(), icon, xPosition+2, yPosition+2);

        int l = 14737632;

        if (!this.enabled)
            l = -6250336;
        else if (this.field_146123_n)
            l = 16777120;

        float scale = 1.25F;
        GL11.glScalef(1F/scale, 1F/scale, 1F/scale);

        for(int i = 0; i < text.size() && i < 2; i++){
            drawCenteredString(par1Minecraft.fontRenderer, text.get(i), (int)(scale*(xPosition+20+40)), (int)(scale*((yPosition+3)+par1Minecraft.fontRenderer.FONT_HEIGHT*i)), l);
        }
        GL11.glScalef(scale, scale, scale);
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        boolean press = super.mousePressed(par1Minecraft, par2, par3);
        if(press){
            Minecraft.getMinecraft().displayGuiScreen(new GuiChangelogDownload(parent));
        }
        return press;
    }
}
