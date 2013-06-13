package mods.battlegear2.common.gui;

import javax.swing.text.rtf.RTFEditorKit;

import com.google.common.collect.ContiguousSet;

import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.common.heraldry.SigilHelper;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerHeraldry extends Container {

	SlotHeraldryItem heraldrySlot;
	//SlotHeraldryItem[] heraldrySlots;
	public byte[] code = SigilHelper.defaultSigil;
	
	public boolean isLocalWorld = false;
	private final EntityPlayer thePlayer;
	
	public ContainerHeraldry(InventoryPlayer inventoryPlayer, boolean local, EntityPlayer player){
		this.thePlayer = player;
		this.isLocalWorld = local;
	
		
		heraldrySlot = new SlotHeraldryItem(-10000, -10000);
		this.addSlotToContainer(heraldrySlot);
		
		for (int i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 108 + i * 18 - 12));
			}
		}
				
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 166-12));
		}
		/*heraldrySlots = new SlotHeraldryItem[16];
		//heraldrySlot[] = new SlotHeraldryItem(31, 70);
		for(int x = 0; x < 4; x++){
			for(int y = 0; y < 4; y ++){
				heraldrySlots[x*4+y] = new SlotHeraldryItem(7+(16*x), 7+(16*y)-16);
				this.addSlotToContainer(heraldrySlots[x*4+y]);
			}
		}*/
		setCode(code);

	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}


	public void setCode(byte[]code) {
		this.code = code;
		ItemStack heraldry = new ItemStack(BattlegearConfig.heradricItem);
		((IHeraldyItem)heraldry.getItem()).setHeraldryCode(heraldry, code);
		heraldrySlot.inventory.setInventorySlotContents(0,heraldry);
		/*
		for (Slot slot : heraldrySlots) {
			slot.inventory.setInventorySlotContents(0,heraldry);
		}
		*/
		
	}
	
	private ItemStack generateStack(){
		ItemStack heraldry = new ItemStack(BattlegearConfig.heradricItem);
		((IHeraldyItem)heraldry.getItem()).setHeraldryCode(heraldry, code);
		return heraldry;
	}

	
	
	@Override
	public ItemStack slotClick(int par1, int par2, int par3,
			EntityPlayer par4EntityPlayer) {
		if(par1==0){
			ItemStack current = par4EntityPlayer.inventory.getItemStack();
			if(current == null){
				return super.slotClick(par1, par2, par3, par4EntityPlayer);
			}else if (current.itemID == BattlegearConfig.heradricItem.itemID){
				setCode(((IHeraldyItem)current.getItem()).getHeraldryCode(current));
				par4EntityPlayer.inventory.setItemStack(null);
				return null;
			}else{
				return null;
			}
			
		}else{
			return super.slotClick(par1, par2, par3, par4EntityPlayer);
		}
	}	

}
