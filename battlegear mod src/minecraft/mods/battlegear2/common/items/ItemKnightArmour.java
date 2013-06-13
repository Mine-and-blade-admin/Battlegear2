package mods.battlegear2.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IHeraldyArmour;
import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.client.heraldry.HeraldryArmourModel;
import mods.battlegear2.client.heraldry.HeraldyPattern;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.gui.ArmourSlot;
import mods.battlegear2.common.heraldry.KnightArmourRecipie;
import mods.battlegear2.common.heraldry.SigilHelper;
import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.IArmorTextureProvider;

public class ItemKnightArmour extends ItemArmor implements IHeraldyArmour, IArmorTextureProvider{
	
	private Icon baseIcon;
	private Icon postRenderIcon;
	private Icon trimRenderIcon;
	
	private Object modelObject;

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
	public Icon getBaseIcon(ItemStack stack) {
		return baseIcon;
	}

	@Override
	public Icon getPostRenderIcon(ItemStack stack) {
		return postRenderIcon;
	}
	
	@Override
	public Icon getTrimIcon(ItemStack stack) {
		return trimRenderIcon;
	}

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		
		if(stack.hasTagCompound()){
			if(stack.getTagCompound().hasKey("heraldry")){
				int oldcode = stack.getTagCompound().getInteger("heraldry");
				stack.getTagCompound().setByteArray("hc2", SigilHelper.translate(oldcode));
				stack.getTagCompound().removeTag("heraldry");
			}
		}
		
		return true;
	}
	
	

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		par3List.add(String.format("%s +%d %s", 
				EnumChatFormatting.BLUE, this.damageReduceAmount,
				StringTranslate.getInstance().translateKey("tooltip.armour.points")));
	}

	@Override
	public byte[] getHeraldryCode(ItemStack stack) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if(!stack.getTagCompound().hasKey("hc2")){
			stack.getTagCompound().setByteArray("hc2", SigilHelper.defaultSigil);
			return SigilHelper.defaultSigil;
		}else{
			return stack.getTagCompound().getByteArray("hc2");
		}
	}
	
	@Override
	public void setHeraldryCode(ItemStack stack, byte[] code){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByteArray("hc2", code);
	}

	@Override
	public void removeHeraldry(ItemStack item) {
		if(item.hasTagCompound()){
			item.getTagCompound().setByteArray("hc2", SigilHelper.defaultSigil);
		}
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
		
		if(modelObject == null){
			modelObject = new HeraldryArmourModel(armorType);
		}
		
		HeraldryArmourModel model = (HeraldryArmourModel)modelObject;
		
		
		model.setItemStack(itemStack);
		model.bipedHead.showModel = armorSlot == 0;

		model.bipedHeadwear.showModel = false;
		model.bipedBody.showModel = armorSlot == 1 || armorSlot == 2;
		model.bipedRightArm.showModel = armorSlot == 1;
		model.bipedLeftArm.showModel = armorSlot == 1;
		model.bipedRightLeg.showModel = armorSlot == 2 || armorSlot == 3;
		model.bipedLeftLeg.showModel = armorSlot == 2 || armorSlot == 3;
		
		if(entityLiving != null){
			model.heldItemRight = entityLiving.getHeldItem() == null?0:1;
			if(entityLiving instanceof EntityPlayer){
				if (entityLiving.getHeldItem() != null &&  ((EntityPlayer)entityLiving).getItemInUseCount() > 0){
					EnumAction enumaction = entityLiving.getHeldItem().getItemUseAction();
					if (enumaction == EnumAction.block){
		                model.heldItemRight = 3;
		            }
					model.aimedBow = enumaction == EnumAction.bow;
		        }
				
				model.heldItemLeft = ((EntityPlayer)entityLiving).inventory.getStackInSlot(
						((EntityPlayer)entityLiving).inventory.currentItem+InventoryPlayerBattle.WEAPON_SETS) == null?0:1;
				
			}
			model.isSneak = entityLiving.isSneaking();
		}
		
		
		return model;
	}

	@Override
	public String getBaseArmourPath(int armourSlot) {
		return BattleGear.imageFolder+"armours/knights/knights-base-"+(armourSlot==2?1:0)+".png";
	}

	@Override
	public String getPatternArmourPath(HeraldyPattern pattern, int armourSlot) {
		return BattleGear.imageFolder+"armours/knights/patterns/knights-pattern-"+(armourSlot==2?1:0)+"-"+pattern.ordinal()+".png";
	}
	
}
