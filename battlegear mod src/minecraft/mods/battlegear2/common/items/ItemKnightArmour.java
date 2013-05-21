package mods.battlegear2.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IHeraldyArmour;
import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.client.heraldry.HeraldryArmourModel;
import mods.battlegear2.client.heraldry.HeraldyPattern;
import mods.battlegear2.client.heraldry.SigilHelper;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.gui.ArmourSlot;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.common.IArmorTextureProvider;

public class ItemKnightArmour extends ItemArmor implements IHeraldyArmour, IArmorTextureProvider{
	
	private Icon baseIcon;
	private Icon postRenderIcon;
	private Icon trimRenderIcon;

	public ItemKnightArmour(int id, int armourType) {
		super(id, BattleGear.knightArmourMaterial, 1, armourType);
		this.setCreativeTab(BattlegearConfig.customTab);
		setUnlocalizedName("battlegear2:knights_armour."+BattlegearConfig.armourTypes[armourType]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		//super.registerIcons(par1IconRegister);
		baseIcon = par1IconRegister.registerIcon("battlegear2:armours/knight-base-"+BattlegearConfig.armourTypes[armorType]);
		postRenderIcon = par1IconRegister.registerIcon("battlegear2:armours/knight-post-"+BattlegearConfig.armourTypes[armorType]);
	
		if(armorType == 2){
			trimRenderIcon = par1IconRegister.registerIcon("battlegear2:armours/knight-trim-"+BattlegearConfig.armourTypes[armorType]);
		}
	}

	@Override
	public Icon getBaseIcon() {
		return baseIcon;
	}

	@Override
	public Icon getPostRenderIcon() {
		return postRenderIcon;
	}
	
	@Override
	public Icon getTrimIcon() {
		return trimRenderIcon;
	}

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		return true;
	}

	@Override
	public int getHeraldryCode(ItemStack stack) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if(!stack.getTagCompound().hasKey("heraldry")){
			stack.getTagCompound().setInteger("heraldry", SigilHelper.defaultSigil);
			return SigilHelper.defaultSigil;
		}else{
			return stack.getTagCompound().getInteger("heraldry");
		}
	}
	
	public void setHeraldryCode(ItemStack stack, int code){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("heraldry", code);
	}

	@Override
	public boolean shouldDoPass(HeraldyRenderPassess pass) {
		
		if(pass.equals(HeraldyRenderPassess.PrimaryColourBase) || 
				pass.equals(HeraldyRenderPassess.SecondaryColourTrim) ||
				pass.equals(HeraldyRenderPassess.PostRenderIcon)){
			return true;
		}else
			return armorType==1;
		
	}
	
	


	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		return null;
		//return BattleGear.imageFolder+"armours/knights/knights-"+(slot==2?1:0)+".png";
	}

	@Override
	public boolean useDefaultRenderer() {
		return true;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return BattleGear.imageFolder+"armours/knights/knights-"+(slot==2?1:0)+".png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLiving entityLiving,
			ItemStack itemStack, int armorSlot) {
		//TODO: Possibly not the most efficiant, maybe we should change this to using static references for better preformance
		HeraldryArmourModel model = new HeraldryArmourModel(armorSlot);
		model.setItemStack(itemStack);
		model.bipedHead.showModel = armorSlot == 0;

		model.bipedHeadwear.showModel = armorSlot == 0;
		model.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
		model.bipedRightArm.showModel = armorSlot == 1;
		model.bipedLeftArm.showModel = armorSlot == 1;
		model.bipedRightLeg.showModel = armorSlot == 2 || armorSlot == 3;
		model.bipedLeftLeg.showModel = armorSlot == 2 || armorSlot == 3;
		
		return model;
	}

	@Override
	public String getBaseArmourPath(int armourSlot) {
		return BattleGear.imageFolder+"armours/knights/knights-base-"+(armourSlot==2?1:0)+".png";
	}

	@Override
	public String getPatternArmourPath(HeraldyPattern pattern, int armourSlot) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

}
