package mods.battlegear2.recipies;


import cpw.mods.fml.common.ICraftingHandler;
import mods.battlegear2.items.ItemShield;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShieldCraftingHandeler implements ICraftingHandler{
    @Override
    public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {

        if(item.getItem() instanceof ItemShield){
            boolean shieldFound = false;
            boolean isOnlyShield = true;
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null){
                    if(stack.getItem() instanceof ItemShield){
                        if(shieldFound){
                            isOnlyShield = false;
                        }else{
                            shieldFound = true;
                        }
                    } else{
                        isOnlyShield = false;
                    }
                }
            }

            if(isOnlyShield && shieldFound){

                int arrowCount = ((ItemShield) item.getItem()).getArrowCount(item);
                ((ItemShield) item.getItem()).setArrowCount(item, 0);
                while(arrowCount > 0){

                    int nextStackSize = Math.min(arrowCount, 64);
                    arrowCount -= nextStackSize;
                    player.inventory.addItemStackToInventory(new ItemStack(Item.arrow, nextStackSize));

                }

            }
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
