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
		return ((type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY)
				&& item.getItem() instanceof IHeraldryItem
				&& ((IHeraldryItem)item.getItem()).hasHeraldry(item));
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
			drawEquippedHeraldryItem(item, data);
		}
		
		if(type == ItemRenderType.INVENTORY){
			drawInventoryHeraldryItem(item, data);
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
        
        mc.renderEngine.bindTexture(HeraldryPattern.values()[SigilHelper.getPattern(code)].getPath());
        renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel);
        
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour1(code)]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour2(code)]);

	    HeraldryPositions position = HeraldryPositions.values()[SigilHelper.getIconPos(code)];
	    HeraldryIcon sigil = HeraldryIcon.values()[SigilHelper.getIcon(code)];
	    
	    if(! HeraldryIcon.Blank.equals(sigil)){
		    mc.renderEngine.bindTexture(sigil.getForegroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	if(flip){
		    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x+width, y, x, y+width);
		    	}else{
		    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x, y, x+width, y+width);
		    	}
		    }
		    
		    mc.renderEngine.bindTexture(sigil.getBackgroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(! flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	if(flip){
		    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x+width, y, x, y+width);
		    	}else{
		    		renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel-10, x, y, x+width, y+width);
		    	}
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
        
        float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour1(code)]);
        GL11.glColor3f(colour[2], colour[1], colour[0]);
        
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
        
        icon = HeraldryPattern.values()[SigilHelper.getPattern(code)].getIcon();
        colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour2(code)]);
        GL11.glColor3f(colour[2], colour[1], colour[0]);
        
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        
        GL11.glPushMatrix();
        
        mc.renderEngine.bindTexture( HeraldryPattern.values()[SigilHelper.getPattern(code)].getPath());
        renderItemIn2D_2(tessellator, 1, 0, 0, 1, 16, 16, 0.0625F);
        
        
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour1(code)]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getIconColour2(code)]);
	    
	    HeraldryPositions position = HeraldryPositions.values()[SigilHelper.getIconPos(code)];
	    HeraldryIcon sigil = HeraldryIcon.values()[SigilHelper.getIcon(code)];
	    
	    
	    if(! HeraldryIcon.Blank.equals(sigil)){
		    mc.renderEngine.bindTexture(sigil.getForegroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	if(flip){
		    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x+width, y, x, width+y, 16, 16, 0.0625F);
		    	}else{
		    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x, y, x+width, width+y, 16, 16, 0.0625F);
		    	}
		    }
		    
		    mc.renderEngine.bindTexture(sigil.getBackgroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(! flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	if(flip){
		    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x+width, y, x, width+y, 16, 16, 0.0625F);
		    	}else{
		    		RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, x, y, x+width, width+y, 16, 16, 0.0625F);
		    	}
		    	
		    }
	    }
	    
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);   	    
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
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
	
	public static void renderItemIn2D_2(Tessellator par0Tessellator, float par1, float par2, float par3, float par4, int par5, int par6, float par7)
    {
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)par1, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)par1, (double)par2);
        par0Tessellator.draw();
        
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - par7), (double)par1, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, (double)(0.0F - par7), (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, (double)(0.0F - par7), (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - par7), (double)par1, (double)par4);
        par0Tessellator.draw();
    }
}
