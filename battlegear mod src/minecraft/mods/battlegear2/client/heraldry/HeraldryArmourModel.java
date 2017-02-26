package mods.battlegear2.client.heraldry;

import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.IHeraldyArmour;
import mods.battlegear2.api.heraldry.PatternStore;
import mods.battlegear2.heraldry.HelaldyArmourPositions;
import mods.battlegear2.heraldry.HeraldryIcon;
import mods.battlegear2.heraldry.SigilHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class HeraldryArmourModel extends ModelBiped{

	private ItemStack stack = ItemStack.EMPTY;
	private final EntityEquipmentSlot armourSlot;
	
	private boolean renderDecorations = false;
	private float helmOffset;
	
	public HeraldryArmourModel(EntityEquipmentSlot par1) {
		super(par1==EntityEquipmentSlot.LEGS ? 0.4F : 1F);
		this.armourSlot = par1;
		bipedHeadwear.cubeList.clear();
		if(armourSlot == EntityEquipmentSlot.LEGS){
			//bipedRightLeg = bipedRightLeg.setTextureOffset(40, 16);

			bipedRightLeg.cubeList.clear();
			bipedRightLeg.setTextureOffset(40, 16);
			bipedRightLeg.mirror = true;
			List<ModelBox> legBoxlist = bipedLeftLeg.cubeList;
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
	public void render(@Nullable Entity par1Entity, float par2, float par3, float par4,
					   float par5, float par6, float par7) {
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
		
		
		//Allows for proper enchantment rendering
		if(GL11.glIsEnabled(GL11.GL_BLEND)){
			
		}else if(stack.getItem() instanceof IHeraldyArmour){
			GlStateManager.pushMatrix();
			IHeraldyArmour heraldryItem = (IHeraldyArmour)stack.getItem();
			if(heraldryItem.hasHeraldry(stack)){
				byte[] code = heraldryItem.getHeraldry(stack);
				//if helmet
				if(armourSlot == EntityEquipmentSlot.HEAD && renderDecorations){
					if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 0);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 0);
					}
				}
				
				
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(heraldryItem.getBaseArmourPath(armourSlot)));//.bindTexture(heraldryItem.getBaseArmourPath(armourSlot));
				float[] colour = SigilHelper.getPrimaryColourArray(code);
				GlStateManager.color(colour[0], colour[1], colour[2]);
				this.bipedHead.render(par7);
	            this.bipedBody.render(par7);
	            this.bipedRightArm.render(par7);
	            this.bipedLeftArm.render(par7);
	            this.bipedRightLeg.render(par7);
	            this.bipedLeftLeg.render(par7);
	            this.bipedHeadwear.render(par7);
	            
	            if(armourSlot == EntityEquipmentSlot.HEAD && renderDecorations){
	            	if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 1);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 1);
					}
				}
	
	            colour = SigilHelper.getSecondaryColourArray(code);
				GlStateManager.color(colour[0], colour[1], colour[2]);

				GlStateManager.enableBlend();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
	            //GlStateManager.disableLighting();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
	            
	            FMLClientHandler.instance().getClient().renderEngine.bindTexture(
                        new ResourceLocation(heraldryItem.getPatternArmourPath(PatternStore.DEFAULT, new HeraldryData(code).getPatternIndex(), armourSlot)));
				GlStateManager.loadIdentity();

				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
	            this.bipedHead.render(par7);
	            this.bipedBody.render(par7);
	            this.bipedRightArm.render(par7);
	            this.bipedLeftArm.render(par7);
	            this.bipedRightLeg.render(par7);
	            this.bipedLeftLeg.render(par7);
	            this.bipedHeadwear.render(par7);
	            
	            if(armourSlot == EntityEquipmentSlot.HEAD && renderDecorations){
	            	if(par1Entity == null){
						renderHelmDecoration(0, SigilHelper.getHelm(code), 1);
					}else{
						renderHelmDecoration(par1Entity.getRotationYawHead(), SigilHelper.getHelm(code), 1);
					}
				}

				GlStateManager.disableLighting();
	            //If chestplate
	            if(armourSlot == EntityEquipmentSlot.CHEST){
	            	HeraldryIcon sigil = SigilHelper.getSigil(code);
	            	if(!sigil.equals(HeraldryIcon.Blank)){
		            	float[] colourIconPrimary = SigilHelper.getSigilPrimaryColourArray(code);
		            	float[] colourIconSecondary = SigilHelper.getSigilSecondaryColourArray(code);

						GlStateManager.pushMatrix();
		            	HelaldyArmourPositions pos = HelaldyArmourPositions.values()[SigilHelper.getSigilPosition(code).ordinal()];
		            	
		            	bipedBody.postRender(0.0625F);
						GlStateManager.translate(-5 * 0.0625F, 0.0625F, -3 * 0.0625F - 0.001F);
						GlStateManager.scale(0.6F, 0.6F, 1F);


						GlStateManager.matrixMode(GL11.GL_TEXTURE);
				    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(sigil.getForegroundImage());
						GlStateManager.loadIdentity();
						GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			            
		            	for(int pass = 0; pass < pos.getPassess(); pass++){
							GlStateManager.pushMatrix();
			            	float x = pos.getSourceX(pass);
					    	float y = pos.getSourceY(pass);
					    	float xEnd = pos.getXEnd(pass);
					    	float yEnd = pos.getYEnd(pass);
					    	boolean flipColours = pos.getAltColours(pass);
					    	
					    	if(flipColours){
								GlStateManager.color(colourIconSecondary[0],
										colourIconSecondary[1],
										colourIconSecondary[2]);
					    	}else{
								GlStateManager.color(colourIconPrimary[0],
										colourIconPrimary[1],
										colourIconPrimary[2]);
					    	}


							renderTexturedQuad(x, yEnd, xEnd, y);

							GlStateManager.popMatrix();
		            	}

						GlStateManager.matrixMode(GL11.GL_TEXTURE);
		    	    	FMLClientHandler.instance().getClient().renderEngine.bindTexture(sigil.getBackgroundImage());
						GlStateManager.loadIdentity();
						GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		            	
		            	for(int pass = 0; pass < pos.getPassess(); pass++){
							GlStateManager.pushMatrix();
			            	float x = pos.getSourceX(pass);
					    	float y = pos.getSourceY(pass);
					    	float xEnd = pos.getXEnd(pass);
					    	float yEnd = pos.getYEnd(pass);
					    	boolean flipColours = pos.getAltColours(pass);
					    	
					    	if(!flipColours){
								GlStateManager.color(colourIconSecondary[0],
										colourIconSecondary[1],
										colourIconSecondary[2]);
					    	}else{
								GlStateManager.color(colourIconPrimary[0],
										colourIconPrimary[1],
										colourIconPrimary[2]);
					    	}

							renderTexturedQuad(x, yEnd, xEnd, y);

							GlStateManager.popMatrix();
		            	}


						GlStateManager.popMatrix();
	            	}
	            }



				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.depthMask(true);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
	
			}

			GlStateManager.popMatrix();
		}

	}

	public static void renderTexturedQuad(float par1, float par2, float par3, float par4) {
		Tessellator par0Tessellator = Tessellator.getInstance();
		VertexBuffer renderer = par0Tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		renderer.pos(0, 0, 0).tex((double) par1, (double) par4).normal(0, 0, 1).endVertex();
		renderer.pos(1, 0, 0).tex((double) par3, (double) par4).normal(0, 0, 1).endVertex();
		renderer.pos(1, 1, 0).tex((double) par3, (double) par2).normal(0, 0, 1).endVertex();
		renderer.pos(0, 1, 0).tex((double) par1, (double) par2).normal(0, 0, 1).endVertex();
		par0Tessellator.draw();
    }


	public void renderHelmDecoration(float rot, byte style, int pass) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, helmOffset, 0);
		switch(style){
		case 0: //None
			break;
		case 1:
			GlStateManager.pushMatrix();
			bipedHead.postRender(0.0625F);	
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.translate(-1.25F+1F/16F, 0.5F, 0.5F/16);
			renderItemIn2D(1, 0.75F, 0.5F);
			GlStateManager.popMatrix();
			break;
		case 2: //Plume
			
			if(pass == 0){
				GlStateManager.pushMatrix();
				bipedHead.postRender(0.0625F);	
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(-1.25F+1F/16F+0.5F, 1F/16F, 0.5F/16);
				renderItemIn2D(0.75F, 0.5F, 0.5F);
				GlStateManager.popMatrix();
			}else{
				GlStateManager.pushMatrix();
				bipedHead.postRender(0.0625F);	
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(180, 1, 0, 0);
				GlStateManager.translate(-1.25F+1F/16F+0.5F, 1F/16F, 1F/16);
				GlStateManager.scale(1, 1, 2);

				renderItemIn2D(0.75F, 0.5F, 0.5F);
				GlStateManager.scale(1, 1,0.5F);
				GlStateManager.popMatrix();
			}
			break;
		case 3: //Horns
			GlStateManager.pushMatrix();
			bipedHead.postRender(0.0625F);	
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.scale(1.25F, 0.5F, 1.25F);
			GlStateManager.translate(-0.5F, 14F/16F, 0.5F/16);
			renderItemIn2D(0.5F, 0.25F, 0.25F);
			GlStateManager.scale(1F/1.25F, 1F/0.5F, 1F/1.25F);
			GlStateManager.popMatrix();
		}
		GlStateManager.translate(0, 0, -helmOffset);
		GlStateManager.popMatrix();
	}

	/**
	 * Renders an item held in hand as a 2D texture with thickness
	 */
	public static void renderItemIn2D(float p_78439_1_, float p_78439_3_, float p_78439_4_) {
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer p_78439_0_ = tess.getBuffer();
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		p_78439_0_.pos(0, 0, 0).tex((double) p_78439_1_, (double) p_78439_4_).normal(0, 0, 1).endVertex();
		p_78439_0_.pos(1, 0, 0).tex((double) p_78439_3_, (double) p_78439_4_).normal(0, 0, 1).endVertex();
		p_78439_0_.pos(1, 1, 0).tex((double) p_78439_3_, 0).normal(0, 0, 1).endVertex();
		p_78439_0_.pos(0, 1, 0).tex((double) p_78439_1_, 0).normal(0, 0, 1).endVertex();
		tess.draw();
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		p_78439_0_.pos(0, 1, -0.0625).tex((double) p_78439_1_, 0).normal(0, 0, -1).endVertex();
		p_78439_0_.pos(1, 1, -0.0625).tex((double) p_78439_3_, 0).normal(0, 0, -1).endVertex();
		p_78439_0_.pos(1, 0, -0.0625).tex((double) p_78439_3_, (double) p_78439_4_).normal(0, 0, -1).endVertex();
		p_78439_0_.pos(0, 0, -0.0625).tex((double) p_78439_1_, (double) p_78439_4_).normal(0, 0, -1).endVertex();
		tess.draw();
		float f5 = 0.5F * (p_78439_1_ - p_78439_3_) / (float) 64;
		float f6 = 0.5F * (p_78439_4_) / (float) 32;
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		int k;
		float f7;
		float f8;

		for (k = 0; k < 64; ++k) {
			f7 = (float) k / (float) 64;
			f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
			p_78439_0_.pos((double) f7, 0, -0.0625).tex((double) f8, (double) p_78439_4_).normal(-1, 0, 0).endVertex();
			p_78439_0_.pos((double) f7, 0, 0).tex((double) f8, (double) p_78439_4_).normal(-1, 0, 0).endVertex();
			p_78439_0_.pos((double) f7, 1, 0).tex((double) f8, 0).normal(-1, 0, 0).endVertex();
			p_78439_0_.pos((double) f7, 1, -0.0625).tex((double) f8, 0).normal(-1, 0, 0).endVertex();
		}

		tess.draw();
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
		float f9;

		for (k = 0; k < 64; ++k) {
			f7 = (float) k / (float) 64;
			f8 = p_78439_1_ + (p_78439_3_ - p_78439_1_) * f7 - f5;
			f9 = f7 + 1.0F / (float) 64;
			p_78439_0_.pos((double) f9, 1, -0.0625).tex((double) f8, 0).normal(1, 0, 0).endVertex();
			p_78439_0_.pos((double) f9, 1, 0).tex((double) f8, 0).normal(1, 0, 0).endVertex();
			p_78439_0_.pos((double) f9, 0, 0).tex((double) f8, (double) p_78439_4_).normal(1, 0, 0).endVertex();
			p_78439_0_.pos((double) f9, 0, -0.0625).tex((double) f8, (double) p_78439_4_).normal(1, 0, 0).endVertex();
		}

		tess.draw();
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

		for (k = 0; k < 32; ++k) {
			f7 = (float) k / (float) 32;
			f8 = p_78439_4_ - p_78439_4_ * f7 - f6;
			f9 = f7 + 1.0F / (float) 32;
			p_78439_0_.pos(0, (double) f9, 0).tex((double) p_78439_1_, (double) f8).normal(0, 1, 0).endVertex();
			p_78439_0_.pos(1, (double) f9, 0).tex((double) p_78439_3_, (double) f8).normal(0, 1, 0).endVertex();
			p_78439_0_.pos(1, (double) f9, -0.0625).tex((double) p_78439_3_, (double) f8).normal(0, 1, 0).endVertex();
			p_78439_0_.pos(0, (double) f9, -0.0625).tex((double) p_78439_1_, (double) f8).normal(0, 1, 0).endVertex();
		}

		tess.draw();
		p_78439_0_.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

		for (k = 0; k < 32; ++k) {
			f7 = (float) k / (float) 32;
			f8 = p_78439_4_ - p_78439_4_ * f7 - f6;
			p_78439_0_.pos(1, (double) f7, 0).tex((double) p_78439_3_, (double) f8).normal(0, -1, 0).endVertex();
			p_78439_0_.pos(0, (double) f7, 0).tex((double) p_78439_1_, (double) f8).normal(0, -1, 0).endVertex();
			p_78439_0_.pos(0, (double) f7, -0.0625).tex((double) p_78439_1_, (double) f8).normal(0, -1, 0).endVertex();
			p_78439_0_.pos(1, (double) f7, -0.0625).tex((double) p_78439_3_, (double) f8).normal(0, -1, 0).endVertex();
		}

		tess.draw();
	}
}
