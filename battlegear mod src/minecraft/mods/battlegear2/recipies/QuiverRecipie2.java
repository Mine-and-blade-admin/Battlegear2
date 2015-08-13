package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public final class QuiverRecipie2 implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventorycrafting, World world) {
        int slot = getNextQuiver(inventorycrafting, 0);
        if(slot == -1 || getNextQuiver(inventorycrafting, slot + 1) != -1){
            return false;
        }
        ItemStack quiver = inventorycrafting.getStackInSlot(slot);
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null && !((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack))
            {
                return false;
            }
        }

        return true;
    }

    private int getNextQuiver(InventoryCrafting inventorycrafting, int min){
        for(int i = min; i < inventorycrafting.getSizeInventory(); i++) {
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof IArrowContainer2) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
        int slot = getNextQuiver(inventorycrafting, 0);
        if(slot == -1){
            return null;
        }
        ItemStack quiver = inventorycrafting.getStackInSlot(slot).copy();
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack!=null)
            {
                if(((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack))
                {
                    ((IArrowContainer2)quiver.getItem()).addArrows(quiver, stack.copy());
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

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return new ItemStack[inv.getSizeInventory()];
    }

}
