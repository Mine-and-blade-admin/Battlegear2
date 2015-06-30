package mods.battlegear2.recipies;

import mods.battlegear2.api.quiver.IArrowContainer2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CraftingHandeler {

    public static void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {

        if (item.getItem() instanceof IArrowContainer2) {
            ItemStack quiver = null;

            for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null){
                    if(stack.getItem() instanceof IArrowContainer2){
                        if(quiver == null){
                            quiver = stack;
                        }
                        else
                            return;
                    }
                }
            }

            if(quiver == null)
                return;
            List<ItemStack> arrows = new ArrayList<ItemStack>();
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null && stack != quiver){
                    if(((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack)){
                    	arrows.add(stack);
                    }
                    else 
                    	return;
                }
            }
            if(!arrows.isEmpty())
            {
            	Iterator itr = arrows.iterator();
        		ItemStack drop = null;
        		while(itr.hasNext() && drop==null){
        			ItemStack temp = (ItemStack) itr.next();
        			drop = ((IArrowContainer2)quiver.getItem()).addArrows(quiver, temp);
        			if(drop==null)
        				itr.remove();
        			else
        				temp.stackSize = drop.stackSize;
        		}
        		itr = arrows.iterator();
        		while(itr.hasNext()){
        			ItemStack temp = (ItemStack) itr.next();
        			if(!player.inventory.addItemStackToInventory(temp)){
                        EntityItem entityitem = ForgeHooks.onPlayerTossEvent(player, temp, true);
                        if(entityitem!=null) {
                            entityitem.setNoPickupDelay();
                            entityitem.setOwner(player.getCommandSenderName());
                        }
        			}
        		}
        		for(int index=0;index<craftMatrix.getSizeInventory();index++){
        			craftMatrix.setInventorySlotContents(index, null);
        		}
            }
        }
    }
}
