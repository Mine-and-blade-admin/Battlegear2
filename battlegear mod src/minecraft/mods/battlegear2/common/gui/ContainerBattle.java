package mods.battlegear2.common.gui;

import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;


public class ContainerBattle extends Container{
	
	/** Determines if inventory manipulation should be handled. */
	public boolean isLocalWorld = false;
	private final EntityPlayer thePlayer;
	
	public ContainerBattle(InventoryPlayer inventoryPlayer, boolean local, EntityPlayer player){
		this.thePlayer = player;
		this.isLocalWorld = local;
		//Armour slots,range [0-3]
		for(int i = 0; i < 4; i++){
			this.addSlotToContainer(new ArmourSlot(this, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 117, 8 + i * 18, i));
		}
		
		//Normal inventory slots[4-30]
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
		
		//Default bar[31-39]
		for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
		
		//Weapon Slots[40-45] even slots for main, odd slots for offhand
        for(int x = 0; x < 3; x++){
        	WeaponSlot main = new WeaponSlot(inventoryPlayer,
        			x+InventoryPlayerBattle.OFFSET, 97, 15+x*18, true);
        	WeaponSlot offhand = new WeaponSlot(inventoryPlayer,
        			x+InventoryPlayerBattle.OFFSET+InventoryPlayerBattle.WEAPON_SETS,
        			137, 15+x*18, false);
        	
        	main.setPartner(offhand);
        	offhand.setPartner(main);
        	
        	addSlotToContainer(main);
        	addSlotToContainer(offhand);
        }
        
        System.out.println(inventorySlots.size());
	}

	public boolean canInteractWith(EntityPlayer par1EntityPlayer){
        return true;
    }
@Override
	/**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
    	ItemStack itemStack = null;
    	Slot slot = (Slot) this.inventorySlots.get(slotIndex);
    	if (slot != null && slot.getHasStack()) {
    	    ItemStack itemStack1 = slot.getStack();
    	    itemStack = itemStack1.copy();
    	    if (slotIndex<4||slotIndex>39)
    	    {//we are in weapon/armor slots, so we transfer to normal
    	    	//chose from the up left slot in the inventory to the right slot in bar 	
    	    	if(!this.mergeItemStack(itemStack1, 4, 39, false))
    	    		return null; 
    	    }
    	    else //we are in normal inventory
    	    {
    	    	//TODO: review this
    	    	if(itemStack1.getItem() instanceof ItemArmor)
    	    	{
    	    		/*
    	    		 * put in slot corresponding to armortype,
    	    		 *  no need to merge because max stack is 1
    	    		 *  we only need to check if empty
    	    		 */
    	    		int type=((ItemArmor)itemStack1.getItem()).armorType;
    	    		if (this.inventorySlots.get(type)!=null && !((Slot)this.inventorySlots.get(type)).getHasStack())
    	    			this.putStackInSlot(type,itemStack1);
    	    	}
    	    	else if(itemStack1.getItem() instanceof IBattlegearWeapon)
    	    	{
    	    		/* Item using the API 
    	    		 */
    	    		if(((IBattlegearWeapon)itemStack1.getItem()).isOffhandHandDualWeapon())
    	    		{//search first available offhand slot
    	    			for (int i=41;i<46;i=i+2){
    	    				Slot offhandSlot = (Slot) this.inventorySlots.get(i);
    	    				if (offhandSlot!=null && !offhandSlot.getHasStack())//offhand slot is free
    	    				{
    	    					Slot mainSlot = (Slot) this.inventorySlots.get(i-1);
    	    					if(mainSlot!=null && !mainSlot.getHasStack())//nothing in main slot
    	    					{	
    	    						this.putStackInSlot(i,itemStack1);
    	    						break;
    	    					}
    	    					else if(mainSlot!=null && mainSlot.getHasStack()) //something in main slot
    	    					{
    	    						Item mainItem = mainSlot.getStack().getItem();
    	    						if(mainItem instanceof IBattlegearWeapon && ((IBattlegearWeapon)mainItem).willAllowOffhandWeapon())
    	    						{	//main item use the API too :)
    	    							this.putStackInSlot(i,itemStack1);
    	    							break;
    	    						}
    	    						else if(mainItem instanceof ItemSword)// a special case for swords
    	    						{	
    	    							this.putStackInSlot(i,itemStack1);
    	    							break;
    	    						}
    	    					}
    	    				}
    	    			}
    	    		}
    	    		else//not offhandDualwieldable
    	    		{//search first available main slot
    	    			for (int i=40;i<45;i=i+2)
    	    			{
    	    				Slot mainSlot = (Slot) this.inventorySlots.get(i);
    	    				if (mainSlot!=null && !mainSlot.getHasStack())
    	    				{
    	    					this.putStackInSlot(i,itemStack1);
    	    					break;
    	    				}
    	    			}
    	    		}
    	    	}
    	    	else//put any other item in first available main slot
    	    	{
    	    		for (int i=40;i<45;i=i+2)
    	    		{
	    			Slot mainSlot = (Slot) this.inventorySlots.get(i);
	    			if (mainSlot!=null && !mainSlot.getHasStack())
	    			{	Slot offhandSlot =(Slot)this.inventorySlots.get(i+1);
	    				if (offhandSlot!=null && offhandSlot.getHasStack() && !mergeItemStack(offhandSlot.getStack(),4,39,false))
	    					return null;/*something in offhandslot could be an issue, for safety
	    						 				we stop if we can't put this one back in normal inventory*/
	    				this.putStackInSlot(i,itemStack1);
	    				break;
	    			}
	    		}
    	    	}
    	    }	
    	    if (itemStack1.stackSize == 0) 
    	    {
    		slot.putStack((ItemStack)null);
    	    } 
    	    else 
    	    {
    		slot.onSlotChanged();
    	    }
    	    if (itemStack1.stackSize != itemStack.stackSize)
    	    {
    		slot.onPickupFromSlot(player, itemStack1);
    	    } 
    	    else 
    	    {
    		return null;
    	    }
    	}
    	return itemStack;
    }
}
