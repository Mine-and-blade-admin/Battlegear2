package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Recipe to fill item instances of IArrowContainer2 with compatible arrows
 * {@see IArrowContainer2#isCraftableWithArrows(ItemStack,ItemStack)}
 */
public final class QuiverRecipe implements IRecipe {

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
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        int slot = getNextQuiver(inv, 0);
        if(slot == -1){
            return new ItemStack[0];
        }
        ItemStack quiver = inv.getStackInSlot(slot).copy();
        List<ItemStack> arrows = new ArrayList<ItemStack>();
        int i = 0;
        for(; i < inv.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inv.getStackInSlot(i);
            if(stack != null){
                if(((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack)){
                    arrows.add(stack);
                }
                else
                    break;
            }
        }
        if(!arrows.isEmpty())
        {
            Iterator<ItemStack> itr = arrows.iterator();
            EntityPlayer player = ForgeHooks.getCraftingPlayer();
            while(itr.hasNext()) {
                ItemStack temp = itr.next();
                ItemStack drop = ((IArrowContainer2)quiver.getItem()).addArrows(quiver, temp);
                if(drop != null && drop.stackSize > 0) {
                    temp = drop.copy();
                    if(!player.inventory.addItemStackToInventory(temp)){
                        player.dropPlayerItemWithRandomChoice(temp, false);
                    }
                }
                itr.remove();
            }
            for(int index = 0; index < i; index++){
                inv.setInventorySlotContents(index, null);
            }
            for(;i < inv.getSizeInventory(); i++){
                ItemStack stack = inv.getStackInSlot(i);
                if(stack != null){
                    stack.stackSize++;
                }
            }
        }
        return new ItemStack[i];
    }

}
