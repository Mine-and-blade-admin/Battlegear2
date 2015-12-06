package mods.battlegear2.api.core;

import net.minecraft.block.Block;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * User: nerd-boy
 * Date: 15/07/13
 * Time: 3:08 PM
 * Replacement for the player inventory
 */
public class InventoryPlayerBattle extends InventoryPlayer {
    //Mark the inventory content as dirty to be send to the client
    public boolean hasChanged = true;
    //The offsets used
    public static int ARMOR_OFFSET = 100;
    public static int OFFSET = 150;
    public static int WEAPON_SETS = 3;

    public static int EXTRA_ITEMS = WEAPON_SETS * 2;
    //The "battle" extra slots
    public ItemStack[] extraItems;

    public InventoryPlayerBattle(EntityPlayer entityPlayer) {
        super(entityPlayer);
        extraItems = new ItemStack[EXTRA_ITEMS];
    }

    /**
     * @return true if the current item value is offset in the battle slot range
     */
    public boolean isBattlemode() {
        return this.currentItem >= OFFSET && this.currentItem < OFFSET + EXTRA_ITEMS;
    }

    /**
     * Resize currentItem "battle" upper bound for all players to fit this player's.
     */
    public void resizeExtra(){
        if(EXTRA_ITEMS < extraItems.length){
            EXTRA_ITEMS = extraItems.length;
        }
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
                    return mainInventory.length - 1;//Between 36 and 99
                }
                break;
            case ARMOR:
                if(ARMOR_OFFSET+armorInventory.length+1<OFFSET) {
                    temp = new ItemStack[armorInventory.length + 1];
                    System.arraycopy(armorInventory, 0, temp, 0, armorInventory.length);
                    armorInventory = temp;
                    return ARMOR_OFFSET + armorInventory.length - 1;//Between 104 and 149
                }
                break;
            case BATTLE:
                temp = new ItemStack[extraItems.length+1];
                System.arraycopy(extraItems, 0, temp, 0, extraItems.length);
                extraItems = temp;
                resizeExtra();
                return OFFSET + extraItems.length - 1;
        }
        return Integer.MIN_VALUE;//Impossible because of byte cast in inventory NBT
    }

    /**
     * Patch used for "set current slot" vanilla packets
     * @param id the value to test for currentItem setting
     * @return true if it is possible for currentItem to be set with this value
     */
    public static boolean isValidSwitch(int id) {
        return (id >= 0 && id < getHotbarSize()) || (id >= OFFSET && id < OFFSET + EXTRA_ITEMS);
    }

    /**
     * Returns a slot index in extra inventory containing a specific itemID
     */
    private int getInventorySlotContainItem(Item par1) {
        for (int j = 0; j < this.extraItems.length; ++j) {
            if (this.extraItems[j] != null && this.extraItems[j].getItem() == par1) {
                return j;
            }
        }
        return -1;
    }

    /**
     * Allows the item switching in "battlemode", delegates to parent method if in normal mode
     * @return the currently selected {@link ItemStack}
     */
    @Override
    public ItemStack getCurrentItem() {
        return isBattlemode() ? extraItems[currentItem - OFFSET] : super.getCurrentItem();
    }

    /**
     * Changes currentItem and currentItemStack based on a given target
     * @param targetItem the newly selected item
     * @param targetDamage the newly selected item damage
     * @param compareWithDamage if item damage should matter when searching for the target in the inventory
     * @param forceInEmptySlots if the newly selected item should be forced in empty slots if it couldn't be found as-is
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setCurrentItem(Item targetItem, int targetDamage, boolean compareWithDamage, boolean forceInEmptySlots) {
        if (!isBattlemode())
            super.setCurrentItem(targetItem, targetDamage, compareWithDamage, forceInEmptySlots);
    }

    /**
     * Scroll the currentItem possible values
     * @param direction if <0: in the natural order, if >0: in the opposite order
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int direction) {
        if (isBattlemode()) {

        	if (direction > 0){
                currentItem--;
                if (currentItem < OFFSET) {
                    currentItem = OFFSET + WEAPON_SETS - 1;
                }
            }else if (direction != 0){
                currentItem++;
                if (currentItem >= OFFSET + WEAPON_SETS) {
                    currentItem = OFFSET;
                }
            }

        } else {
            super.changeCurrentItem(direction);
        }
    }

    /**
     * Clears all slots that contain the target item with given damage
     * @param targetId if null, not specific
     * @param targetDamage if <0, not specific
     * @param amount Number of items to remove. if 0, does not affect the inventory; if <=0, unlimited
     * @param targetNBT if null, not specific
     * @return the total number of items cleared
     */
    @Override
    public int func_174925_a(Item targetId, int targetDamage, int amount, NBTTagCompound targetNBT) {
        int stacks = super.func_174925_a(targetId, targetDamage, amount, targetNBT);
        if(amount > 0 && stacks >= amount){
            return stacks;
        }
        for (int i = 0; i < extraItems.length; i++) {
            ItemStack stack = extraItems[i];
            if (stack != null &&
                    (targetId == null || stack.getItem() == targetId) &&
                    (targetDamage <= -1 || stack.getMetadata() == targetDamage) &&
                    (targetNBT == null || CommandTestForBlock.func_175775_a(targetNBT, stack.getTagCompound(), true))) {
                int temp = amount <= 0 ? stack.stackSize : Math.min(amount - stacks, stack.stackSize);
                stacks += temp;
                if(amount != 0) {
                    extraItems[i].stackSize -= temp;
                    if(extraItems[i].stackSize == 0){
                        extraItems[i] = null;
                        hasChanged = true;
                    }
                    if(amount > 0 && stacks >= amount){
                        return stacks;
                    }
                }
            }
        }
        return stacks;
    }

    /**
     *  Called by EntityPlayer#onLivingUpdate(), to animate the item being picked up and tick it
     */
    @Override
    public void decrementAnimations() {
    	super.decrementAnimations();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (this.extraItems[i] != null) {
                this.extraItems[i].updateAnimation(this.player.worldObj, this.player, i + OFFSET, this.currentItem == i + OFFSET);
            }
        }
    }

    /**
     * Removed one item of specified itemID from inventory (if it is in a stack, the stack size will reduce with 1)
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
     * @param par1 the item to search for
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
     * Removes from an inventory slot up to a specified number of items and returns them in a new stack.
     * @param slot to remove from
     * @param amount to remove in the item stack
     * @return the removed items in a item stack, if any
     */
    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot >= OFFSET) {
            ItemStack targetStack = extraItems[slot - OFFSET];

            if (targetStack != null) {
                hasChanged = true;
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

    /**
     * Remove the stack in given slot, and return it
     * @param slot to get the content from
     * @return the stack that is stored in given slot
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack result = getStackInSlot(slot);
        setInventorySlotContents(slot, null);
        return result;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory
     * @param slot whose content will change
     * @param itemStack to put in the slot
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        setInventorySlotContents(slot, itemStack, true);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory, mark as dirty according to the boolean argument
     * @param slot whose content will change
     * @param itemStack to put in the slot
     * @param changed if the inventory packet should be sent next tick
     */
    public void setInventorySlotContents(int slot, ItemStack itemStack, boolean changed) {
        if (slot >= OFFSET) {
            hasChanged = changed;
            extraItems[slot - OFFSET] = itemStack;
        } else {
            super.setInventorySlotContents(slot, itemStack);
        }
    }

    /**
     * UNUSED
     * Get the current item "action value" against a block
     * @param block that the player is acting against
     * @return some action value of the current item against given block
     */
    @Override
    public float getStrVsBlock(Block block) {
        if (isBattlemode()) {
            ItemStack currentItemStack = getCurrentItem();
            return currentItemStack != null ? currentItemStack.getStrVsBlock(block) : 1.0F;

        } else {
            return super.getStrVsBlock(block);
        }
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +150
     * for battle slots).
     */
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

    /**
     * Reads from the given tag list, resize each arrays to maximum required and fills the slots in the inventory with the correct items.
     */
    @Override
    public void readFromNBT(NBTTagList nbtTagList) {
        int highestMain = mainInventory.length, highestArmor = armorInventory.length, highestExtra = extraItems.length;
        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbtTagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 0 && j < ARMOR_OFFSET) {
                if(j >= highestMain)
                    highestMain = j + 1;
            }
            else if (j >= ARMOR_OFFSET && j < OFFSET) {
                if(j - ARMOR_OFFSET >= highestArmor)
                    highestArmor = j + 1 - ARMOR_OFFSET;
            }
            else if (j >= OFFSET && j < 255) {
                if(j - OFFSET >= highestExtra)
                    highestExtra = j + 1 - OFFSET;
            }
        }
        this.mainInventory = new ItemStack[highestMain];
        this.armorInventory = new ItemStack[highestArmor];
        this.extraItems = new ItemStack[highestExtra];
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
        resizeExtra();
    }

    /**
     *
     * @param slot to get the content from
     * @return the content of the given slot
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= OFFSET) {
            return extraItems[slot - OFFSET];
        } else {
            return super.getStackInSlot(slot);
        }
    }

    /**
     *
     * @return the usual number of slots for vanilla inventory (not hardcoded)
     */
    @Override
    public int getSizeInventory() {
        return this.mainInventory.length + this.armorInventory.length;
    }

    /**
     * Drop all slots content, and clear them
     */
    @Override
    public void dropAllItems() {
        hasChanged = true;
    	super.dropAllItems();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (this.extraItems[i] != null) {
                this.player.dropItem(this.extraItems[i], true, false);
                this.extraItems[i] = null;
            }
        }
    }

    /**
     * Copy the slots content from another instance, usually for changing dimensions
     * @param par1InventoryPlayer the instance to copy from
     */
    @Override
    public void copyInventory(InventoryPlayer par1InventoryPlayer) {
        this.mainInventory = new ItemStack[par1InventoryPlayer.mainInventory.length];
        this.armorInventory = new ItemStack[par1InventoryPlayer.armorInventory.length];
        super.copyInventory(par1InventoryPlayer);
        if (par1InventoryPlayer instanceof InventoryPlayerBattle) {
            this.extraItems = new ItemStack[((InventoryPlayerBattle) par1InventoryPlayer).extraItems.length];
            for (int i = 0; i < extraItems.length; i++) {
                this.extraItems[i] = ItemStack.copyItemStack(par1InventoryPlayer.getStackInSlot(i + OFFSET));
            }
        }
    }

    @Override
    public void clear()
    {
        super.clear();
        for (int i = 0; i < this.extraItems.length; ++i)
        {
            this.extraItems[i] = null;
        }
    }

    /**
     * Get the offset item (for the left hand)
     * @return the item held in left hand, if any
     */
    public ItemStack getCurrentOffhandWeapon(){
        if(isBattlemode()){
            if(isOffset())
                return getStackInSlot(currentItem);
            return getStackInSlot(currentItem + WEAPON_SETS);
        }else{
            return null;
        }
    }

    /**
     * Get the item in the opposite hand
     * If currentItem is set to right hand, return the left hand
     * If currentItem is set to left hand, return the right hand
     * If not in battle mode, return null
     */
    public ItemStack getCurrentOppositeHand(){
        if(isBattlemode()){
            if(isOffset())
                return getStackInSlot(currentItem - WEAPON_SETS);
            return getStackInSlot(currentItem + WEAPON_SETS);
        }else{
            return null;
        }
    }

    /**
     * Swap the currentItem to the opposite hand
     * WARNING Calling this <strong>twice</strong> is required to put the game back on its feet
     * @return the new currentItem value
     */
    public int swapHandItem(){
        if(isBattlemode()){
            if(isOffset())
                currentItem -= WEAPON_SETS;
            else
                currentItem += WEAPON_SETS;
        }
        return currentItem;
    }

    /**
     * @return true if the currentItem is in the left hand
     */
    public boolean isOffset(){
        return currentItem + WEAPON_SETS >= OFFSET + EXTRA_ITEMS;
    }
}
