package mods.battlegear2.recipies;

import mods.battlegear2.api.shield.IArrowDisplay;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class ShieldRemoveArrowRecipie implements IRecipe{

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean foundStack = false;

        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack != null){
                if(stack.getItem() instanceof IArrowDisplay){
                    if(foundStack)
                        return false;
                    else
                        foundStack = true;
                }else
                    return false;
            }
        }
        return foundStack;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {

        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack != null){
                if(stack.getItem() instanceof IArrowDisplay){
                    ItemStack shieldCopy =  stack.copy();

                    ((IArrowDisplay)shieldCopy.getItem()).setArrowCount(shieldCopy, 0);

                    return shieldCopy;
                }
            }
        }

        return null;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(BattlegearConfig.shield[2], 1, 0);
    }
}
