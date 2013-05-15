package mods.battlegear2.common;

import mods.battlegear2.api.IHeraldryItem;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class HeraldricWeaponRecipie implements IRecipe{

	private Item heraldricWeapon;
	
	public HeraldricWeaponRecipie(Item item){
		this.heraldricWeapon = item;
	}
	
	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		
		boolean itemFound = false;
		boolean iconFound = false;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			
			if(stack != null){
				if(stack.getItem().itemID == BattlegearConfig.heradricItem.itemID){
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
				}
			}
			
		}
		
		NBTTagCompound compound = item.getTagCompound();
		if(compound == null){
			compound = new NBTTagCompound();
		}
		
		int code = ((IHeraldryItem)icon.getItem()).getHeraldryCode(icon);
		System.out.println(code);
		compound.setInteger("colour", code);
		item.setTagCompound(compound);
		
		return item;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		ItemStack stack = new ItemStack(heraldricWeapon, 1);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger("colour", 0);
		return stack;
	}

}
