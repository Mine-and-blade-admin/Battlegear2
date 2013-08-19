package mods.battlegear2.recipies;

import mods.battlegear2.api.IShield;
import mods.battlegear2.items.ItemShield;
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
                if(stack.getItem() instanceof ItemShield){
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
                if(stack.getItem() instanceof ItemShield){
                    ItemStack shieldCopy =  stack.copy();

                    ((ItemShield)shieldCopy.getItem()).setArrowCount(shieldCopy, 0);

                    return shieldCopy;
                }
            }

        }

        return null;
    }

    @Override
    public int getRecipeSize() {
        return 1; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(BattlegearConfig.shield[2], 1, 0);
    }
}
