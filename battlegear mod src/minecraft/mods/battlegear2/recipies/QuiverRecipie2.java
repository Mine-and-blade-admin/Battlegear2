package mods.battlegear2.recipies;

import mods.battlegear2.api.IArrowContainer;
import mods.battlegear2.api.IArrowContainer2;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class QuiverRecipie2 implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        boolean hasQuiver = false;
        boolean hasArrow = false;
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
                if(stack.getItem() instanceof IArrowContainer2)
                {
                    if(hasQuiver)
                        return false;
                    hasQuiver= true;
                }
                else if(stack.itemID == Item.arrow.itemID){
                    if(hasArrow)
                        return false;
                    hasArrow = true;
                }
            }
        }


        return hasArrow && hasQuiver;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        ItemStack quiver = null;
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
                if(stack.getItem() instanceof IArrowContainer2)
                {
                    quiver = stack.copy();
                }
            }
        }
        if(quiver!=null){

            for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
                ItemStack stack = inventorycrafting.getStackInSlot(i);
                if(stack!=null)
                {
                    if(stack.itemID == Item.arrow.itemID)
                    {
                        ((IArrowContainer2)quiver.getItem()).addArrows(quiver, stack);
                    }
                }
            }

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
