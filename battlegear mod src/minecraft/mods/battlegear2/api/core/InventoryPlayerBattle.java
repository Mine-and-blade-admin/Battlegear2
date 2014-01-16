package mods.battlegear2.api.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * User: nerd-boy
 * Date: 15/07/13
 * Time: 3:08 PM
 * Replacement for the player inventory
 */
public class InventoryPlayerBattle extends InventoryPlayer {

    public boolean hasChanged = true;

    public static int OFFSET = 150;
    public static int WEAPON_SETS = 3;

    public static int EXTRA_ITEMS = WEAPON_SETS * 2;
    public static int EXTRA_INV_SIZE = WEAPON_SETS * 2 + 6 + 6;

    public ItemStack[] extraItems;


    public InventoryPlayerBattle(EntityPlayer entityPlayer) {
        super(entityPlayer);
        extraItems = new ItemStack[EXTRA_INV_SIZE];
    }

    public boolean isBattlemode() {
        return this.currentItem < OFFSET + 2 * WEAPON_SETS && this.currentItem >= OFFSET;
    }

    public static boolean isValidSwitch(int id) {
        return (id >= 0 && id < getHotbarSize()) || (id >= OFFSET && id < OFFSET + 2 * WEAPON_SETS);
    }

    /**
     * Returns a slot index in main inventory containing a specific itemID
     */
    private int getInventorySlotContainItem(int par1) {
        for (int j = 0; j < this.extraItems.length; ++j) {
            if (this.extraItems[j] != null && this.extraItems[j].itemID == par1) {
                return j;
            }
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int getInventorySlotContainItemAndDamage(int par1, int par2) {
        for (int k = 0; k < this.extraItems.length; ++k) {
            if (this.extraItems[k] != null && this.extraItems[k].itemID == par1 && this.extraItems[k].getItemDamage() == par2) {
                return k;
            }
        }
        return -1;
    }

    @Override
    public ItemStack getCurrentItem() {
        return isBattlemode() ? extraItems[currentItem - OFFSET] : super.getCurrentItem();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setCurrentItem(int par1, int par2, boolean par3, boolean par4) {
        if (!isBattlemode())
            super.setCurrentItem(par1, par2, par3, par4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int direction) {
        hasChanged = true;

        if (isBattlemode()) {

        	if (direction > 0){
        		direction = 1;
            }else if (direction != 0){
            	direction = -1;
            }

            //noinspection StatementWithEmptyBody
            for (currentItem -= direction; currentItem < OFFSET; currentItem += WEAPON_SETS) {
            }

            while (currentItem >= OFFSET + WEAPON_SETS) {
                currentItem -= WEAPON_SETS;
            }

        } else {
            super.changeCurrentItem(direction);
        }
    }

    @Override
    public int clearInventory(int targetId, int targetDamage) {
        hasChanged = true;

        int stacks = super.clearInventory(targetId, targetDamage);

        for (int i = 0; i < extraItems.length; i++) {
            if (extraItems[i] != null &&
                    (targetId <= -1 || extraItems[i].itemID == targetId) &&
                    (targetDamage <= -1 || extraItems[i].getItemDamage() == targetDamage)) {

                stacks += extraItems[i].stackSize;
                extraItems[i] = null;
            }
        }

        return stacks;
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    @Override
    public void decrementAnimations() {
    	super.decrementAnimations();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (this.extraItems[i] != null) {
                this.extraItems[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem + OFFSET == i);
            }
        }
    }


    /**
     * removed one item of specified itemID from inventory (if it is in a stack, the stack size will reduce with 1)
     */
    @Override
    public boolean consumeInventoryItem(int par1) {
        int j = this.getInventorySlotContainItem(par1);

        if (j < 0) {
            return super.consumeInventoryItem(par1);
        } else {
            hasChanged = true;
            if (--this.extraItems[j].stackSize <= 0) {
                this.extraItems[j] = null;
            }

            return true;
        }
    }


    /**
     * Get if a specifiied item id is inside the inventory.
     */
    @Override
    public boolean hasItem(int par1) {
        if (super.hasItem(par1)) {
            return true;
        } else {
            int j = this.getInventorySlotContainItem(par1);
            return j >= 0;
        }
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    @Override
    public boolean addItemStackToInventory(ItemStack par1ItemStack) {
        return super.addItemStackToInventory(par1ItemStack);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        hasChanged = true;
        if (slot >= OFFSET) {
            ItemStack targetStack = extraItems[slot - OFFSET];

            if (targetStack != null) {

                if (targetStack.stackSize <= amount) {

                    extraItems[slot - OFFSET] = null;
                    return targetStack;

                } else {

                    targetStack = extraItems[slot - OFFSET].splitStack(amount);

                    if (extraItems[slot - OFFSET].stackSize == 0) {
                        extraItems[slot - OFFSET] = null;
                    }

                    return targetStack;
                }
            }

            return null;

        } else {
            return super.decrStackSize(slot, amount);
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (slot >= OFFSET) {
            return extraItems[slot - OFFSET];
        } else {
            return super.getStackInSlotOnClosing(slot);
        }
    }


    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        setInventorySlotContents(slot, itemStack, true);
    }

    public void setInventorySlotContents(int slot, ItemStack itemStack, boolean changed) {
        hasChanged = changed;
        if (itemStack != null && itemStack.itemID == 0)
            itemStack = null;

        if (slot >= OFFSET) {
            extraItems[slot - OFFSET] = itemStack;
        } else {
            super.setInventorySlotContents(slot, itemStack);
        }
    }

    @Override
    public float getStrVsBlock(Block block) {
        if (isBattlemode()) {
            ItemStack currentItemStack = getCurrentItem();

            return currentItemStack != null ? currentItemStack.getStrVsBlock(block) : 1.0F;

        } else {
            return super.getStrVsBlock(block);
        }
    }

    @Override
    public NBTTagList writeToNBT(NBTTagList par1nbtTagList) {
        NBTTagList nbtList = super.writeToNBT(par1nbtTagList);
        NBTTagCompound nbttagcompound;

        for (int i = 0; i < extraItems.length; ++i) {
            if (extraItems[i] != null) {
                nbttagcompound = new NBTTagCompound();
                //This will be -ve, but meh still works
                nbttagcompound.setByte("Slot", (byte) (i + OFFSET));
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

        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) nbtTagList.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= OFFSET) {
                ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
                if (itemstack != null) {
                    extraItems[j - OFFSET] = itemstack;
                }
            }
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= OFFSET) {
            return extraItems[slot - OFFSET];
        } else {
            return super.getStackInSlot(slot);
        }
    }

    @Override
    public void dropAllItems() {
    	super.dropAllItems();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (this.extraItems[i] != null) {
                this.player.dropPlayerItemWithRandomChoice(this.extraItems[i], true);
                this.extraItems[i] = null;
            }
        }
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();
        hasChanged = true;
    }

    @Override
    public void copyInventory(InventoryPlayer par1InventoryPlayer) {
        hasChanged = true;
        extraItems = new ItemStack[EXTRA_INV_SIZE];

        super.copyInventory(par1InventoryPlayer);

        if (par1InventoryPlayer instanceof InventoryPlayerBattle) {
            for (int i = 0; i < extraItems.length; i++) {
                this.extraItems[i] = par1InventoryPlayer.getStackInSlot(i + OFFSET);
            }
        }
    }

    public ItemStack getCurrentOffhandWeapon(){
        if(isBattlemode()){
            return getStackInSlot(currentItem+WEAPON_SETS);
        }else{
            return null;
        }
    }
}
