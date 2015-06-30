package mods.battlegear2.recipies;

import mods.battlegear2.api.shield.IArrowDisplay;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public final class ShieldRemoveArrowRecipie implements IRecipe{

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
                    ItemStack shieldCopy = stack.copy();

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

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting craftMatrix) {
        ItemStack[] result = new ItemStack[craftMatrix.getSizeInventory()];
        ItemStack shield = null;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            shield = craftMatrix.getStackInSlot(i);
            if (shield != null && shield.getItem() instanceof IArrowDisplay) {
                break;
            }
            shield = null;
        }

        if (shield != null) {

            int arrowCount = ((IArrowDisplay) shield.getItem()).getArrowCount(shield);
            int i = 0;
            while (arrowCount > 0) {

                int nextStackSize = Math.min(arrowCount, Items.arrow.getItemStackLimit());
                arrowCount -= nextStackSize;
                ItemStack temp = new ItemStack(Items.arrow, nextStackSize);
                if (i < craftMatrix.getSizeInventory()) {
                    result[i] = temp;
                    i++;
                } else {
                    EntityPlayer player = ForgeHooks.getCraftingPlayer();
                    if (!player.inventory.addItemStackToInventory(temp)) {
                        EntityItem entityitem = ForgeHooks.onPlayerTossEvent(player, temp, true);
                        if (entityitem != null) {
                            entityitem.setNoPickupDelay();
                            entityitem.setOwner(player.getCommandSenderName());
                        }
                    }
                }

            }

        }
        return result;
    }
}
