package mods.battlegear2.client.heraldry;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.api.IHeraldyItem.HeraldyRenderPassess;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.MinecraftForge;

public class HeraldryItemRenderer implements IItemRenderer{

	Minecraft mc;
	RenderItem itemRenderer;
	private float trimZRaiseFactor;

	
	public HeraldryItemRenderer(){
		this(1);
	}
	public HeraldryItemRenderer(float trimZRaiseFactor) {
		this.trimZRaiseFactor = trimZRaiseFactor;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return ((type == ItemRenderType.EQUIPPED || 
				type == ItemRenderType.EQUIPPED_FIRST_PERSON ||
				type == ItemRenderType.INVENTORY || 
				type == ItemRenderType.ENTITY)
				&& item.getItem() instanceof IHeraldyItem
				&& ((IHeraldyItem)item.getItem()).hasHeraldry(item));
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return (type == ItemRenderType.ENTITY && 
				(helper == ItemRendererHelper.ENTITY_BOBBING || 
					(helper == ItemRendererHelper.ENTITY_ROTATION && Minecraft.isFancyGraphicsEnabled())
				));
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		if(mc == null){
			mc = FMLClientHandler.instance().getClient();
			itemRenderer = new RenderItem();
		}
		
		if(item != null){
			if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON){
				drawEquippedHeraldryItem(item, data);
			}
			
			if(type == ItemRenderType.INVENTORY){
				drawInventoryHeraldryItem(item, data);
			}
			
			if(type == ItemRenderType.ENTITY){
				drawIEntityHeraldryItem(item, data);
			}
		}
	}
	
	protected void drawIEntityHeraldryItem(ItemStack item, Object[] data) {
		EntityItem entiyItem = (EntityItem)data[1];
		GL11.glPushMatrix();
		
		
		if(RenderItem.renderInFrame){
			GL11.glScalef(1.2F, 1.2F, 1);
			GL11.glTranslatef(-0.5F, -0.375F, 0);
		}else{
			GL11.glTranslatef(-0.5F, 0, 0);
			GL11.glRotatef(-mc.renderViewEntity.rotationYaw, 0, 1, 0);
		}
		drawEquippedHeraldryItem(item, data);
		GL11.glPopMatrix();
	}
	
	protected void drawInventoryHeraldryItem(ItemStack item, Object[] data) {
		
		this.mc.renderEngine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
		
		Tessellator tessellator = Tessellator.instance;
		
		IHeraldyItem heraldryItem = (IHeraldyItem)item.getItem();
		byte[] code = heraldryItem.getHeraldryCode(item);
		
		Icon icon = heraldryItem.getBaseIcon(item);
		
		float[] colour = SigilHelper.getPrimaryColourArray(code);
		GL11.glColor3f(colour[0], colour[1], colour[2]);
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.PrimaryColourBase) && icon!=null)
        	itemRenderer.renderIcon(0, 0, icon, 16, 16);
		
        this.mc.renderEngine.bindTexture("/gui/items.png");
		GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    //itemRenderer.zLevel -= 50.0F;

	    GL11.glPushMatrix();
	    
	    colour = SigilHelper.getSecondaryColourArray(code);
	    GL11.glColor3f(colour[0], colour[1], colour[2]);
        
        mc.renderEngine.bindTexture(SigilHelper.getPattern(code).getPath());
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.SecondaryColourPattern))
        	renderTexturedQuad(0, 0, 16, 16, itemRenderer.zLevel);

	    float[] colourIconPrimary = SigilHelper.getSigilPrimaryColourArray(code);
	    float[] colourIconSecondary = SigilHelper.getSigilSecondaryColourArray(code);

	    HeraldryPositions position = SigilHelper.getSigilPosition(code);
	    HeraldryIcon sigil = SigilHelper.getSigil(code);
	    
	    if(! HeraldryIcon.Blank.equals(sigil) && heraldryItem.shouldDoPass(HeraldyRenderPassess.Sigil)){
		    mc.renderEngine.bindTexture(sigil.getForegroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor3f(colourIconSecondary[0],
		    				colourIconSecondary[1],
		    				colourIconSecondary[2]);
		    	}else{
		    		GL11.glColor3f(colourIconPrimary[0],
		    				colourIconPrimary[1],
		    				colourIconPrimary[2]);
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
		    	
		    	if(!flipColours){
		    		GL11.glColor3f(colourIconSecondary[0],
		    				colourIconSecondary[1],
		    				colourIconSecondary[2]);
		    	}else{
		    		GL11.glColor3f(colourIconPrimary[0],
		    				colourIconPrimary[1],
		    				colourIconPrimary[2]);
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
        //itemRenderer.zLevel += 50.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        this.mc.renderEngine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
        colour = SigilHelper.getSecondaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        icon = heraldryItem.getTrimIcon(item);
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.SecondaryColourTrim) && icon!=null){
        	itemRenderer.renderIcon(0, 0, icon, 16, 16);
        }
        
        GL11.glColor4f(1, 1, 1, 1);
	    icon = heraldryItem.getPostRenderIcon(item);
        
        itemRenderer.renderIcon(0, 0, icon, 16, 16);
		
	}
	
	public void drawEquippedHeraldryItem(ItemStack item, Object... data){
		this.mc.renderEngine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		IHeraldyItem heraldryItem = (IHeraldyItem)item.getItem();
		byte[] code = heraldryItem.getHeraldryCode(item);

		Icon icon = heraldryItem.getBaseIcon(item);
		
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        
        float[] colour = SigilHelper.getPrimaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);
    	
        
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.PrimaryColourBase)&& icon!=null)
        	RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
        
        this.mc.renderEngine.bindTexture("/gui/items.png");
        icon = SigilHelper.getPattern(code).getIcon();
        colour = SigilHelper.getSecondaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);

        
        GL11.glDepthFunc(GL11.GL_EQUAL);
        //GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        
        GL11.glPushMatrix();
        
        mc.renderEngine.bindTexture(SigilHelper.getPattern(code).getPath());
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.SecondaryColourPattern)&& icon!=null)
        	renderItemIn2D_2(tessellator, 0, 0, 1, 1, 16, 16, 0.0625F);
        
        
	    float[] colourIconPrimary = SigilHelper.getSigilPrimaryColourArray(code);
	    float[] colourIconSecondary = SigilHelper.getSigilSecondaryColourArray(code);
	    
	    HeraldryPositions position = SigilHelper.getSigilPosition(code);
	    HeraldryIcon sigil = SigilHelper.getSigil(code);	    
	    
	    GL11.glDisable(GL11.GL_LIGHTING);
	    if(! HeraldryIcon.Blank.equals(sigil) && heraldryItem.shouldDoPass(HeraldyRenderPassess.Sigil)){
		    mc.renderEngine.bindTexture(sigil.getForegroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor3f(colourIconSecondary[0],
		    				colourIconSecondary[1],
		    				colourIconSecondary[2]);
		    	}else{
		    		GL11.glColor3f(colourIconPrimary[0],
		    				colourIconPrimary[1],
		    				colourIconPrimary[2]);
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
		    	
		    	if(!flipColours){
		    		GL11.glColor3f(colourIconSecondary[0],
		    				colourIconSecondary[1],
		    				colourIconSecondary[2]);
		    	}else{
		    		GL11.glColor3f(colourIconPrimary[0],
		    				colourIconPrimary[1],
		    				colourIconPrimary[2]);
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
        
        this.mc.renderEngine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
        colour = SigilHelper.getSecondaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        icon = heraldryItem.getTrimIcon(item);
        if(heraldryItem.shouldDoPass(HeraldyRenderPassess.SecondaryColourTrim) && icon!=null)
        	RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F*trimZRaiseFactor);

        GL11.glColor4f(1, 1, 1, 1);
	    icon = heraldryItem.getPostRenderIcon(item);

        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F*trimZRaiseFactor);
	
	    
	    if (item != null && item.hasEffect()){
        	renderEnchantmentEffects(tessellator);
        }
	    
	    
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
	
	public static void renderEnchantmentEffects(Tessellator tessellator) {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("%blur%/misc/glint.png");
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        float f7 = 0.76F;
        GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        float f8 = 0.125F;
        GL11.glScalef(f8, f8, f8);
        float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
        GL11.glTranslatef(f9, 0.0F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(f8, f8, f8);
        f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
        GL11.glTranslatef(-f9, 0.0F, 0.0F);
        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
	}
}
