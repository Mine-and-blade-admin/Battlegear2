package mods.battlegear2.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IHeraldryItem;
import mods.battlegear2.client.heraldry.SigilHelper;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.gui.ArmourSlot;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.common.IArmorTextureProvider;

public class ItemKnightArmour extends ItemArmor implements IHeraldryItem, IArmorTextureProvider{
	
	private Icon baseIcon;
	private Icon postRenderIcon;
	private Icon trimRenderIcon;

	public ItemKnightArmour(int id, int armourType) {
		super(id, BattleGear.knightArmourMaterial, 1, armourType);
		this.setCreativeTab(BattlegearConfig.customTab);
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
	public boolean shouldDoPass(HeraldryRenderPassess pass) {
		if(armorType == 1){
			return ! pass.equals(HeraldryRenderPassess.SecondaryColourTrim);
		}
		else if (armorType == 2){
			return pass.equals(HeraldryRenderPassess.PrimaryColourBase) || pass.equals(HeraldryRenderPassess.SecondaryColourTrim);
		}else{
			return pass.equals(HeraldryRenderPassess.PrimaryColourBase);
		}
	}

	@Override
	public String getArmorTextureFile(ItemStack itemstack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return BattleGear.imageFolder+"armours/knights/knights-"+(slot==2?1:0)+".png";
	}
	
	

}
