package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

public class HeraldyRecipie implements IRecipe{
	protected final Item heraldricWeapon;
	
	public HeraldyRecipie(Item item){
		this.heraldricWeapon = item;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inventorycrafting,@Nonnull World world) {
		
		boolean itemFound = false;
		boolean iconFound = false;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(!stack.isEmpty()){
				if(stack.getItem() == BattlegearConfig.heradricItem ||
						stack.getItem() == Items.WATER_BUCKET){
					if(iconFound)
						return false;
					else
						iconFound = true;
				}else if (stack.getItem() == heraldricWeapon){
					if(itemFound)
						return false;
					else
						itemFound = true;
				}
			}
		}
		
		return itemFound && iconFound;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventorycrafting) {
		
		ItemStack item = ItemStack.EMPTY;
		ItemStack icon = ItemStack.EMPTY;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			
			if(!stack.isEmpty()){
				if(stack.getItem() == BattlegearConfig.heradricItem){
					icon = stack;
				}else if (stack.getItem() == heraldricWeapon){
					item = stack;
				}else if (stack.getItem() == Items.WATER_BUCKET){
					icon = stack;
				}
			}
		}
        if(item.isEmpty())
            return item;
		
		item = item.copy();
		
		if(heraldricWeapon instanceof IHeraldryItem){
			byte[] code = SigilHelper.getDefault();
			if(icon.getItem() == BattlegearConfig.heradricItem){
				code = ((IHeraldryItem)icon.getItem()).getHeraldry(icon);
			}
			((IHeraldryItem) heraldricWeapon).setHeraldry(item, code);
		}else{
			
			NBTTagCompound compound = item.getTagCompound();
			if(compound == null){
				compound = new NBTTagCompound();
			}
			if(icon.getItem() == BattlegearConfig.heradricItem){
				byte[] code = ((IHeraldryItem)icon.getItem()).getHeraldry(icon);
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

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		ItemStack stack = new ItemStack(heraldricWeapon, 1);
		if(heraldricWeapon instanceof IHeraldryItem){
			((IHeraldryItem)heraldricWeapon).setHeraldry(stack, SigilHelper.getDefault());
		}else{
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setByteArray("hc", SigilHelper.getDefault());
		}
		return stack;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
