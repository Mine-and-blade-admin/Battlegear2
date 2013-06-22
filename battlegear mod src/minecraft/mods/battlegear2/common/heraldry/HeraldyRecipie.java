package mods.battlegear2.common.heraldry;

import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class HeraldyRecipie implements IRecipe{
	protected Item heraldricWeapon;
	
	public HeraldyRecipie(Item item){
		this.heraldricWeapon = item;
	}

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		
		boolean itemFound = false;
		boolean iconFound = false;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(stack != null){
				if(stack.getItem().itemID == BattlegearConfig.heradricItem.itemID ||
						stack.getItem().itemID == Item.bucketWater.itemID){
					if(iconFound)
						return false;
					else
						iconFound = true;
				}else if (stack.getItem().itemID == heraldricWeapon.itemID){
					if(itemFound)
						return false;
					else
						itemFound = true;
				}
			}
		}
		
		return itemFound && iconFound;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		
		ItemStack item = null;
		ItemStack icon = null;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			
			if(stack != null){
				if(stack.getItem().itemID == BattlegearConfig.heradricItem.itemID){
					icon = stack;
				}else if (stack.getItem().itemID == heraldricWeapon.itemID){
					item = stack;
				}else if (stack.getItem().itemID == Item.bucketWater.itemID){
					icon = stack;
				}
			}
			
		}
		
		item=item.copy();
		
		if(heraldricWeapon instanceof IHeraldyItem){
			byte[] code = SigilHelper.getDefault();
			if(icon.getItem().itemID == BattlegearConfig.heradricItem.itemID){
				code = ((IHeraldyItem)icon.getItem()).getHeraldryCode(icon);
			}
			((IHeraldyItem) heraldricWeapon).setHeraldryCode(item, code);
		}else{
			
			NBTTagCompound compound = item.getTagCompound();
			if(compound == null){
				compound = new NBTTagCompound();
			}
			if(icon.getItem().itemID == BattlegearConfig.heradricItem.itemID){
				byte[] code = ((IHeraldyItem)icon.getItem()).getHeraldryCode(icon);
				compound.setByteArray("hc2", code);
				item.setTagCompound(compound);
			}else{ // should be a bucket
				if(compound.hasKey("hc2")){
					compound.removeTag("hc2");
				}
			}
		}
		return item;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		ItemStack stack = new ItemStack(heraldricWeapon, 1);
		if(heraldricWeapon instanceof IHeraldyItem){
			((IHeraldyItem)heraldricWeapon).setHeraldryCode(stack, SigilHelper.getDefault());
		}else{
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setByteArray("hc", SigilHelper.getDefault());
		}
		return stack;
	}
	
	
}
