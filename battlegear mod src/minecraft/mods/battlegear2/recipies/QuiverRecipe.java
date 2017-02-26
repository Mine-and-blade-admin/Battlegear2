package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Recipe to fill item instances of IArrowContainer2 with compatible arrows
 * {@see IArrowContainer2#isCraftableWithArrows(ItemStack,ItemStack)}
 */
public final class QuiverRecipe implements IRecipe {

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventorycrafting,@Nonnull World world) {
        int slot = getNextQuiver(inventorycrafting, 0);
        if(slot == -1 || getNextQuiver(inventorycrafting, slot + 1) != -1){
            return false;
        }
        ItemStack quiver = inventorycrafting.getStackInSlot(slot);
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(!stack.isEmpty() && !((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack))
            {
                return false;
            }
        }

        return true;
    }

    private int getNextQuiver(InventoryCrafting inventorycrafting, int min){
        for(int i = min; i < inventorycrafting.getSizeInventory(); i++) {
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof IArrowContainer2) {
                return i;
            }
        }
        return -1;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventorycrafting) {
        int slot = getNextQuiver(inventorycrafting, 0);
        if(slot == -1){
            return ItemStack.EMPTY;
        }
        ItemStack quiver = inventorycrafting.getStackInSlot(slot).copy();
        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(!stack.isEmpty())
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

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
        int slot = getNextQuiver(inv, 0);
        if(slot == -1){
            return NonNullList.withSize(0, ItemStack.EMPTY);
        }
        ItemStack quiver = inv.getStackInSlot(slot).copy();
        List<ItemStack> arrows = new ArrayList<ItemStack>();
        int i = 0;
        for(; i < inv.getSizeInventory(); i++){
            if(slot == i)
                continue;
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()){
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
                if(!drop.isEmpty()) {
                    temp = drop.copy();
                    if(!player.inventory.addItemStackToInventory(temp)){
                        player.dropItem(temp, false);
                    }
                }
                itr.remove();
            }
            for(int index = 0; index < i; index++){
                inv.setInventorySlotContents(index, ItemStack.EMPTY);
            }
            for(;i < inv.getSizeInventory(); i++){
                ItemStack stack = inv.getStackInSlot(i);
                if(!stack.isEmpty()){
                    stack.grow(1);
                }
            }
        }
        return NonNullList.withSize(i, ItemStack.EMPTY);
    }

}
