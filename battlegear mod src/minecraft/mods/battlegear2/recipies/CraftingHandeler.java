package mods.battlegear2.recipies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.items.ItemShield;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

public class CraftingHandeler implements ICraftingHandler{
    @Override
    public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {

        if(item.getItem() instanceof ItemShield){
            ItemStack shield = null;
            boolean isOnlyShield = true;
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null){
                    if(stack.getItem() instanceof ItemShield){
                        if(shield == null){
                            shield = stack;
                        }else{
                            isOnlyShield = false;
                        }
                    } else{
                        isOnlyShield = false;
                    }
                }
            }

            if(isOnlyShield && shield != null){

                int arrowCount = ((ItemShield) shield.getItem()).getArrowCount(shield);

                while(arrowCount > 0){

                    int nextStackSize = Math.min(arrowCount, 64);
                    arrowCount -= nextStackSize;
                    ItemStack temp = new ItemStack(Item.arrow, nextStackSize);
                    if(!player.inventory.addItemStackToInventory(temp)){
                    	player.dropPlayerItem(temp);
                    }

                }

            }
        }
        else if(item.getItem() instanceof IArrowContainer2){
            ItemStack quiver = null;
            ItemStack arrowStack = null;

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
            List<ItemStack> arrows = new ArrayList();
            for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null && stack != quiver){
                    if(((IArrowContainer2)quiver.getItem()).isCraftableWithArrows(quiver, stack)){
                    	arrows.add(stack);
                        /*ItemStack rejectArrows = ((IArrowContainer2)quiver.getItem()).addArrows(quiver, stack);
                        player.inventory.addItemStackToInventory(rejectArrows);
                        craftMatrix.setInventorySlotContents(i, null);*/
                    }
                    else 
                    	return;
                }
            }
            if(arrows.isEmpty())
            {
            	//remove arrows ?
            }
            else
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
        				player.dropPlayerItem(temp);
        			}
        		}
        		for(int index=0;index<craftMatrix.getSizeInventory();index++){
        			craftMatrix.setInventorySlotContents(index, null);
        		}
            }
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
