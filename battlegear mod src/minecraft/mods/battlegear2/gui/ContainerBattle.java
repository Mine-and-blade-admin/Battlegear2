package mods.battlegear2.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ContainerBattle extends ContainerLocalPlayer {

    public ContainerBattle(InventoryPlayer inventoryPlayer, boolean local, EntityPlayer player) {
        super(local, player);
        //Armour slots,range [0-3]
        for (int i = 0; i < 4; i++) {
            this.addSlotToContainer(new ArmorSlot(i, inventoryPlayer, inventoryPlayer.getSizeInventory() - 1 - i, 98, 8 + i * 18));
        }

        //Normal inventory slots[4-30]
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        //Default bar[31-39]
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }

        //Weapon Slots[40-45] even slots for main, odd slots for offhand
        for (int x = 0; x < InventoryPlayerBattle.WEAPON_SETS; x++) {
            WeaponSlot main = new WeaponSlot(inventoryPlayer,
                    x + InventoryPlayerBattle.OFFSET, 78, 15 + x * 18, true);
            WeaponSlot offhand = new WeaponSlot(inventoryPlayer,
                    x + InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.WEAPON_SETS,
                    118, 15 + x * 18, false);

            main.setPartner(offhand);
            offhand.setPartner(main);

            addSlotToContainer(main);
            addSlotToContainer(offhand);
        }

        //Cloak Slot [46]
        //Slot cloakSlot = new ItemSlot(inventoryPlayer,InventoryPlayerBattle.EXTRA_ITEMS+InventoryPlayerBattle.OFFSET, 152 ,8, new int[]{BattlegearConfig.cloak.itemID});
        //cloakSlot.setBackgroundIconIndex(BattleGear.proxy.getBackgroundIcon(2));
        //this.addSlotToContainer(cloakSlot);

    }

    @Override
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
                this.mergeItemStack(itemStack1, 4, 39, false);
            }
            else //we are in normal inventory
            {

                if(itemStack.getItem() instanceof ItemArmor){
                    int index =  ((ItemArmor)itemStack.getItem()).armorType ;
                    if (!this.mergeItemStack(itemStack1, index, index + 1, false)){
                        //return null;
                    }
                }else{
                    for(int index=40;index<this.inventorySlots.size();index++){//Search within WeaponSlot{
                        Slot destSlot = (Slot) this.inventorySlots.get(index);
                        if ( !destSlot.getHasStack() && destSlot.isItemValid(itemStack1))
                        {
                            if (!this.mergeItemStack(itemStack1, index, index + 1, false)){
                                //return null;
                            }
                        }
                    }
                }
            }
            if (itemStack1.stackSize == 0)
            {
                slot.putStack(null);
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

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        super.onCraftMatrixChanged(par1IInventory);
        if(!isLocalWorld){
            Battlegear.packetHandler.sendPacketToPlayer(
                    new BattlegearSyncItemPacket(thePlayer).generatePacket(),
                    (EntityPlayerMP)thePlayer);
        }
    }

/*
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(slotIndex < 4){ //If in the armour bar
                if (!this.mergeItemStack(itemstack1, 4, 39, false)){
                    return null;
                }
            }else if (slotIndex >= 40){ //In weapon bar
                if (!this.mergeItemStack(itemstack1, 4, 39, false)){
                    return null;
                }
            }else{

                if(itemstack.getItem() instanceof ItemArmor){
                    if(!((Slot)this.inventorySlots.get(((ItemArmor)itemstack.getItem()).armorType)).getHasStack()){
                        int armourSlot = 5 + ((ItemArmor)itemstack.getItem()).armorType;
                        if (!this.mergeItemStack(itemstack1, armourSlot, armourSlot + 1, false)){
                            return null;
                        }
                    }
                }else{  //maybe need to test (itemstack.getItem() instanceof IBattlegearWeapon)
                    for(int i = 40; i < this.inventorySlots.size(); i++){
                        Slot tempSlot = this.getSlot(i);
                        if(tempSlot.getHasStack() && tempSlot.isItemValid(itemstack)){
                            if (!this.mergeItemStack(itemstack1, i, i + 1, false)){
                                return null;
                            }
                        }
                    }
                }
            }
        }

        return itemstack;

    }
    */
}
