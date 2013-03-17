package mods.battlegear2.common.gui;


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
		
		//Weapon Slots[40-42]
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
    	    	//TODO: decide what to do, should we put armor/weapons in corresponding slot like this?
    	    	if(itemStack1.getItem() instanceof ItemArmor)
    	    	{
    	    		this.mergeItemStack(itemStack1, 0, 3, false);
    	    	}
    	    	else if(itemStack1.getItem() instanceof ItemSword)
    	    	{
    	    		/*
    	    		 * TODO: This doesn't 100% work. It will not move bows and will move items
    	    		 * into invalid slots (eg a sword in an offhand slot with a bow)
    	    		 */
    	    		this.mergeItemStack(itemStack1, 40, 42, false);
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
