package assets.battlegear2.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * The purpose of this class is to handle the extra functionality associated with the
 * extra inventory slots that have been added through the mod. Previously this functionality
 * was going to be used as a coremod, however due to the fact that the edits required
 * are quite extensive this has been moved into it's own class.</br>
 * 
 * There is a possibility this could negatively impact on compatibility (I know the mod
 * "Keep Inventory On Death" also replaces it, possibly the new version of Aether MIGHT
 * also). We can revisit if that is the case at a later date.
 * 
 * @author nerd-boy
 */
public class InventoryPlayerBattle extends InventoryPlayer{

	
	public static int OFFSET = 150;
	
	public static int WEAPON_SETS = 3;
	
	public static int EXTRA_INV_SIZE = WEAPON_SETS*2 + 6 + 6;
	
	
	public ItemStack[] extraItems = new ItemStack[EXTRA_INV_SIZE];
	
	public InventoryPlayerBattle(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
	}
	
	public boolean isBattlemode(){
		return this.currentItem < OFFSET + 2*WEAPON_SETS && this.currentItem >= OFFSET;
	}
	
	
	public static boolean isValidSwitch(int id) {
		return (id >= 0 && id < getHotbarSize()) || (id >= OFFSET && id < OFFSET+2*WEAPON_SETS);
	}
	
	

	@Override
	public ItemStack getCurrentItem() {
		return isBattlemode() ? extraItems[currentItem-OFFSET] : super.getCurrentItem();
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setCurrentItem(int par1, int par2, boolean par3, boolean par4) {
		if(!isBattlemode())
			super.setCurrentItem(par1, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void changeCurrentItem(int direction) {
		if(isBattlemode()){
			
			direction = Math.min(direction, 1);
			direction = Math.max(direction, -1);
			
			for(currentItem -= direction; currentItem < OFFSET; currentItem+=WEAPON_SETS){}
			
			while(currentItem >= OFFSET+WEAPON_SETS){
				currentItem -= WEAPON_SETS;
			}
			
		}else{
			super.changeCurrentItem(direction);
		}
	}

	@Override
	public int clearInventory(int targetId, int targetDamage) {
		int stacks =  super.clearInventory(targetId, targetDamage);
		
		for(int i = 0; i < extraItems.length; i++){
			if (extraItems[i] != null &&
					(targetId <= -1 || extraItems[i].itemID == targetId) && 
					(targetDamage <= -1 || extraItems[i].getItemDamage() == targetDamage)){
				
				stacks += extraItems[i].stackSize;
                extraItems[i] = null;
            }
		}
		
		return stacks;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		
		if(slot >= OFFSET){
			ItemStack targetStack = extraItems[slot-OFFSET];
			
			if(targetStack != null){
				
				if(targetStack.stackSize <= amount){
					
					extraItems[slot-OFFSET] = null;
					return targetStack;
					
				}else{
					
					targetStack = extraItems[slot-OFFSET].splitStack(amount);
					
					if (extraItems[slot].stackSize == 0)
	                {
	                    extraItems[slot] = null;
	                }

	                return targetStack;
				}
			}
			
			return null;
			
		}else{
			return super.decrStackSize(slot, amount);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		
		if(slot >= OFFSET){
			return extraItems[slot-OFFSET];
		}else{
			return super.getStackInSlotOnClosing(slot);
		}
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot >= OFFSET){
			return extraItems[slot-OFFSET];
		}else{
			return super.getStackInSlot(slot);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		
		if(itemStack != null && itemStack.itemID == 0)
			itemStack = null;
		
		if(slot >= OFFSET){
			extraItems[slot-OFFSET] = itemStack;
		}else{
			super.setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public void copyInventory(InventoryPlayer par1InventoryPlayer) {
		extraItems = new ItemStack[EXTRA_INV_SIZE];
		
		super.copyInventory(par1InventoryPlayer);
		
		if(par1InventoryPlayer instanceof InventoryPlayerBattle){
			for(int i = 0; i < extraItems.length; i++){
				this.extraItems[i] = par1InventoryPlayer.getStackInSlot(i+OFFSET);
			}
		}
	}

	@Override
	public NBTTagList writeToNBT(NBTTagList par1nbtTagList) {
		NBTTagList nbtList =  super.writeToNBT(par1nbtTagList);
		NBTTagCompound nbttagcompound;
		
		for (int i = 0; i < extraItems.length; ++i){
			if(extraItems[i] != null){
				nbttagcompound = new NBTTagCompound();
				//This will be -ve, but meh still works
                nbttagcompound.setByte("Slot", (byte)(i+OFFSET));
                this.extraItems[i].writeToNBT(nbttagcompound);
                nbtList.appendTag(nbttagcompound);
			}
        }
		return nbtList;
	}

	@Override
	public void readFromNBT(NBTTagList nbtTagList) {
		super.readFromNBT(nbtTagList);
		
		extraItems = new ItemStack[EXTRA_INV_SIZE];
		
		for (int i = 0; i < nbtTagList.tagCount(); ++i){
			NBTTagCompound nbttagcompound = (NBTTagCompound)nbtTagList.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            
            if(j >= OFFSET){
	            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
	            if (itemstack != null){
	            	extraItems[j-OFFSET] = itemstack;	            	
	            }
            }
        }
	}

	@Override
	public float getStrVsBlock(Block block) {
		if(isBattlemode()){
			 ItemStack currentItemStack = getCurrentItem();

		     return currentItemStack != null ? currentItemStack.getStrVsBlock(block) : 1.0F;
		     
		}else{
			return super.getStrVsBlock(block);
		}
	}

	/*@Override
	public int getDamageVsEntity(Entity par1Entity) {
		if(isBattlemode()){
			ItemStack currentItemStack = this.getCurrentItem();
			
	        return currentItemStack != null ? currentItemStack.getDamageVsEntity(par1Entity) : 1;
		}else{
			return super.getDamageVsEntity(par1Entity);
		}
	}
	*/
	public ItemStack getCurrentOffItem(){
		if(isBattlemode()){
			return this.getStackInSlot(currentItem+3);
		}else{
			return null;
		}
	}
	
	
	
			

}
