package mods.battlegear2.api.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

/**
 * User: nerd-boy
 * Date: 15/07/13
 * Time: 3:08 PM
 * Replacement for the player inventory
 */
public class InventoryPlayerBattle extends InventoryPlayer {

    public boolean hasChanged = true;
    public static int ARMOR_OFFSET = 100;
    public static int OFFSET = 150;
    public static int WEAPON_SETS = 3;

    public static int EXTRA_ITEMS = WEAPON_SETS * 2;
    public static int EXTRA_INV_SIZE = EXTRA_ITEMS + 6 + 6;

    public ItemStack[] extraItems;


    public InventoryPlayerBattle(EntityPlayer entityPlayer) {
        super(entityPlayer);
        extraItems = new ItemStack[EXTRA_INV_SIZE];
    }

    public boolean isBattlemode() {
        return this.currentItem >= OFFSET && this.currentItem < OFFSET + EXTRA_ITEMS;
    }

    /**
     * Returns a new slot index according to the type
     * @param type determines which inventory array to expand
     * @return the new slot index, or Integer.MIN_VALUE if it is not possible to expand further
     */
    public int requestNewSlot(InventorySlotType type){
        ItemStack[] temp;
        switch(type){
            case MAIN:
                if(mainInventory.length+1<ARMOR_OFFSET) {
                    temp = new ItemStack[mainInventory.length + 1];
                    System.arraycopy(mainInventory, 0, temp, 0, mainInventory.length);
                    mainInventory = temp;
                    return mainInventory.length;//Between 36 and 99
                }
                break;
            case ARMOR:
                if(ARMOR_OFFSET+armorInventory.length+1<OFFSET) {
                    temp = new ItemStack[armorInventory.length + 1];
                    System.arraycopy(armorInventory, 0, temp, 0, armorInventory.length);
                    armorInventory = temp;
                    return ARMOR_OFFSET + armorInventory.length;//Between 104 and 149
                }
                break;
            case BATTLE:
                temp = new ItemStack[extraItems.length+1];
                System.arraycopy(extraItems, 0, temp, 0, extraItems.length);
                extraItems = temp;
                return OFFSET + extraItems.length;
        }
        return Integer.MIN_VALUE;//Impossible because of byte cast in inventory NBT
    }

    public static boolean isValidSwitch(int id) {
        return (id >= 0 && id < getHotbarSize()) || (id >= OFFSET && id < OFFSET + EXTRA_ITEMS);
    }

    /**
     * Returns a slot index in main inventory containing a specific itemID
     */
    private int getInventorySlotContainItem(Item par1) {
        for (int j = 0; j < this.extraItems.length; ++j) {
            if (this.extraItems[j] != null && this.extraItems[j].getItem() == par1) {
                return j;
            }
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int getInventorySlotContainItemAndDamage(Item par1, int par2) {
        for (int k = 0; k < this.extraItems.length; ++k) {
            if (this.extraItems[k] != null && this.extraItems[k].getItem() == par1 && this.extraItems[k].getItemDamage() == par2) {
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
    public void func_146030_a(Item par1, int par2, boolean par3, boolean par4) {
        if (!isBattlemode())
            super.func_146030_a(par1, par2, par3, par4);
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
    public int clearInventory(Item targetId, int targetDamage) {
        hasChanged = true;

        int stacks = super.clearInventory(targetId, targetDamage);

        for (int i = 0; i < extraItems.length; i++) {
            if (extraItems[i] != null &&
                    (targetId == null || extraItems[i].getItem() == targetId) &&
                    (targetDamage <= -1 || extraItems[i].getItemDamage() == targetDamage)) {

                stacks += extraItems[i].stackSize;
                extraItems[i] = null;
            }
        }

        return stacks;
    }

    /**
     *  Called by EntityPlayer#onLivingUpdate(), usually to animate the item being picked up
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
    public boolean consumeInventoryItem(Item par1) {
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
     * Get if a specified item id is inside the inventory.
     */
    @Override
    public boolean hasItem(Item par1) {
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
        if (slot >= OFFSET) {
            extraItems[slot - OFFSET] = itemStack;
        } else {
            super.setInventorySlotContents(slot, itemStack);
        }
    }

    @Override
    public float func_146023_a(Block block) {
        if (isBattlemode()) {
            ItemStack currentItemStack = getCurrentItem();
            return currentItemStack != null ? currentItemStack.func_150997_a(block) : 1.0F;

        } else {
            return super.func_146023_a(block);
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
        this.mainInventory = new ItemStack[mainInventory.length];
        this.armorInventory = new ItemStack[armorInventory.length];
        this.extraItems = new ItemStack[extraItems.length];

        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbtTagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null) {
                if (j >= 0 && j < this.mainInventory.length) {
                    this.mainInventory[j] = itemstack;
                }
                else if (j >= ARMOR_OFFSET && j - ARMOR_OFFSET < this.armorInventory.length) {
                    this.armorInventory[j - ARMOR_OFFSET] = itemstack;
                }
                else if (j >= OFFSET && j - OFFSET < this.extraItems.length) {
                    this.extraItems[j - OFFSET] = itemstack;
                }
                else{
                    MinecraftForge.EVENT_BUS.post(new UnhandledInventoryItemEvent(player, j, itemstack));
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
    public int getSizeInventory() {
        return this.mainInventory.length + this.armorInventory.length;
    }

    @Override
    public void dropAllItems() {
    	super.dropAllItems();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (this.extraItems[i] != null) {
                this.player.func_146097_a(this.extraItems[i], true, false);
                this.extraItems[i] = null;
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        hasChanged = true;
    }

    @Override
    public void copyInventory(InventoryPlayer par1InventoryPlayer) {
        hasChanged = true;
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
