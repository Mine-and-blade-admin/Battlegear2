package mods.battlegear2.items;


import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowEvent;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemQuiver extends Item implements IArrowContainer2, IDyable {
    public Icon quiverDetails;
    public Icon quiverArrows;

    public ItemQuiver(int id) {
        super(id);
        this.setMaxStackSize(1);
    }

    private NBTTagCompound getNBTTagComound(ItemStack stack){
        if(!stack.hasTagCompound()){
            NBTTagCompound compound = new NBTTagCompound();
            compound.setByte("current", (byte) 0);
            stack.setTagCompound(compound);
        }
        return stack.getTagCompound();
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        quiverDetails = par1IconRegister.registerIcon("battlegear2:quiver/quiver-details");
        quiverArrows = par1IconRegister.registerIcon("battlegear2:quiver/quiver-arrows");

    }

    @Override
    public int getSlotCount(ItemStack container) {
        return 4;
    }

    @Override
    public int getSelectedSlot(ItemStack container) {
        return getNBTTagComound(container).getByte("current");
    }

    @Override
    public void setSelectedSlot(ItemStack container, int newSlot) {
        getNBTTagComound(container).setByte("current", (byte)newSlot);
    }

    @Override
    public ItemStack getStackInSlot(ItemStack container, int slot) {
        NBTTagCompound compound = getNBTTagComound(container);
        if(compound.hasKey("Slot"+slot)){
            return ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Slot"+slot));
        }else{
            return null;
        }
    }

    @Override
    public void setStackInSlot(ItemStack container, int slot, ItemStack stack) {
        NBTTagCompound compound = getNBTTagComound(container);
        if(stack == null){
            compound.removeTag("Slot"+slot);
        }else{
            NBTTagCompound newSlotCompound = new NBTTagCompound();

            stack.writeToNBT(newSlotCompound);
            compound.setCompoundTag("Slot"+slot, newSlotCompound);
        }
    }

    @Override
    public boolean hasArrowFor(ItemStack stack, ItemStack bow, EntityPlayer player, int slot) {
        return bow != null && bow.getItem() instanceof ItemBow && ((IArrowContainer2)stack.getItem()).getStackInSlot(stack, slot) != null;
    }

    @Override
    public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge) {
        ItemStack selected = getStackInSlot(stack, getSelectedSlot(stack));
        if(selected == null)
            return null;
        else {
            Class clazz = QuiverArrowRegistry.getArrowClass(selected);

            if(clazz != null){

                try {
                    return (EntityArrow)clazz.getConstructor(World.class, EntityLivingBase.class, Float.TYPE)
                            .newInstance(player.worldObj, player, charge);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

    }

    @Override
    public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {

    }

    @Override
    public void onPreArrowFired(QuiverArrowEvent arrowEvent) {
    }

    @Override
    public boolean isCraftableWithArrows(ItemStack stack, ItemStack arrows) {
        return arrows != null &&
                (arrows.getItem().itemID == Item.arrow.itemID ||
                        arrows.getItem().itemID == BattlegearConfig.MbArrows.itemID);
    }


    @Override
    public ItemStack addArrows(ItemStack container, ItemStack newStack) {
        if(newStack != null){
            int left_over = newStack.stackSize;
            int slotCount = getSlotCount(container);
            for(int i = 0; i < slotCount && left_over > 0; i++){
                ItemStack slotStack = getStackInSlot(container, i);
                if(slotStack == null){
                    newStack.stackSize = left_over;
                    setStackInSlot(container, i, newStack);
                    left_over = 0;
                }else{

                    if(newStack.itemID == slotStack.itemID){
                        int newSize = Math.min(64, slotStack.stackSize + left_over);
                        left_over = left_over - (newSize - slotStack.stackSize);
                        slotStack.stackSize = newSize;
                        setStackInSlot(container, i, slotStack);
                    }
                }
            }

            if(left_over > 0 ){
                newStack.stackSize = left_over;
                return newStack;
            }

        }

        return null;
    }


    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(stack, par2EntityPlayer, list, par4);

        list.add(String.format("%s",StatCollector.translateToLocal("attribute.quiver.arrow.count")));

        int slotCount = getSlotCount(stack);
        int selected = getSelectedSlot(stack);
        for(int i = 0; i < slotCount; i++){
            ItemStack slotStack = getStackInSlot(stack, i);
            if(slotStack != null){
                list.add(String.format(" %s%s: %s x %s", i,
                        i==selected?EnumChatFormatting.DARK_GREEN:EnumChatFormatting.GOLD,
                        slotStack.stackSize,
                        StatCollector.translateToLocal(slotStack.getItem().getUnlocalizedName(slotStack)+".name")));
            }else{
                list.add(String.format(" %s%s: %s", i,
                        i==selected?EnumChatFormatting.DARK_GREEN:EnumChatFormatting.GOLD,
                        StatCollector.translateToLocal("attribute.quiver.arrow.empty")));
            }
        }

    }


    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return (!par1ItemStack.hasTagCompound() ? false : (!par1ItemStack.getTagCompound().hasKey("display") ? false : par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color")));
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    public int getColor(ItemStack par1ItemStack)
    {
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                return getDefaultColor(par1ItemStack);
            }
            else
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
                return nbttagcompound1 == null ? getDefaultColor(par1ItemStack): (nbttagcompound1.hasKey("color") ? nbttagcompound1.getInteger("color") : getDefaultColor(par1ItemStack));
            }
        }
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    public void removeColor(ItemStack par1ItemStack)
    {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound != null)
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1.hasKey("color"))
                {
                    nbttagcompound1.removeTag("color");
                }
            }
    }

    @Override
    public int getDefaultColor(ItemStack par1ItemStack) {
        return 0xFFC65C35;
    }

    @Override
    public void setColor(ItemStack par1ItemStack, int par2)
    {

            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                nbttagcompound = new NBTTagCompound();
                par1ItemStack.setTagCompound(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (!nbttagcompound.hasKey("display"))
            {
                nbttagcompound.setCompoundTag("display", nbttagcompound1);
            }

            nbttagcompound1.setInteger("color", par2);
    }
}
