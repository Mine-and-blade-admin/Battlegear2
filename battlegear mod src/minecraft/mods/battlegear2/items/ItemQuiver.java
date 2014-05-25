package mods.battlegear2.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class ItemQuiver extends Item implements IArrowContainer2, IDyable {
    public IIcon quiverDetails;
    public IIcon quiverArrows;

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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    	for(int i = 0; i<getSlotCount(stack);i++){
            ItemStack temp = getStackInSlot(stack, i);
            if(temp!=null){
                EntityItem entityitem = ForgeHooks.onPlayerTossEvent(player, temp, true);
                if(entityitem!=null) {
                    entityitem.delayBeforeCanPickup = 0;
                    entityitem.func_145797_a(player.getCommandSenderName());
                }
                setStackInSlot(stack, i, null);
            }
    	}
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
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
            compound.setTag("Slot"+slot, newSlotCompound);
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
        else
            return QuiverArrowRegistry.getArrowType(selected, world, player, charge);
    }

    @Override
    public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {
        if(!player.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) == 0){
            int selectedSlot = getSelectedSlot(stack);
            ItemStack arrowStack = getStackInSlot(stack, selectedSlot);
            arrowStack.stackSize --;
            if(arrowStack.stackSize <= 0){
                ForgeEventFactory.onPlayerDestroyItem(player, arrowStack);
                arrowStack = null;
            }
            setStackInSlot(stack, selectedSlot, arrowStack);
        }
    }

    @Override
    public void onPreArrowFired(PlayerEventChild.QuiverArrowEvent.Firing arrowEvent) {
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
        if(!bow.hasTagCompound()){
            bow.stackTagCompound = new NBTTagCompound();
        }
        bow.stackTagCompound.setTag("Battlegear2-LoadedArrow", tags);
    }

    @Override
    public boolean isCraftableWithArrows(ItemStack stack, ItemStack arrows) {
        return QuiverArrowRegistry.isKnownArrow(arrows);
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
                    if(newStack.getItem() == slotStack.getItem() && newStack.getItemDamage() == slotStack.getItemDamage()){
                        int newSize = Math.min(64, slotStack.stackSize + left_over);
                        left_over = left_over - (newSize - slotStack.stackSize);
                        slotStack.stackSize = newSize;
                        setStackInSlot(container, i, slotStack);
                    }
                }
            }
            if(left_over > 0){
                newStack.stackSize = left_over;
                return newStack;
            }
        }
        return null;
    }

    @Override
    public boolean renderDefaultQuiverModel(ItemStack container) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
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


    @Override
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("display") && par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color");
    }

    @Override
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
}
