package mods.battlegear2.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.client.ClientProxy;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class WeaponSlot extends Slot {

    private WeaponSlot partner;
    private boolean mainHand;

    public WeaponSlot(InventoryPlayer par1iInventory, int par2, int par3, int par4, boolean mainhand) {
        super(par1iInventory, par2, par3, par4);
        this.mainHand = mainhand;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getBackgroundSprite() {
        return ((ClientProxy) Battlegear.proxy).getSlotIcon(mainHand ? 0 : 1);
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
            return super.isItemValid(null);
        } else if(inventory instanceof InventoryPlayer){
            EntityPlayer player = ((InventoryPlayer) inventory).player;
            if (mainHand) {
                return BattlegearUtils.isMainHand(par1ItemStack, partner.getStack(), player) && super.isItemValid(par1ItemStack);
            } else if (BattlegearUtils.isOffHand(par1ItemStack, player)) {
                return BattlegearUtils.isMainHand(partner.getStack(), par1ItemStack, player) && super.isItemValid(par1ItemStack);
            }
        }
        return false;
    }
}
