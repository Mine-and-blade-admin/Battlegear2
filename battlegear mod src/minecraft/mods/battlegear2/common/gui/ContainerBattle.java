package mods.battlegear2.common.gui;


import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class ContainerBattle extends Container{
	
	/** Determines if inventory manipulation should be handled. */
	public boolean isLocalWorld = false;
	private final EntityPlayer thePlayer;
	
	public ContainerBattle(InventoryPlayer inventoryPlayer, boolean local, EntityPlayer player){
		this.thePlayer = player;
		this.isLocalWorld = local;
		
		for(int i = 0; i < 4; i++){
			this.addSlotToContainer(new ArmourSlot(this, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 117, 8 + i * 18, i));
		}
		
		//Normal inventory slots
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }
		
		//Default bar
		for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
		
		//Weapon Slots
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

	/**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
    	System.out.println("transfer");
    	//TODO do this!!!!
    	return null;
    }
}
