package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class QuiverRecipie2 implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        //boolean hasArrow = false;
        ItemStack quiver = null;
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
                if(stack.getItem() instanceof IArrowContainer2)
                {
                    if(quiver!=null)
                        return false;
                    //Don't copy (yet) as we need the reference to check later
                    quiver = stack;
                }
            }
        }
        if(quiver!=null){
            for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
                ItemStack stack = inventorycrafting.getStackInSlot(i);
                if(stack!=null && stack!=quiver)
                {
                    if(!((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
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
                    if(((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack))
                    {
                        ((IArrowContainer2)quiver.getItem()).addArrows(quiver, stack.copy());
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
