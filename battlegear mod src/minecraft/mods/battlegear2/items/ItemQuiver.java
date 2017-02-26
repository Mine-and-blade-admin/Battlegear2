package mods.battlegear2.items;

import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemQuiver extends Item implements IArrowContainer2, IDyable, ISheathed {
    public ItemQuiver() {
        super();
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
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float offX, float offY, float offZ) {
        EnumActionResult flag = EnumActionResult.FAIL;
        ItemStack stack = player.getHeldItem(hand);
        for(int i = 0; i < getSlotCount(stack);i++){
            ItemStack temp = getStackInSlot(stack, i);
            if(!temp.isEmpty()){
                EntityItem entityitem = ForgeHooks.onPlayerTossEvent(player, temp, true);
                if(entityitem!=null) {
                    entityitem.setNoPickupDelay();
                    entityitem.setOwner(player.getName());
                }
                setStackInSlot(stack, i, ItemStack.EMPTY);
                flag = EnumActionResult.SUCCESS;
            }
    	}
        return flag;
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
            return new ItemStack(compound.getCompoundTag("Slot"+slot));
        }else{
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStackInSlot(ItemStack container, int slot, ItemStack stack) {
        NBTTagCompound compound = getNBTTagComound(container);
        if(stack == null || stack.isEmpty()){
            compound.removeTag("Slot"+slot);
        }else{
            NBTTagCompound newSlotCompound = new NBTTagCompound();
            stack.writeToNBT(newSlotCompound);
            compound.setTag("Slot"+slot, newSlotCompound);
        }
    }

    @Override
    public boolean hasArrowFor(ItemStack stack, ItemStack bow, EntityPlayer player, int slot) {
        return bow != null && BattlegearUtils.isBow(bow.getItem()) && !((IArrowContainer2)stack.getItem()).getStackInSlot(stack, slot).isEmpty();
    }

    @Override
    public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge) {
        ItemStack selected = getStackInSlot(stack, getSelectedSlot(stack));
        if(selected.isEmpty())
            return null;
        else
            return QuiverArrowRegistry.getArrowType(selected, world, player, charge);
    }

    @Override
    public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {
        if(!player.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) == 0){
            int selectedSlot = getSelectedSlot(stack);
            ItemStack arrowStack = getStackInSlot(stack, selectedSlot);
            arrowStack.shrink(1);
            if(arrowStack.getCount() <= 0){
                //ForgeEventFactory.onPlayerDestroyItem(player, arrowStack);
                arrowStack = null;
            }
            setStackInSlot(stack, selectedSlot, arrowStack);
        }
    }

    @Override
    public void onPreArrowFired(PlayerEventChild.QuiverArrowEvent.Firing arrowEvent) {
        if(arrowEvent.getArcher().capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, arrowEvent.getBow()) > 0){
            arrowEvent.arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
        }
        writeBowNBT(arrowEvent.getBow(), getStackInSlot(arrowEvent.quiver, getSelectedSlot(arrowEvent.quiver)));
    }

    /**
     * Convenience feature for "AnonymousProductions" dude
     * @param bow
     * @param loadedArrow
     */
    public static void writeBowNBT(ItemStack bow, ItemStack loadedArrow) {
        NBTTagCompound tags = new NBTTagCompound();
        loadedArrow.writeToNBT(tags);
        bow.setTagInfo("Battlegear2-LoadedArrow", tags);
    }

    @Override
    public boolean isCraftableWithArrows(ItemStack stack, ItemStack arrows) {
        return QuiverArrowRegistry.isKnownArrow(arrows);
    }

    @Override
    public ItemStack addArrows(ItemStack container, ItemStack newStack) {
        if(!newStack.isEmpty()){
            int left_over = newStack.getCount();
            int slotCount = getSlotCount(container);
            for(int i = 0; i < slotCount && left_over > 0; i++){
                ItemStack slotStack = getStackInSlot(container, i);
                if(slotStack.isEmpty()){
                    newStack.setCount(left_over);
                    setStackInSlot(container, i, newStack);
                    left_over = 0;
                }else{
                    if(newStack.getItem() == slotStack.getItem() && newStack.getItemDamage() == slotStack.getItemDamage()){
                        int newSize = Math.min(slotStack.getMaxStackSize(), slotStack.getCount() + left_over);
                        left_over = left_over - (newSize - slotStack.getCount());
                        slotStack.setCount(newSize);
                        setStackInSlot(container, i, slotStack);
                    }
                }
            }
            if(left_over > 0){
                newStack.setCount(left_over);
                return newStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean renderDefaultQuiverModel(ItemStack container) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
        super.addInformation(stack, par2EntityPlayer, list, par4);

        list.add(I18n.format("attribute.quiver.arrow.count"));

        int slotCount = getSlotCount(stack);
        int selected = getSelectedSlot(stack);
        for(int i = 0; i < slotCount; i++){
            ItemStack slotStack = getStackInSlot(stack, i);
            if(!slotStack.isEmpty()){
                list.add(String.format(" %s%s: %s x %s", i,
                        i==selected? TextFormatting.DARK_GREEN:TextFormatting.GOLD,
                        slotStack.getCount(),
                        slotStack.getDisplayName()));
            }else{
                list.add(String.format(" %s%s: %s", i,
                        i==selected?TextFormatting.DARK_GREEN:TextFormatting.GOLD,
                        I18n.format("attribute.quiver.arrow.empty")));
            }
        }

    }

    @Override
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("display") && par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color");
    }

    @Override
    public int getColor(ItemStack par1ItemStack)
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

    @Override
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
            nbttagcompound.setTag("display", nbttagcompound1);
        }
        nbttagcompound1.setInteger("color", par2);
    }

    @Override
    public boolean sheatheOnBack(ItemStack item) {
        return false;
    }
}