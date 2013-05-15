package mods.battlegear2.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.client.heraldry.HeraldryIcon;
import mods.battlegear2.client.heraldry.HeraldryPattern;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;

public class ItemHeradryIcon extends Item implements IHeraldryItem{

	
	Icon base;
	Icon trim;
	
	public ItemHeradryIcon(int par1) {
		super(par1);
		//temp
		this.setCreativeTab(BattlegearConfig.customTab);
		this.setMaxStackSize(1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		BattleGear.proxy.registerTextures(par1IconRegister);
		base = par1IconRegister.registerIcon("battlegear2:heraldry-base");
		trim = par1IconRegister.registerIcon("battlegear2:heraldry-trim");
	}
	
	@Override
	public Icon getBaseIcon() {
		return base;
	}
	
	

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public Icon getPostRenderIcon() {
		return trim;
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
		
		NBTTagCompound compound = stack.getTagCompound();
		if(compound.hasKey("heraldry")){
			return compound.getInteger("heraldry");
		}else{
			compound.setInteger("heraldry",0);
			return 0;
		}
	}
	
	

}
