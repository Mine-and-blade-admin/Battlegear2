package mods.battlegear2.api.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Arrays;

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
    public static int OFFSET = 151;
    public static int WEAPON_SETS = 3;

    public static int EXTRA_ITEMS = WEAPON_SETS * 2;
    //The "battle" extra slots
    public ItemStack[] extraItems;

    public InventoryPlayerBattle(EntityPlayer entityPlayer) {
        super(entityPlayer);
        extraItems = new ItemStack[EXTRA_ITEMS];
        Arrays.fill(extraItems, ItemStack.EMPTY);
    }

    /**
     * @return true if the current item value is offset in the battle slot range
     */
    public boolean isBattlemode() {
        return this.currentItem >= OFFSET && this.currentItem < OFFSET + EXTRA_ITEMS;
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
            if (this.extraItems[j].getItem() == par1) {
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
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setPickedItemStack(@Nonnull ItemStack targetItem) {
        if (!isBattlemode())
            super.setPickedItemStack(targetItem);
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
            }else if (direction != 0){
                currentItem++;
            }
            if (currentItem < OFFSET) {
                currentItem = OFFSET + WEAPON_SETS - 1;
            }else if (currentItem >= OFFSET + WEAPON_SETS) {
                currentItem = OFFSET;
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
    public int clearMatchingItems(Item targetId, int targetDamage, int amount, NBTTagCompound targetNBT) {
        int stacks = super.clearMatchingItems(targetId, targetDamage, amount, targetNBT);
        if(amount > 0 && stacks >= amount){
            return stacks;
        }
        for (int i = 0; i < extraItems.length; i++) {
            ItemStack stack = extraItems[i];
            if (stack != null &&
                    (targetId == null || stack.getItem() == targetId) &&
                    (targetDamage <= -1 || stack.getMetadata() == targetDamage) &&
                    (targetNBT == null || NBTUtil.areNBTEquals(targetNBT, stack.getTagCompound(), true))) {
                int temp = amount <= 0 ? stack.getCount() : Math.min(amount - stacks, stack.getCount());
                stacks += temp;
                if(amount != 0) {
                    extraItems[i].shrink(temp);
                    if(extraItems[i].getCount() == 0){
                        extraItems[i] = ItemStack.EMPTY;
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
            if (!this.extraItems[i].isEmpty()) {
                this.extraItems[i].updateAnimation(this.player.world, this.player, i + OFFSET, this.currentItem == i + OFFSET);
            }
        }
    }

    /**
     * Get if a specified item id is inside the inventory.
     * @param par1 the item to search for
     */
    @Override
    public boolean hasItemStack(ItemStack par1) {
        if (super.hasItemStack(par1)) {
            return true;
        } else {
            for(ItemStack in : extraItems){
                if(in.isItemEqual(par1)){
                    return true;
                }
            }
            return false;
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

            if (!targetStack.isEmpty()) {
                hasChanged = true;
                if (targetStack.getCount() <= amount) {
                    extraItems[slot - OFFSET] = ItemStack.EMPTY;
                    return targetStack;
                } else {
                    targetStack = extraItems[slot - OFFSET].splitStack(amount);
                    if (extraItems[slot - OFFSET].getCount() == 0) {
                        extraItems[slot - OFFSET] = ItemStack.EMPTY;
                    }
                    return targetStack;
                }
            }

            return ItemStack.EMPTY;

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
    public ItemStack removeStackFromSlot(int slot) {
        ItemStack result = getStackInSlot(slot);
        setInventorySlotContents(slot, ItemStack.EMPTY);
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
    public float getStrVsBlock(@Nonnull IBlockState block) {
        if (isBattlemode()) {
            ItemStack currentItemStack = getCurrentItem();
            return !currentItemStack.isEmpty() ? currentItemStack.getStrVsBlock(block) : 1.0F;

        } else {
            return super.getStrVsBlock(block);
        }
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +150
     * for battle slots).
     */
    @Nonnull
    @Override
    public NBTTagList writeToNBT(@Nonnull NBTTagList par1nbtTagList) {
        NBTTagList nbtList = super.writeToNBT(par1nbtTagList);
        NBTTagCompound nbttagcompound;

        for (int i = 0; i < extraItems.length; ++i) {
            if (!extraItems[i].isEmpty()) {
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
        super.readFromNBT(nbtTagList);
        this.extraItems = new ItemStack[extraItems.length];
        Arrays.fill(extraItems, ItemStack.EMPTY);
        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbtTagList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = new ItemStack(nbttagcompound);
            if (j >= OFFSET && j - OFFSET < this.extraItems.length) {
                this.extraItems[j - OFFSET] = itemstack;
            }
        }
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
        return this.mainInventory.size() + this.armorInventory.size();
    }

    /**
     * Drop all slots content, and clear them
     */
    @Override
    public void dropAllItems() {
        hasChanged = true;
    	super.dropAllItems();
        for (int i = 0; i < this.extraItems.length; ++i) {
            if (!this.extraItems[i].isEmpty()) {
                this.player.dropItem(this.extraItems[i], true, false);
                this.extraItems[i] = ItemStack.EMPTY;
            }
        }
    }

    /**
     * Copy the slots content from another instance, usually for changing dimensions
     * @param par1InventoryPlayer the instance to copy from
     */
    @Override
    public void copyInventory(InventoryPlayer par1InventoryPlayer) {
        super.copyInventory(par1InventoryPlayer);
        if (par1InventoryPlayer instanceof InventoryPlayerBattle) {
            this.extraItems = new ItemStack[((InventoryPlayerBattle) par1InventoryPlayer).extraItems.length];
            for (int i = 0; i < extraItems.length; i++) {
                this.extraItems[i] = par1InventoryPlayer.getStackInSlot(i + OFFSET).copy();
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < this.extraItems.length; ++i)
        {
            this.extraItems[i] = ItemStack.EMPTY;
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
            return offHandInventory.get(0);
        }
    }

    /**
     * Get the item in the opposite hand
     * If currentItem is set to right hand, return the left hand
     * If currentItem is set to left hand, return the right hand
     * If not in battle mode, return ItemStack.EMPTY
     */
    public ItemStack getCurrentOppositeHand(){
        if(isBattlemode()){
            if(isOffset())
                return getStackInSlot(currentItem - WEAPON_SETS);
            return getStackInSlot(currentItem + WEAPON_SETS);
        }else{
            return ItemStack.EMPTY;
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
