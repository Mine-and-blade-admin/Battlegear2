package mods.battlegear2.client.heraldry;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.api.IHeraldyArmour;
import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.common.BattleGear;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

public class HeraldryArmourModel extends ModelBiped{
	ItemStack stack;
	int armourSlot;
	
	public HeraldryArmourModel(int par1) {
		super(par1==2 ? 0.5F : 1F);
		this.armourSlot = par1;
	}
	
	public void setItemStack(ItemStack stack){
		this.stack = stack;
	}

	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4,
			float par5, float par6, float par7) {
		super.render(par1Entity, par2, par3, par4, par5, par6, par7);
		
		IHeraldyArmour heraldryItem = (IHeraldyArmour)stack.getItem();
		if(heraldryItem.hasHeraldry(stack)){
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(heraldryItem.getBaseArmourPath(armourSlot));
			float[] colour = SigilHelper.convertColourToARGBArray(
								SigilHelper.colours[
					                    SigilHelper.getColour1(heraldryItem.getHeraldryCode(stack))]
							);
			GL11.glColor3f(colour[2], colour[1], colour[0]);
			
			this.bipedHead.render(par7);
            this.bipedBody.render(par7);
            this.bipedRightArm.render(par7);
            this.bipedLeftArm.render(par7);
            this.bipedRightLeg.render(par7);
            this.bipedLeftLeg.render(par7);
            this.bipedHeadwear.render(par7);		
		}
		//Set new image path
		//re-render
	}
	
	
}
