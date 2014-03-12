package mods.battlegear2.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class WeaponSlot extends Slot {

    private WeaponSlot partner;
    private boolean mainHand;

    public WeaponSlot(IInventory par1iInventory, int par2, int par3, int par4, boolean mainhand) {
        super(par1iInventory, par2, par3, par4);
        this.mainHand = mainhand;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBackgroundIconIndex(){
        //MOJANG derp fixes:
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
        return Battlegear.proxy.getSlotIcon(mainHand ? 0 : 1);
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
                if (BattlegearUtils.isWeapon(par1ItemStack)) {
                    if (partner.getHasStack() && !(partner.getStack().getItem() instanceof IArrowContainer2)) {
                        return BattlegearUtils.isMainHand(par1ItemStack, partner.getStack()) && super.isItemValid(par1ItemStack);
                    } else {
                        return super.isItemValid(par1ItemStack);
                    }
                } else {
                    return false;
                }
            } else {

                if(par1ItemStack.getItem() instanceof IArrowContainer2){
                    if (partner.getHasStack()) {
                        return partner.getStack().getItem() instanceof ItemBow && super.isItemValid(par1ItemStack);
                    }else{
                        return super.isItemValid(par1ItemStack);
                    }

                }else if (BattlegearUtils.isOffHand(par1ItemStack)) {
                    if (partner.getHasStack()) {
                    	return BattlegearUtils.isMainHand(partner.getStack(), par1ItemStack) && super.isItemValid(par1ItemStack);
                    } else {
                        return super.isItemValid(par1ItemStack);
                    }
                }else{
                    return false;
                }

        }
    }
}
