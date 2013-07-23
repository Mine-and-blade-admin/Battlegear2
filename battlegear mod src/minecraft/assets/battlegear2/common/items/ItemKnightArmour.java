package assets.battlegear2.common.items;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import assets.battlegear2.api.IHeraldyArmour;
import assets.battlegear2.api.IHeraldyItem;
import assets.battlegear2.client.heraldry.HeraldryArmourModel;
import assets.battlegear2.client.heraldry.HeraldyPattern;
import assets.battlegear2.common.BattleGear;
import assets.battlegear2.common.heraldry.SigilHelper;
import assets.battlegear2.common.inventory.InventoryPlayerBattle;
import assets.battlegear2.common.utils.BattlegearConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKnightArmour extends ItemArmor implements IHeraldyArmour/*, IArmorTextureProvider*/{
	
	private Icon baseIcon[];
	private Icon postRenderIcon[];
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
		
		if(armorType == 0){
			baseIcon = new Icon[4];
			postRenderIcon = new Icon[4];
			for(int i = 0; i < 4; i ++){
				baseIcon[i] = par1IconRegister.registerIcon("battlegear2:armours/knight-base-"+BattlegearConfig.armourTypes[armorType]+"-"+i);
				postRenderIcon[i] =  par1IconRegister.registerIcon("battlegear2:armours/knight-post-"+BattlegearConfig.armourTypes[armorType]+"-"+i);
			}
		}else{
			baseIcon = new Icon[1];
			postRenderIcon = new Icon[1];
			baseIcon[0] = par1IconRegister.registerIcon("battlegear2:armours/knight-base-"+BattlegearConfig.armourTypes[armorType]);
			postRenderIcon[0] = par1IconRegister.registerIcon("battlegear2:armours/knight-post-"+BattlegearConfig.armourTypes[armorType]);
		}
	
		if(armorType == 2){
			trimRenderIcon = par1IconRegister.registerIcon("battlegear2:armours/knight-trim-"+BattlegearConfig.armourTypes[armorType]);
		}
	}

	@Override
	public Icon getBaseIcon(ItemStack stack) {
		if(armorType == 0){
			return baseIcon[SigilHelper.getHelm(((IHeraldyItem)stack.getItem()).getHeraldryCode(stack))];
		}else
			return baseIcon[0];
	}

	@Override
	public Icon getPostRenderIcon(ItemStack stack) {
		if(armorType == 0){
			return postRenderIcon[SigilHelper.getHelm(((IHeraldyItem)stack.getItem()).getHeraldryCode(stack))];
		}else 
			return postRenderIcon[0];
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
				StatCollector.translateToLocal("tooltip.armour.points")));
	}

	@Override
	public byte[] getHeraldryCode(ItemStack stack) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if(!stack.getTagCompound().hasKey("hc2")){
			stack.getTagCompound().setByteArray("hc2", SigilHelper.getDefault());
			return SigilHelper.getDefault();
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
			item.getTagCompound().setByteArray("hc2", SigilHelper.getDefault());
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

	//@Override
	//public String getArmorTextureFile(ItemStack itemstack) {
	//	return null;
		//return BattleGear.imageFolder+"armours/knights/knights-"+(slot==2?1:0)+".png";
	//}

	@Override
	public boolean useDefaultRenderer() {
		return true;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return "/textures/armours/knights/knights-"+(slot==2?1:0)+".png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		
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
			if(entityLiving instanceof EntityPlayer)
			{
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
