package mods.battlegear2.items;

import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.heraldry.SigilHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHeradryIcon extends Item implements IHeraldryItem {

	IIcon base;
	IIcon trim;
	
	public ItemHeradryIcon() {
		super();
		//this.setCreativeTab(BattlegearConfig.customTab);
		this.setMaxStackSize(1);
		setUnlocalizedName("battlegear2:heraldric");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("battlegear2:bg-icon");
		base = par1IconRegister.registerIcon("battlegear2:heraldry-base");
		trim = par1IconRegister.registerIcon("battlegear2:heraldry-trim");
	}
	
	@Override
	public IIcon getBaseIcon(ItemStack stack) {
		return base;
	}
	
	@Override
	public IIcon getTrimIcon(ItemStack stack) {
		return null;
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack){
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return itemStack;
	}

	@Override
	public IIcon getPostRenderIcon(ItemStack stack) {
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
	public byte[] getHeraldry(ItemStack stack) {
		if(!stack.hasTagCompound()){
			return SigilHelper.getDefault();
		}
		NBTTagCompound compound = stack.getTagCompound();
		if(compound.hasKey("hc2")){
			return compound.getByteArray("hc2");
		}else{
			return SigilHelper.getDefault();
		}
	}

	@Override
	public void removeHeraldry(ItemStack item) {
		if(item.hasTagCompound()){
			item.getTagCompound().removeTag("hc2");
		}
	}

	@Override
	public void setHeraldry(ItemStack stack, byte[] code) {
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
