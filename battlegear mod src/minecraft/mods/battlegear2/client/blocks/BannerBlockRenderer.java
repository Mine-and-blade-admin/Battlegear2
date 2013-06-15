package mods.battlegear2.client.blocks;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import org.lwjgl.opengl.GL11;

import mods.battlegear2.api.IHeraldyItem.HeraldyRenderPassess;
import mods.battlegear2.client.heraldry.HeraldryBannerPositions;
import mods.battlegear2.client.heraldry.HeraldryIcon;
import mods.battlegear2.client.heraldry.HeraldryPositions;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.blocks.TileEntityBanner;
import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BannerBlockRenderer extends TileEntitySpecialRenderer{

    private RenderBlocks blockRenderer;
    
    /** The ModelSign instance used by the TileEntitySignRenderer */
    
    /**
     * Called when the ingame world being rendered changes (e.g. on world -> nether travel) due to using one renderer
     * per tile entity type, rather than instance
     */
    public void onWorldChange(World par1World)
    {
        this.blockRenderer = new RenderBlocks(par1World);
    }
    
   
    
    public void renderTileEntityAt(TileEntity tileentity, double par2, double par4,
			double par6, float f) {
    	Block block = tileentity.blockType;
		TileEntityBanner bannerEntity = (TileEntityBanner)tileentity;
		
		
		
		if(bannerEntity.isBase()){
			Tessellator tessellator = Tessellator.instance;
			double x1, x2, y1, y2, z1, z2;

			this.bindTextureByName("/terrain.png");
	        RenderHelper.disableStandardItemLighting();
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_CULL_FACE);
	         if (Minecraft.isAmbientOcclusionEnabled())
	        {
	            GL11.glShadeModel(GL11.GL_SMOOTH);
	        }
	        else
	        {
	            GL11.glShadeModel(GL11.GL_FLAT);
	        }
	        float angle = -bannerEntity.getAngle();
	        GL11.glPushMatrix();
	        GL11.glTranslatef((float)par2+0.5F, (float)par4, (float)par6+0.5F);
	        GL11.glRotatef(angle+90, 0, 1, 0);
	        
			if(bannerEntity.isOnGround()){
			
				
		        
		        tessellator.startDrawingQuads();
		        
		        //Draw Base 1
		        Icon icon = Block.planks.getIcon(0, 0);
		        x1 = -7F/16F;
		        x2 = 7F/16F;
		        y1 = 0F;
		        y2 = 1/16F;
		        z1 = -.5F/16;
		        z2 = .5F/16;
		        
		        tessellator.setNormal(0, 0, 1);
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(10), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(10), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(10), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(10), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(12), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(12), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(12), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(12), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(14), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(14), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(14), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(14), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(16), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(16), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(6), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(6), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        
		        tessellator.draw();
		        GL11.glPopMatrix();
		        
		        GL11.glPushMatrix();
		        y2 = 0.95F/16F;
		        GL11.glTranslatef((float)par2+0.5F, (float)par4, (float)par6+0.5F);
		        GL11.glRotatef(angle, 0, 1, 0);
		        tessellator.startDrawingQuads();
	
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(2), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(2), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(2), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(2), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(15));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(6), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(6), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(2));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        
		        tessellator.draw();
		        GL11.glPopMatrix();
		        
		        //Draw Base 2
		       
		        
		        //Draw Pole
		        GL11.glPushMatrix();
		        GL11.glTranslatef((float)par2+0.5F, (float)par4, (float)par6+0.5F);
		        GL11.glRotatef(angle+90, 0, 1, 0);
		        y2 = 0.95F/16F;
		        tessellator.startDrawingQuads();
		        
		        icon = Block.wood.getIcon(3, 0);
		        x1 = -.5F/16F;
		        x2 = .5F/16F;
		        y1 = 1/16F;
		        y2 = 1;
		        z1 = -.5F/16;
		        z2 = .5F/16;
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(1));
		        
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(1));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(16), icon.getInterpolatedV(16));
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(16), icon.getInterpolatedV(1));
	
		        y1 = 1;
		        y2 = 30F/16F;
		        
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(0));
		        
		        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        
		        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(0));
		        
		        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(0));
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(12), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(16), icon.getInterpolatedV(14));
		        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(16), icon.getInterpolatedV(0));
	
		        icon = Block.wood.getIcon(0, 0);
		        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
		        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
		        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
		        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
		        tessellator.draw();
		        
		        //GL11.glPopMatrix();
		        
		       /* GL11.glPushMatrix();
		        GL11.glTranslatef((float)par2+0.5F, (float)par4, (float)par6+0.5F);
		        GL11.glRotatef(angle, 0, 1, 0);*/
		        
		        
		        
			}else{
				
			}
			
			Icon icon = Block.planks.getIcon(0, 0);
	        x1 = -7.5/16F;
	        x2 = 7.5/16F;
	        y1 = 28.5 / 16F;
	        y2 = 29.5F / 16F;
	        if(bannerEntity.isOnGround()){
	        	z1 = 0F;
	        	z2 = 1/16F;
	        }else{
	        	z1 = -0.5F;
	        	z2 = z1 + 1/16F;
	        }
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(1));
	        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(0), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(2), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(2), icon.getInterpolatedV(1));
	        
	        tessellator.addVertexWithUV(x1, y1, z1, icon.getInterpolatedU(2), icon.getInterpolatedV(1));
	        tessellator.addVertexWithUV(x2, y1, z1, icon.getInterpolatedU(2), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
	        
	        tessellator.addVertexWithUV(x1, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(1));
	        tessellator.addVertexWithUV(x2, y2, z1, icon.getInterpolatedU(4), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(1));
	        
	        tessellator.addVertexWithUV(x1, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(1));
	        tessellator.addVertexWithUV(x2, y1, z2, icon.getInterpolatedU(6), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x2, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(15));
	        tessellator.addVertexWithUV(x1, y2, z2, icon.getInterpolatedU(8), icon.getInterpolatedV(1));
	        tessellator.draw();
			
			
	        
	        x1 = 7F/16F;
	        x2 = -7F/16F;
	        y2 = 29.5F / 16F;
	        y1 = y2 - 14F/16F * 2;
	        z2 = z2 + 0.1F/16F;
	        
	        float[] colour = SigilHelper.getPrimaryColourArray(bannerEntity.getHeraldry());
	        
	        tessellator.startDrawingQuads();
	        GL11.glColor3f(colour[0], colour[1], colour[2]);
	        this.bindTextureByName(bannerEntity.getBannerBasePath());
	        tessellator.addVertexWithUV(x1, y1, z2, 0, 1);
	        tessellator.addVertexWithUV(x2, y1, z2, 1, 1);
	        tessellator.addVertexWithUV(x2, y2, z2, 1, 0);
	        tessellator.addVertexWithUV(x1, y2, z2, 0, 0);
	        tessellator.draw();
	        
	        
	        GL11.glPushMatrix();
	        GL11.glDepthFunc(GL11.GL_EQUAL);
	        //GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_BLEND);
		    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		    
		    colour = SigilHelper.getSecondaryColourArray(bannerEntity.getHeraldry());
	        
	        
	        GL11.glColor3f(colour[0], colour[1], colour[2]);
	        this.bindTextureByName(String.format("%sblocks/banner/pattern/banner-pattern-%s.png", BattleGear.imageFolder, SigilHelper.getPattern(bannerEntity.getHeraldry()).ordinal()));
	        tessellator.startDrawingQuads();
	        tessellator.addVertexWithUV(x1, y1, z2, 0, 1);
	        tessellator.addVertexWithUV(x2, y1, z2, 1, 1);
	        tessellator.addVertexWithUV(x2, y2, z2, 1, 0);
	        tessellator.addVertexWithUV(x1, y2, z2, 0, 0);
	        tessellator.draw();
	        
	        
	        float[] colourIconPrimary = SigilHelper.getSigilPrimaryColourArray(bannerEntity.getHeraldry());
		    float[] colourIconSecondary = SigilHelper.getSigilSecondaryColourArray(bannerEntity.getHeraldry());
		    
		    HeraldryBannerPositions position = HeraldryBannerPositions.values()[SigilHelper.getSigilPosition(bannerEntity.getHeraldry()).ordinal()];
		    HeraldryIcon sigil = SigilHelper.getSigil(bannerEntity.getHeraldry());	
		    
		    
		    
		    if(! HeraldryIcon.Blank.equals(sigil)){
			    this.bindTextureByName(sigil.getForegroundImagePath());
			    z2+=0.1/16F;
			    
			    GL11.glDepthFunc(GL11.GL_LEQUAL);
		        //GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glEnable(GL11.GL_BLEND);
			    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			    
			    
			    for(int i = 0; i < position.getPassess(); i++){
			    	tessellator.startDrawingQuads();
			    	x1 = position.getSourceXStart()[i];
			    	x2 = position.getSourceXEnd()[i];
			    	y1 = position.getSourceYStart()[i];
			    	y2 = position.getSourceYEnd()[i];
			    	boolean flip = position.getPatternFlip()[i];
			    	boolean flipColours = position.getAltColours()[i];
			    	
			    	if(flipColours){
			    		GL11.glColor3f(
			    				colourIconSecondary[0],
			    				colourIconSecondary[1],
			    				colourIconSecondary[2]);
			    	}else{
			    		GL11.glColor3f(colourIconPrimary[0],
			    				colourIconPrimary[1],
			    				colourIconPrimary[2]);
			    	}
			    	
			    	if(flip){
			    		
			    		tessellator.addVertexWithUV(x1, y1, z2, 1, 0);
			 	        tessellator.addVertexWithUV(x2, y1, z2, 0, 0);
			 	        tessellator.addVertexWithUV(x2, y2, z2, 0, 1);
			 	        tessellator.addVertexWithUV(x1, y2, z2, 1, 1);
			    	}else{
			    		tessellator.addVertexWithUV(x1, y1, z2, 0, 0);
			 	        tessellator.addVertexWithUV(x2, y1, z2, 1, 0);
			 	        tessellator.addVertexWithUV(x2, y2, z2, 1, 1);
			 	        tessellator.addVertexWithUV(x1, y2, z2, 0, 1);
			    	}
			    	tessellator.draw();
			    }
			    
			    
			    this.bindTextureByName(sigil.getBackgroundImagePath());
			    
			    for(int i = 0; i < position.getPassess(); i++){
			    	tessellator.startDrawingQuads();
			    	x1 = position.getSourceXStart()[i];
			    	x2 = position.getSourceXEnd()[i];
			    	y1 = position.getSourceYStart()[i];
			    	y2 = position.getSourceYEnd()[i];
			    	boolean flip = position.getPatternFlip()[i];
			    	boolean flipColours = position.getAltColours()[i];
			    	
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
			    		tessellator.addVertexWithUV(x1, y1, z2, 1, 0);
			 	        tessellator.addVertexWithUV(x2, y1, z2, 0, 0);
			 	        tessellator.addVertexWithUV(x2, y2, z2, 0, 1);
			 	        tessellator.addVertexWithUV(x1, y2, z2, 1, 1);
			    	}else{
			    		tessellator.addVertexWithUV(x1, y1, z2, 0, 0);
			 	        tessellator.addVertexWithUV(x2, y1, z2, 1, 0);
			 	        tessellator.addVertexWithUV(x2, y2, z2, 1, 1);
			 	        tessellator.addVertexWithUV(x1, y2, z2, 0, 1);
			    	}
			    	tessellator.draw();
			    }
			    
			    
			    GL11.glDisable(GL11.GL_BLEND);
		        GL11.glDepthMask(true);
		        //itemRenderer.zLevel += 50.0F;
		        //GL11.glEnable(GL11.GL_LIGHTING);
		        GL11.glDepthFunc(GL11.GL_LEQUAL);
			    
		    }
	        
	        
	        
	        
	        
	        
	        
	        
		    GL11.glDisable(GL11.GL_BLEND);
	        GL11.glDepthMask(true);
	        //itemRenderer.zLevel += 50.0F;
	        //GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glDepthFunc(GL11.GL_LEQUAL); 
	        
	        
	        GL11.glPopMatrix();
	        
	        
	        
	        
	        GL11.glPopMatrix();
		}
	        
    }
}
