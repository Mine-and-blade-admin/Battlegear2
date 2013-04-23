package mods.battlegear2.common.gui;

import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotHeraldryItem extends Slot{

	public SlotHeraldryItem(int x, int y) {
		super(new InventoryBasic("", true, 1), 0, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return par1ItemStack != null && par1ItemStack.itemID == BattlegearConfig.heradricItem.itemID;
	}
	
	

	@Override
	public void onPickupFromSlot(EntityPlayer par1EntityPlayer,
			ItemStack par2ItemStack) {
		
		
		this.inventory.setInventorySlotContents(0, par2ItemStack);
		
	}

	
}
