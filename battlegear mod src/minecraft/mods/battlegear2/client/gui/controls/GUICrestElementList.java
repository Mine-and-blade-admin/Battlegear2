package mods.battlegear2.client.gui.controls;

import mods.battlegear2.api.heraldry.Crest;
import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.RefreshableTexture;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GUICrestElementList extends GUIScrollList {

    private BattlegearSigilGUI parent;
    private List<Crest> entries;

    private RefreshableTexture[] dynamicTextures = new RefreshableTexture[HeraldryData.MAX_CRESTS+1];
    private boolean[] dirtyTextures = new boolean[HeraldryData.MAX_CRESTS+1];

    public GUICrestElementList(BattlegearSigilGUI parent, int listWidth, int x)
    {
        super(listWidth, 30 + 25, parent.height - 30 - 25, x, 25);

        this.parent=parent;
        this.entries = new ArrayList<Crest>();

        for(int i = 0; i < dynamicTextures.length; i++){
            dynamicTextures[i] = new RefreshableTexture(32,32);
            dirtyTextures[i] = true;
        }
    }
    
    public void markDirty(int index){
    	dirtyTextures[index] = true;
    }
    
    @Override
	public int getSize() {
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
        drawRect(left, top-25, left+listWidth, bottom+25, 0xAA000000);
        drawRect(left, parent.height, left+listWidth, 0, 0x44000000);
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        if(dirtyTextures[listIndex]){
            if(listIndex == 0){
                dynamicTextures[0].refreshWith(parent.getCurrentData(), true);
            }else{

            }
            dirtyTextures[listIndex] = false;
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

    

}
