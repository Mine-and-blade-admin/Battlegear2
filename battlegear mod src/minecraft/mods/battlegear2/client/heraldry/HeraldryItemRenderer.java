package mods.battlegear2.client.heraldry;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.api.IHeraldryItem;
import mods.battlegear2.common.BattleGear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class HeraldryItemRenderer implements IItemRenderer{

	Minecraft mc;
	RenderItem itemRenderer;

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return (type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		if(mc == null){
			mc = FMLClientHandler.instance().getClient();
			itemRenderer = new RenderItem();
		}
		
		
		if(type == ItemRenderType.EQUIPPED){
			if(item.getItem() instanceof IHeraldryItem && ((IHeraldryItem)item.getItem()).hasHeraldry(item)){
				drawEquippedHeraldryItem(item, data);
			}
		}
		
		if(type == ItemRenderType.INVENTORY){
			if(item.getItem() instanceof IHeraldryItem && ((IHeraldryItem)item.getItem()).hasHeraldry(item)){
				drawInventoryHeraldryItem(item, data);
			}
		}
	}
	
	protected void drawInventoryHeraldryItem(ItemStack item, Object[] data) {
		this.mc.renderEngine.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		IHeraldryItem heraldryItem = (IHeraldryItem)item.getItem();
		int code = heraldryItem.getHeraldryCode(item);
		
		Icon icon = heraldryItem.getBaseIcon();
		
		float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour1(code)]);
        GL11.glColor4f(colour[2], colour[1], colour[0], 1);
		itemRenderer.renderIcon(0, 0, icon, 16, 16);
		
		GL11.glDepthFunc(GL11.GL_GREATER);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    itemRenderer.zLevel -= 50.0F;

	    GL11.glPushMatrix();
	    
	    colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour2(code)]);
        GL11.glColor4f(colour[2], colour[1], colour[0], 1);
        
        
        mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/patterns/pattern-"+SigilHelper.getPattern(code)+".png");
        renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel);
        
        mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/icons/icon-"+"1"+"-0.png");
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour1(code)]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour2(code)]);

	    int pattern = SigilHelper.getIconPos(code);
	    
	    for(int i = 0; i < SigilHelper.patternPassess[pattern]; i++){
	    	float x = SigilHelper.patternSourceX[pattern][i];
	    	float y = SigilHelper.patternSourceY[pattern][i];
	    	float width = SigilHelper.patternWidth[pattern];
	    	boolean flip = SigilHelper.patternFlip[pattern][i];
	    	boolean flipColours = SigilHelper.patternAltColours[pattern][i];
	    	
	    	GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    	
	    	if(flipColours){
	    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
	    	}else{
	    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
	    	}

	    	if(flip){
	    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x, y, -(width-x), y+width);
	    	}else{
	    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x, y, x+width, y+width);
	    	}
	    }
	    
	    
	    GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        itemRenderer.zLevel += 50.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        
        
        GL11.glColor4f(1, 1, 1, 1);
	    icon = heraldryItem.getPostRenderIcon();
        
        this.mc.renderEngine.bindTexture("/gui/items.png");
        
        itemRenderer.renderIcon(0, 0, icon, 16, 16);
		
	}
	
	public void drawEquippedHeraldryItem(ItemStack item, Object... data){
		this.mc.renderEngine.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		IHeraldryItem heraldryItem = (IHeraldryItem)item.getItem();
		int code = heraldryItem.getHeraldryCode(item);

		Icon icon = heraldryItem.getBaseIcon();
		
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        
        float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.getColour1(code));
        GL11.glColor4f(colour[2], colour[1], colour[0], 1);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
        
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour2(code)]);
        GL11.glColor4f(colour[2], colour[1], colour[0], 1);
        
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/patterns/pattern-"+SigilHelper.getPattern(code)+".png");
        
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
        
        mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/icons/icon-"+"1"+"-0.png");
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour1(code)]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour2(code)]);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    int pattern = SigilHelper.getIconPos(code);
	    for(int i = 0; i < SigilHelper.patternPassess[pattern]; i++){
	    	float x = SigilHelper.patternSourceX[pattern][i];
	    	float y = SigilHelper.patternSourceY[pattern][i];
	    	float width = SigilHelper.patternWidth[pattern];
	    	boolean flip = SigilHelper.patternFlip[pattern][i];
	    	boolean flipColours = SigilHelper.patternAltColours[pattern][i];
	    	
	    	if(flipColours){
	    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
	    	}else{
	    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
	    	}
	    	
	    	if(flip){
	    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x, y, -(width-x), width+y, 96, 96, 0.0625F);
	    	}else{
	    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x, y, x+width, width+y, 96, 96, 0.0625F);
	    	}
	    }
	    
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);   	    
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4f(1, 1, 1, 1);
        
        GL11.glColor4f(1, 1, 1, 1);
	    icon = heraldryItem.getPostRenderIcon();
        f = icon.getMinU();
        f1 = icon.getMaxU();
        f2 = icon.getMinV();
        f3 = icon.getMaxV();
        f4 = 0.0F;
        f5 = 0.3F;
        
        this.mc.renderEngine.bindTexture("/gui/items.png");
        
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F*1.5F);
	}
	
	public void renderTexturedQuad(int par1, int par2, int par4, int par5, float zLevel){
		renderTexturedQuad(par1, par2, par4, par5, zLevel, 0,0,1,1);
	}
	
	public void renderTexturedQuad(int par1, int par2, int par4, int par5, float zLevel, float minX, float minY, float maxX, float maxY)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)zLevel, (double)minX, (double)maxY);
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)zLevel, (double)maxX, (double)maxY);
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)zLevel, (double)maxX, (double)minY);
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)minX, (double)minY);
        tessellator.draw();
    }
}
