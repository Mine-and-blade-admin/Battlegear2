package mods.battlegear2.recipies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mods.battlegear2.api.IArrowContainer;
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
                    player.inventory.addItemStackToInventory(new ItemStack(Item.arrow, nextStackSize));

                }

            }
        }
        else if(item.getItem() instanceof IArrowContainer && ((IArrowContainer)item.getItem()).isCraftableWithArrows(item)){
        	ItemStack quiver = null;
        	boolean hasArrow = false;
        	List<ItemStack> arrows = new ArrayList();
        	for(int i = 0; i < craftMatrix.getSizeInventory(); i++){
                ItemStack stack = craftMatrix.getStackInSlot(i);
                if(stack != null){
                    if(stack.getItem() instanceof IArrowContainer){
                    	if(quiver == null){
                    		quiver = stack;
                    	}
                    	else
                    		return;
                    }
                    else if(stack.itemID == Item.arrow.itemID){
                    	arrows.add(stack);
                    	hasArrow = true;;
                    }
                }
        	}
        	if(quiver!=null && hasArrow){
        		Iterator itr = arrows.iterator();
        		int drop = 0;
        		while(itr.hasNext() && drop==0){
        			ItemStack temp = (ItemStack) itr.next();
        			drop = ((IArrowContainer)quiver.getItem()).addArrows(quiver, temp.stackSize);
        			if(drop==0)
        				itr.remove();
        			else
        				temp.stackSize = drop;
        		}
        		itr = arrows.iterator();
        		while(itr.hasNext()){
        			player.inventory.addItemStackToInventory((ItemStack)itr.next());
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
