package mods.battlegear2.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ArmorSlot extends Slot {
    private final EntityPlayer player;
    private final int type;
    public ArmorSlot(int type, InventoryPlayer par1IInventory, int par2, int par3, int par4) {
        super(par1IInventory, par2, par3, par4);
        this.type = type;
        this.player = par1IInventory.player;
    }

    @Override
    public int getSlotStackLimit(){
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack){
        return par1ItemStack != null && par1ItemStack.getItem().isValidArmor(par1ItemStack, type, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return ItemArmor.EMPTY_SLOT_NAMES[type];
    }
}
