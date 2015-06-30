package mods.battlegear2.client.heraldry;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.IHeraldyArmour;
import mods.battlegear2.api.heraldry.PatternStore;
import mods.battlegear2.heraldry.HelaldyArmourPositions;
import mods.battlegear2.heraldry.HeraldryIcon;
import mods.battlegear2.heraldry.SigilHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class HeraldryArmourModel extends ModelBiped{

	ItemStack stack;
	int armourSlot;
	
	public boolean renderDecorations = true;
	public float helmOffset;
	
	public HeraldryArmourModel(int par1) {
		super(par1==2 ? 0.4F : 1F);
		this.armourSlot = par1;
		
		if(armourSlot == 2){
			//bipedRightLeg = bipedRightLeg.setTextureOffset(40, 16);
			
			List<ModelBox> legBoxlist = bipedLeftLeg.cubeList;
			bipedRightLeg.cubeList.clear();
			bipedRightLeg.setTextureOffset(40, 16);
			bipedRightLeg.mirror = true;
			for (ModelBox modelBox : legBoxlist) {
				bipedRightLeg.addBox(modelBox.posX1, modelBox.posY1, modelBox.posZ1, 
						(int)(modelBox.posX2 - modelBox.posX1),
						(int)(modelBox.posY2 - modelBox.posY1),
						(int)(modelBox.posZ2 - modelBox.posZ1));
			}
			
		}
	}
	
	public void setItemStack(ItemStack stack){
		this.stack = stack;
		setInvisible(false);
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4,
			float par5, float par6, float par7) {
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
		
		
		//Allows for proper enchantment rendering
		if(GL11.glIsEnabled(GL11.GL_BLEND)){
			
		}else{
			GL11.glPushMatrix();
			IHeraldyArmour heraldryItem = (IHeraldyArmour)stack.getItem();
			if(stack != null && heraldryItem.hasHeraldry(stack)){
				byte[] code = heraldryItem.getHeraldry(stack);
				//if helmet
				if(armourSlot == 0 && renderDecorations){
					if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 0);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 0);
					}
				}
				
				
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(heraldryItem.getBaseArmourPath(armourSlot)));//.bindTexture(heraldryItem.getBaseArmourPath(armourSlot));
				float[] colour = SigilHelper.getPrimaryColourArray(code);
				GL11.glColor3f(colour[0], colour[1], colour[2]);
				this.bipedHead.render(par7);
	            this.bipedBody.render(par7);
	            this.bipedRightArm.render(par7);
	            this.bipedLeftArm.render(par7);
	            this.bipedRightLeg.render(par7);
	            this.bipedLeftLeg.render(par7);
	            this.bipedHeadwear.render(par7);
	            
	            if(armourSlot == 0 && renderDecorations){
	            	if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 1);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 1);
					}
				}
	
	            colour = SigilHelper.getSecondaryColourArray(code);
	            GL11.glColor3f(colour[0], colour[1], colour[2]);
	            
	            GL11.glEnable(GL11.GL_BLEND);
	            GL11.glDepthFunc(GL11.GL_LEQUAL);
	            //GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GL11.glMatrixMode(GL11.GL_TEXTURE);
	            
	            FMLClientHandler.instance().getClient().renderEngine.bindTexture(
                        new ResourceLocation(heraldryItem.getPatternArmourPath(PatternStore.DEFAULT, new HeraldryData(code).getPatternIndex(), armourSlot)));
	            GL11.glLoadIdentity();
	            
	            GL11.glMatrixMode(GL11.GL_MODELVIEW);
	            this.bipedHead.render(par7);
	            this.bipedBody.render(par7);
	            this.bipedRightArm.render(par7);
	            this.bipedLeftArm.render(par7);
	            this.bipedRightLeg.render(par7);
	            this.bipedLeftLeg.render(par7);
	            this.bipedHeadwear.render(par7);
	            
	            if(armourSlot == 0 && renderDecorations){
	            	if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 1);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 1);
					}
				}
	            
	            GL11.glDisable(GL11.GL_LIGHTING);
	            //If chestplate
	            if(armourSlot == 1){
	            	HeraldryIcon sigil = SigilHelper.getSigil(code);
	            	if(!sigil.equals(HeraldryIcon.Blank)){
		            	float[] colourIconPrimary = SigilHelper.getSigilPrimaryColourArray(code);
		            	float[] colourIconSecondary = SigilHelper.getSigilSecondaryColourArray(code);
		            	
		            	GL11.glPushMatrix();
		            	HelaldyArmourPositions pos = HelaldyArmourPositions.values()[SigilHelper.getSigilPosition(code).ordinal()];
		            	
		            	bipedBody.postRender(0.0625F);
		            	GL11.glTranslatef(-5*0.0625F, 0.0625F, -3*0.0625F-0.001F);
		            	GL11.glScalef(0.6F, 0.6F, 1F);
		            	
		            	
		            	GL11.glMatrixMode(GL11.GL_TEXTURE);
				    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(sigil.getForegroundImage());
				    	GL11.glLoadIdentity();
			            GL11.glMatrixMode(GL11.GL_MODELVIEW);
			            
		            	for(int pass = 0; pass < pos.getPassess(); pass++){	
		            		GL11.glPushMatrix();
			            	float x = pos.getSourceX(pass);
					    	float y = pos.getSourceY(pass);
					    	float xEnd = pos.getXEnd(pass);
					    	float yEnd = pos.getYEnd(pass);
					    	boolean flipColours = pos.getAltColours(pass);
					    	
					    	if(flipColours){
					    		GL11.glColor3f(colourIconSecondary[0],
					    				colourIconSecondary[1],
					    				colourIconSecondary[2]);
					    	}else{
					    		GL11.glColor3f(colourIconPrimary[0],
					    				colourIconPrimary[1],
					    				colourIconPrimary[2]);
					    	}


							renderTexturedQuad(x, yEnd, xEnd, y);

							GL11.glPopMatrix();
		            	}
		            	
		            	GL11.glMatrixMode(GL11.GL_TEXTURE);
		    	    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(sigil.getBackgroundImage());
		    	    	GL11.glLoadIdentity();
		                GL11.glMatrixMode(GL11.GL_MODELVIEW);
		            	
		            	for(int pass = 0; pass < pos.getPassess(); pass++){	
		            		GL11.glPushMatrix();
			            	float x = pos.getSourceX(pass);
					    	float y = pos.getSourceY(pass);
					    	float xEnd = pos.getXEnd(pass);
					    	float yEnd = pos.getYEnd(pass);
					    	boolean flipColours = pos.getAltColours(pass);
					    	
					    	if(!flipColours){
					    		GL11.glColor3f(colourIconSecondary[0],
					    				colourIconSecondary[1],
					    				colourIconSecondary[2]);
					    	}else{
					    		GL11.glColor3f(colourIconPrimary[0],
					    				colourIconPrimary[1],
					    				colourIconPrimary[2]);
					    	}

							renderTexturedQuad(x, yEnd, xEnd, y);

							GL11.glPopMatrix();
		            	}
		            	
		            	
		            	GL11.glPopMatrix();
	            	}
	            }
	            
	            
	
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GL11.glMatrixMode(GL11.GL_TEXTURE);
	            GL11.glDepthMask(true);
	            GL11.glLoadIdentity();
	            GL11.glMatrixMode(GL11.GL_MODELVIEW);
	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glDisable(GL11.GL_BLEND);
	            GL11.glDepthFunc(GL11.GL_LEQUAL);
	
			}
			
			GL11.glPopMatrix();
		}

	}

	public static void renderTexturedQuad(float par1, float par2, float par3, float par4) {
		Tessellator par0Tessellator = Tessellator.getInstance();
		WorldRenderer renderer = par0Tessellator.getWorldRenderer();
		renderer.startDrawingQuads();
		renderer.setNormal(0.0F, 0.0F, 1.0F);
		renderer.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) par1, (double) par4);
		renderer.addVertexWithUV(1.0D, 0.0D, 0.0D, (double) par3, (double) par4);
		renderer.addVertexWithUV(1.0D, 1.0D, 0.0D, (double) par3, (double) par2);
		renderer.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) par1, (double) par2);
		par0Tessellator.draw();
    }


	public void renderHelmDecoration(float rot, byte style, int pass) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, helmOffset, 0);
		switch(style){
		case 0: //None
			break;
		case 1:
			GL11.glPushMatrix();
			bipedHead.postRender(0.0625F);	
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glTranslatef(-1.25F+1F/16F, 0.5F, 0.5F/16);
			renderItemIn2D(1, 0.75F, 0.5F);
			GL11.glPopMatrix();
			break;
		case 2: //Plume
			
			if(pass == 0){
				GL11.glPushMatrix();
				bipedHead.postRender(0.0625F);	
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glRotatef(180, 1, 0, 0);
				GL11.glTranslatef(-1.25F+1F/16F+0.5F, 1F/16F, 0.5F/16);
				renderItemIn2D(0.75F, 0.5F, 0.5F);
				GL11.glPopMatrix();
			}else{
				GL11.glPushMatrix();
				bipedHead.postRender(0.0625F);	
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glRotatef(180, 1, 0, 0);
				GL11.glTranslatef(-1.25F+1F/16F+0.5F, 1F/16F, 1F/16);
				GL11.glScalef(1, 1, 2);

				renderItemIn2D(0.75F, 0.5F, 0.5F);
				GL11.glScalef(1, 1,0.5F);
				GL11.glPopMatrix();
			}
			break;
		case 3: //Horns
			GL11.glPushMatrix();
			bipedHead.postRender(0.0625F);	
			GL11.glRotatef(180, 1, 0, 0);
			GL11.glScalef(1.25F, 0.5F, 1.25F);
			GL11.glTranslatef(-0.5F, 14F/16F, 0.5F/16);
			renderItemIn2D(0.5F, 0.25F, 0.25F);
			GL11.glScalef(1F/1.25F, 1F/0.5F, 1F/1.25F);
			GL11.glPopMatrix();
		}
		GL11.glTranslatef(0, 0, -helmOffset);
		GL11.glPopMatrix();
	}

	/**
	 * Renders an item held in hand as a 2D texture with thickness
	 */
	public static void renderItemIn2D(float p_78439_1_, float p_78439_3_, float p_78439_4_) {
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer p_78439_0_ = tess.getWorldRenderer();
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(0.0F, 0.0F, 1.0F);
		p_78439_0_.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) p_78439_1_, (double) p_78439_4_);
		p_78439_0_.addVertexWithUV(1.0D, 0.0D, 0.0D, (double) p_78439_3_, (double) p_78439_4_);
		p_78439_0_.addVertexWithUV(1.0D, 1.0D, 0.0D, (double) p_78439_3_, 0);
		p_78439_0_.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) p_78439_1_, 0);
		tess.draw();
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(0.0F, 0.0F, -1.0F);
		p_78439_0_.addVertexWithUV(0.0D, 1.0D, -0.0625, (double) p_78439_1_, 0);
		p_78439_0_.addVertexWithUV(1.0D, 1.0D, -0.0625, (double) p_78439_3_, 0);
		p_78439_0_.addVertexWithUV(1.0D, 0.0D, -0.0625, (double) p_78439_3_, (double) p_78439_4_);
		p_78439_0_.addVertexWithUV(0.0D, 0.0D, -0.0625, (double) p_78439_1_, (double) p_78439_4_);
		tess.draw();
		float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float) 64;
		float f6 = 0.5F * (p_78439_4_) / (float) 32;
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(-1.0F, 0.0F, 0.0F);
		int k;
		float f7;
		float f8;

		for (k = 0; k < 64; ++k) {
			f7 = (float) k / (float) 64;
			f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
			p_78439_0_.addVertexWithUV((double) f7, 0.0D, -0.0625, (double) f8, (double) p_78439_4_);
			p_78439_0_.addVertexWithUV((double) f7, 0.0D, 0.0D, (double) f8, (double) p_78439_4_);
			p_78439_0_.addVertexWithUV((double) f7, 1.0D, 0.0D, (double) f8, 0);
			p_78439_0_.addVertexWithUV((double) f7, 1.0D, -0.0625, (double) f8, 0);
		}

		tess.draw();
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(1.0F, 0.0F, 0.0F);
		float f9;

		for (k = 0; k < 64; ++k) {
			f7 = (float) k / (float) 64;
			f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
			f9 = f7 + 1.0F / (float) 64;
			p_78439_0_.addVertexWithUV((double) f9, 1.0D, -0.0625, (double) f8, 0);
			p_78439_0_.addVertexWithUV((double) f9, 1.0D, 0.0D, (double) f8, 0);
			p_78439_0_.addVertexWithUV((double) f9, 0.0D, 0.0D, (double) f8, (double) p_78439_4_);
			p_78439_0_.addVertexWithUV((double) f9, 0.0D, -0.0625, (double) f8, (double) p_78439_4_);
		}

		tess.draw();
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(0.0F, 1.0F, 0.0F);

		for (k = 0; k < 32; ++k) {
			f7 = (float) k / (float) 32;
			f8 = p_78439_4_ - p_78439_4_ * f7 - f6;
			f9 = f7 + 1.0F / (float) 32;
			p_78439_0_.addVertexWithUV(0.0D, (double) f9, 0.0D, (double) p_78439_1_, (double) f8);
			p_78439_0_.addVertexWithUV(1.0D, (double) f9, 0.0D, (double) p_78439_3_, (double) f8);
			p_78439_0_.addVertexWithUV(1.0D, (double) f9, -0.0625, (double) p_78439_3_, (double) f8);
			p_78439_0_.addVertexWithUV(0.0D, (double) f9, -0.0625, (double) p_78439_1_, (double) f8);
		}

		tess.draw();
		p_78439_0_.startDrawingQuads();
		p_78439_0_.setNormal(0.0F, -1.0F, 0.0F);

		for (k = 0; k < 32; ++k) {
			f7 = (float) k / (float) 32;
			f8 = p_78439_4_ - p_78439_4_ * f7 - f6;
			p_78439_0_.addVertexWithUV(1.0D, (double) f7, 0.0D, (double) p_78439_3_, (double) f8);
			p_78439_0_.addVertexWithUV(0.0D, (double) f7, 0.0D, (double) p_78439_1_, (double) f8);
			p_78439_0_.addVertexWithUV(0.0D, (double) f7, -0.0625, (double) p_78439_1_, (double) f8);
			p_78439_0_.addVertexWithUV(1.0D, (double) f7, -0.0625, (double) p_78439_3_, (double) f8);
		}

		tess.draw();
	}
}
