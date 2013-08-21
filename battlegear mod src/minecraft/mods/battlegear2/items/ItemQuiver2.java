package mods.battlegear2.items;


import mods.battlegear2.api.IArrowContainer2;
import mods.battlegear2.api.QuiverArrowEvent;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemQuiver2 extends Item implements IArrowContainer2 {
    Icon emptyQuiver;

    public ItemQuiver2(int id) {
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
        emptyQuiver = par1IconRegister.registerIcon("battlegear2:quiver-empty");
    }

    /*
     *  FIXME: MC doesn't use stack aware rendering for items in GUI, will have to make an IItemRenderer if we want this
    */
    @Override
    public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        int maxSlot = getSlotCount(stack);
        for(int i = 0; i < maxSlot; i++){
            if(getStackInSlot(stack, i) != null){
                return super.getIcon(stack, renderPass, player, usingItem, useRemaining);

            }
        }
        return emptyQuiver;
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

        if(getStackInSlot(stack, getSelectedSlot(stack)) == null)
            return null;
        else
            return new EntityArrow(world, player, charge);
    }

    @Override
    public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {
        int selectedSlot = getSelectedSlot(stack);
        ItemStack arrowStack = getStackInSlot(stack, selectedSlot);
        arrowStack.stackSize --;
        if(arrowStack.stackSize == 0){
            arrowStack = null;
        }
        setStackInSlot(stack, selectedSlot, arrowStack);
    }

    @Override
    public void onPreArrowFired(QuiverArrowEvent arrowEvent) {
    }

    @Override
    public boolean isCraftableWithArrows(ItemStack stack, ItemStack arrows) {
        return arrows != null && arrows.getItem().itemID == Item.arrow.itemID;
    }


    @Override
    public ItemStack addArrows(ItemStack container, ItemStack newStack) {
        if(newStack != null){
            int left_over = newStack.stackSize;
            int slotCount = getSlotCount(container);
            for(int i = 0; i < slotCount && left_over > 0; i++){
                ItemStack slotStack = getStackInSlot(container, i);
                //System.out.println(slotStack);
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
                        StatCollector.translateToLocal(slotStack.getItem().getUnlocalizedName(slotStack))));
            }else{
                list.add(String.format(" %s%s: %s", i,
                        i==selected?EnumChatFormatting.DARK_GREEN:EnumChatFormatting.GOLD,
                        StatCollector.translateToLocal("attribute.quiver.arrow.empty")));
            }
        }

    }


}
