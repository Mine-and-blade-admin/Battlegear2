package battlegear2.common.gui;

import battlegear2.common.BattleGear;
import battlegear2.common.utils.BattlegearUtils;



import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class WeaponSlot extends Slot{
	
	private WeaponSlot partner;
	private boolean mainHand;

	public WeaponSlot(IInventory par1iInventory, int par2, int par3, int par4, boolean mainhand) {
		super(par1iInventory, par2, par3, par4);
		this.mainHand = mainhand;
		
		
		setBackgroundIconTexture(BattleGear.imageFolder+"Equip GUI.png");
		setBackgroundIconIndex(mainhand ? 15 : 31);
		
			
	}
	
	public WeaponSlot getPartner() {
		return partner;
	}

	
	
	public void setPartner(WeaponSlot partner) {
		this.partner = partner;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		
		if(par1ItemStack == null){
			return super.isItemValid(par1ItemStack);
		}
		else if(mainHand){
			if(BattlegearUtils.isWeapon(par1ItemStack.itemID)){
				if(partner.getHasStack()){
					return BattlegearUtils.isMainHand(par1ItemStack.itemID) ? super.isItemValid(par1ItemStack) : false;
				}else{
					return super.isItemValid(par1ItemStack);
				}
			}else{
				return false;
			}
		}else{
			if(BattlegearUtils.isWeapon(par1ItemStack.itemID)){
				if(partner.getHasStack()){
					if (BattlegearUtils.isMainHand(partner.getStack().itemID)){
						return BattlegearUtils.isOffHand(par1ItemStack.itemID) ? super.isItemValid(par1ItemStack) : false;
					}else{
						return false;
					}
				}else{
					return super.isItemValid(par1ItemStack);
				}
			}else{
				return false;
			}
		}
	}
	
	

}
