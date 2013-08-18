package mods.battlegear2.recipies;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class DummyRecipie implements IRecipe{

    private int itemID;

    public DummyRecipie(int itemId){
        this.itemID = itemId;
    }

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean foundStack = false;

        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack != null){
                if(stack.getItem().itemID == itemID){
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
                if(stack.getItem().itemID == itemID){
                       return stack.copy();
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
        return new ItemStack(itemID, 1, 0);
    }
}
