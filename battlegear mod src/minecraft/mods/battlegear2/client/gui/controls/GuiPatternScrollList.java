package mods.battlegear2.client.gui.controls;

import java.awt.Image;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import mods.battlegear2.client.heraldry.PatternStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.GuiScrollingList;

public class GuiPatternScrollList extends GUIScrollList{
	
	private DynamicTexture[] dynamicTextures = new DynamicTexture[PatternStore.small_rgbs.length];
    private boolean dirtyTextures = true;
	
	BattlegearSigilGUI parent;
	public GuiPatternScrollList(BattlegearSigilGUI parent, int width, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, top+20, bottom-20, left, 20);
		this.parent = parent;
		
		for(int i = 0; i < dynamicTextures.length; i++){
            dynamicTextures[i] = new DynamicTexture(32,32);
        }
	}
	
	
	public void markAllDirty(){
    	dirtyTextures = true;
    }

	@Override
	protected int getSize() {
		return PatternStore.small_rgbs.length;
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
			HeraldryData heraldryData = parent.getCurrentData();
			for(int i = 0; i < dynamicTextures.length; i++){
				BufferedImage image = new BufferedImage(PatternStore.small_rgbs[i][0].length, PatternStore.small_rgbs[i][0][0].length,BufferedImage.TYPE_4BYTE_ABGR);
                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                        image.setRGB(x, y, PatternStore.getBlendedSmallPixel((byte) i, x, y, heraldryData.getColour(0), heraldryData.getColour(1), heraldryData.getColour(2)));
                    }
                }
                if(image.getHeight() != 32 || image.getWidth() != 32){
                    image = (BufferedImage)image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                }
                int[] pixels = dynamicTextures[i].getTextureData();

                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight(); y++){
                        pixels[x+y*image.getWidth()] = image.getRGB(x,y);
                    }
                }
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
