package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
@Deprecated
public class QuiverRecipie implements IRecipe{

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		boolean hasQuiver = false;
		boolean hasArrow = false;
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
            	if(stack.getItem() instanceof IArrowContainer)
            	{
            		if(hasQuiver)
            			return false;
            		if(hasArrow)
            			return true;
            		hasQuiver= true;
            	}
            	else if(stack.itemID == Item.arrow.itemID)
            	{
            		if(hasQuiver)
            			return true;
            		hasArrow = true;
            	}
            }
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		ItemStack quiver = null;
		int arrows = 0;
		for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
            	if(stack.getItem() instanceof IArrowContainer)
            	{
            		quiver = stack.copy();
            	}
            	else if(stack.itemID == Item.arrow.itemID)
            	{
        			arrows +=stack.stackSize;
            	}
            }
		}
		if(quiver!=null)
		{
			 if(((IArrowContainer)quiver.getItem()).isCraftableWithArrows(quiver))
				 ((IArrowContainer)quiver.getItem()).addArrows(quiver,arrows);
			 else
				 return null;
		}
		return quiver;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(BattlegearConfig.quiver,1,0);
	}

}
