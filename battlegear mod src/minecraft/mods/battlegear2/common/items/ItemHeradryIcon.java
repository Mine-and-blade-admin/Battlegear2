package mods.battlegear2.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.api.IHeraldyItem.HeraldyRenderPassess;
import mods.battlegear2.client.heraldry.HeraldryIcon;
import mods.battlegear2.client.heraldry.HeraldyPattern;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.heraldry.SigilHelper;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;

public class ItemHeradryIcon extends Item implements IHeraldyItem{

	Icon base;
	Icon trim;
	
	public ItemHeradryIcon(int par1) {
		super(par1);
		//this.setCreativeTab(BattlegearConfig.customTab);
		this.setMaxStackSize(1);
		setUnlocalizedName("battlegear2:heraldric");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		BattleGear.proxy.registerTextures(par1IconRegister);
		this.itemIcon = par1IconRegister.registerIcon("battlegear2:bg-icon");
		base = par1IconRegister.registerIcon("battlegear2:heraldry-base");
		trim = par1IconRegister.registerIcon("battlegear2:heraldry-trim");
	}
	
	@Override
	public Icon getBaseIcon(ItemStack stack) {
		return base;
	}
	
	@Override
	public Icon getTrimIcon(ItemStack stack) {
		return null;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}
	
	
	@Override
	public boolean hasContainerItem(){
		return true;
	}
	
	@Override
	public ItemStack getContainerItemStack(ItemStack itemStack) {
		return itemStack;
	}

	@Override
	public Icon getPostRenderIcon(ItemStack stack) {
		return trim;
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
		
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("hc2");
	}
	
	@Override
	public byte[] getHeraldryCode(ItemStack stack) {
		if(!stack.hasTagCompound()){
			return SigilHelper.defaultSigil;
		}
		NBTTagCompound compound = stack.getTagCompound();
		if(compound.hasKey("hc2")){
			return compound.getByteArray("hc2");
		}else{
			return SigilHelper.defaultSigil;
		}
	}

	@Override
	public void removeHeraldry(ItemStack item) {
		if(item.hasTagCompound()){
			item.getTagCompound().removeTag("hc2");
		}
	}

	@Override
	public void setHeraldryCode(ItemStack stack, byte[] code) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		stack.getTagCompound().setByteArray("hc2", code);
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public boolean useDefaultRenderer() {
		return false;
	}

	@Override
	public boolean shouldDoPass(HeraldyRenderPassess pass) {
		return ! pass.equals(HeraldyRenderPassess.SecondaryColourTrim);
	}
}
