package mods.battlegear2.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GUITextList extends GUIScrollList{
    private final Box[] texts;
    private final FontRenderer font;
    public GUITextList(FontRenderer font, int width, int top, int bottom, int left, int entryHeight, Box[] texts) {
        super(width, top, bottom, left, entryHeight);
        this.font = font;
        this.texts = texts;
    }

    @Override
    protected int getSize() {
        return texts.length;
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        if(!doubleClick){
            texts[index].isActivated=!texts[index].isActivated;
        }
    }

    @Override
    protected boolean isSelected(int index) {
        return texts[index].isActivated;
    }

    @Override
    protected void drawBackground() {
        drawRect(left, top, left+listWidth, bottom, 0xAA000000);
    }

    @Override
    protected void drawSlot(int index, int var2, int var3, int var4, Tessellator var5) {
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        font.drawStringWithShadow(texts[index].text, left, var3, isSelected(index)?14737632:7368816);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static class Box{
        private final String text;
        public boolean isActivated;
        public Box(String text, boolean activated){
            this.text = text;
            this.isActivated = activated;
        }
    }

    public List<String> getActivated(){
        List<String> data = new ArrayList<String>();
        for(int i=0;i<texts.length;i++){
            if(texts[i].isActivated){
                data.add(texts[i].text);
            }
        }
        return data;
    }
}
