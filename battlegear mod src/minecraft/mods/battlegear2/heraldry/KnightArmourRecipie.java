package mods.battlegear2.heraldry;

import mods.battlegear2.items.ItemKnightArmour;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class KnightArmourRecipie implements IRecipe{

	private final ItemArmor knightArmour;
	private Item chainArmourId;
	private Item ironArmourId;
	
	
	public KnightArmourRecipie(ItemArmor armourType){
		knightArmour = armourType;
		
		switch(armourType.armorType){
			case HEAD:
			chainArmourId = Items.CHAINMAIL_HELMET;
			ironArmourId = Items.IRON_HELMET;
			break;
			case CHEST:
			chainArmourId = Items.CHAINMAIL_CHESTPLATE;
			ironArmourId = Items.IRON_CHESTPLATE;
			break;
			case LEGS:
			chainArmourId = Items.CHAINMAIL_LEGGINGS;
			ironArmourId = Items.IRON_LEGGINGS;
			break;
			case FEET:
			chainArmourId = Items.CHAINMAIL_BOOTS;
			ironArmourId = Items.IRON_BOOTS;
			break;
		}
	}
	
	@Override
	public boolean matches(@Nonnull InventoryCrafting inventorycrafting,@Nonnull World world) {
		
		boolean chainFound = false;
		boolean ironFound = false;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory();i ++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(!stack.isEmpty()){
				if(stack.getItem() == chainArmourId){
					if(chainFound)
						return false;
					else
						chainFound = true;
				}else if(stack.getItem() == ironArmourId){
					if(ironFound)
						return false;
					else
						ironFound = true;
				}else
					return false;
			}
		}
		
		return chainFound && ironFound;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventorycrafting) {
		ItemStack chain = ItemStack.EMPTY;
		ItemStack iron = ItemStack.EMPTY;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory();i ++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(stack.getItem() == chainArmourId){
				chain = stack;
			}else if(stack.getItem() == ironArmourId){
				iron = stack;
			}
		}
        if(iron.isEmpty()||chain.isEmpty())
            return ItemStack.EMPTY;
		
		float damage =  1 - ((1 - ((float)iron.getItemDamage() / (float)iron.getMaxDamage())) * 0.67F +
				(1 - ((float)chain.getItemDamage() / (float)chain.getMaxDamage())) * 0.33F);
		
		ItemStack kArmourStack = new ItemStack(knightArmour, 1, (int)(knightArmour.getMaxDamage() * damage));
		if(iron.hasTagCompound()){
			//copy over the enchantments from the iron armour
			kArmourStack.setTagCompound(iron.getTagCompound());
		}else{
			kArmourStack.setTagCompound(new NBTTagCompound());
		}
		
		((ItemKnightArmour)kArmourStack.getItem()).setHeraldry(kArmourStack, SigilHelper.getDefault());
		
		return kArmourStack;
		
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		//TODO: may need to define the "code"
		return new ItemStack(knightArmour);
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}
}
