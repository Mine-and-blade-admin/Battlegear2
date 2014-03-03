package mods.battlegear2.client.gui.controls;

import mods.battlegear2.api.heraldry.RefreshableTexture;
import org.lwjgl.opengl.GL11;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import mods.battlegear2.api.heraldry.PatternStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiPatternScrollList extends GUIScrollList{
	
	private RefreshableTexture[] dynamicTextures;
    private boolean dirtyTextures = true;
	
	BattlegearSigilGUI parent;
	public GuiPatternScrollList(BattlegearSigilGUI parent, int width, int top, int bottom, int left) {
		super(width, top+20, bottom-20, left, 20);
		this.parent = parent;
        dynamicTextures = new RefreshableTexture[
                PatternStore.DEFAULT.patterns.get(parent.getCurrentData().getPatternIndex()).length];
		for(int i = 0; i < dynamicTextures.length; i++){
            dynamicTextures[i] = new RefreshableTexture(32,32);
        }
	}
	
	
	public void markAllDirty(){
    	dirtyTextures = true;
    }

	@Override
	protected int getSize() {
		return PatternStore.DEFAULT.patterns.get(parent.getCurrentData().getPatternIndex()).length;
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		parent.getCurrentData().setPattern(index);
		parent.markAllDirty();
	}

	@Override
	protected boolean isSelected(int index) {
		return index == parent.getCurrentData().getPattern();
	}

	@Override
	protected void drawBackground() {
		drawRect(left, top-20, left+listWidth, bottom+20, 0xAA000000);
        drawRect(left, parent.height, left+listWidth, 0, 0x44000000);
	}

	@Override
	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		
		if(dirtyTextures){
			HeraldryData heraldryData = parent.getCurrentData().clone();
			for(int i = 0; i < dynamicTextures.length; i++){
                heraldryData.setPattern(i);
                dynamicTextures[i].refreshWith(heraldryData, true);
			}
			dirtyTextures = false;
		}
		
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);


        dynamicTextures[var1].updateDynamicTexture();
        ResourceLocation rl = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_dynamic_pattern_"+var1, dynamicTextures[var1]);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        drawTexturedModalRect(var5, var2-listWidth/2-8, var3, 16, 16, 0);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
		
	}

}
