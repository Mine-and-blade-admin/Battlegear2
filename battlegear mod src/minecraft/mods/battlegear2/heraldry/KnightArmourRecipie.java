package mods.battlegear2.heraldry;

import mods.battlegear2.items.ItemKnightArmour;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class KnightArmourRecipie implements IRecipe{

	private ItemArmor knightArmour;
	private Item chainArmourId;
	private Item ironArmourId;
	
	
	public KnightArmourRecipie(int armourType){
		knightArmour = BattlegearConfig.knightArmor[armourType];
		
		switch(armourType){
		case 0:
			chainArmourId = Items.chainmail_helmet;
			ironArmourId = Items.iron_helmet;
			break;
		case 1:
			chainArmourId = Items.chainmail_chestplate;
			ironArmourId = Items.iron_chestplate;
			break;
		case 2:
			chainArmourId = Items.chainmail_leggings;
			ironArmourId = Items.iron_leggings;
			break;
		case 3:
			chainArmourId = Items.chainmail_boots;
			ironArmourId = Items.iron_boots;
			break;
		}
	}
	
	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		
		boolean chainFound = false;
		boolean ironFound = false;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory();i ++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(stack != null){
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

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		ItemStack chain = null;
		ItemStack iron = null;
		
		for(int i = 0; i < inventorycrafting.getSizeInventory();i ++){
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if(stack != null){
				if(stack.getItem() == chainArmourId){
					chain = stack;
				}else if(stack.getItem() == ironArmourId){
					iron = stack;
				}
			}
		}
        if(iron==null||chain==null)
            return null;
		
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

	@Override
	public ItemStack getRecipeOutput() {
		//TODO: may need to define the "code"
		return new ItemStack(knightArmour);
	}

}
