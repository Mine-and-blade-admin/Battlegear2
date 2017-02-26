package mods.battlegear2.recipies;

import mods.battlegear2.api.shield.IArrowDisplay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

public final class ShieldRemoveArrowRecipie implements IRecipe{

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventorycrafting,@Nonnull World world) {
        boolean foundStack = false;

        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack.getItem() instanceof IArrowDisplay){
                if(foundStack)
                    return false;
                else
                    foundStack = true;
            }else
                return false;
        }
        return foundStack;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventorycrafting) {

        for(int i = 0; i < inventorycrafting.getSizeInventory(); i++){
            ItemStack stack = inventorycrafting.getStackInSlot(i);
            if(stack.getItem() instanceof IArrowDisplay){
                ItemStack shieldCopy = stack.copy();

                ((IArrowDisplay)shieldCopy.getItem()).setArrowCount(shieldCopy, 0);

                return shieldCopy;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting craftMatrix) {
        NonNullList<ItemStack> result = NonNullList.withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);
        ItemStack shield;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            shield = craftMatrix.getStackInSlot(i);
            if (shield.getItem() instanceof IArrowDisplay) {

                int arrowCount = ((IArrowDisplay) shield.getItem()).getArrowCount(shield);
                int j = 0;
                while (arrowCount > 0) {

                    int nextStackSize = Math.min(arrowCount, Items.ARROW.getItemStackLimit());
                    arrowCount -= nextStackSize;
                    ItemStack temp = new ItemStack(Items.ARROW, nextStackSize);
                    if (j < craftMatrix.getSizeInventory()) {
                        result.set(j, temp);
                        j++;
                    } else {
                        EntityPlayer player = ForgeHooks.getCraftingPlayer();
                        if (!player.inventory.addItemStackToInventory(temp)) {
                            player.dropItem(temp, false);
                        }
                    }

                }
                break;
            }

        }
        return result;
    }
}
