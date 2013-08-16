package mods.battlegear2.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WeaponSlot extends Slot {

    private WeaponSlot partner;
    private boolean mainHand;

    public WeaponSlot(IInventory par1iInventory, int par2, int par3, int par4, boolean mainhand) {
        super(par1iInventory, par2, par3, par4);

        this.setBackgroundIcon(Battlegear.proxy.getSlotIcon(mainhand ? 0 : 1));
        this.mainHand = mainhand;

    }


    public WeaponSlot getPartner() {
        return partner;
    }

    public void setPartner(WeaponSlot partner) {
        this.partner = partner;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {

            if (par1ItemStack == null) {
                return super.isItemValid(par1ItemStack);
            } else if (mainHand) {
                if (BattlegearUtils.isWeapon(par1ItemStack.itemID)) {
                    if (partner.getHasStack()) {
                        return BattlegearUtils.isMainHand(par1ItemStack.itemID) ? super.isItemValid(par1ItemStack) : false;
                    } else {
                        return super.isItemValid(par1ItemStack);
                    }
                } else {
                    return false;
                }
            } else {
                if (BattlegearUtils.isWeapon(par1ItemStack.itemID)) {
                    if (partner.getHasStack()) {
                        if (BattlegearUtils.isMainHand(partner.getStack().itemID)) {
                            return BattlegearUtils.isOffHand(par1ItemStack.itemID) ? super.isItemValid(par1ItemStack) : false;
                        } else {
                            return false;
                        }
                    } else {
                        return super.isItemValid(par1ItemStack) && BattlegearUtils.isOffHand(par1ItemStack.itemID);
                    }
                } else
                    return par1ItemStack.getItem() instanceof ItemShield;

        }
    }
}
