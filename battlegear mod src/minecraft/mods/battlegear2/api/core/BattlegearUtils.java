package mods.battlegear2.api.core;

import java.io.Closeable;
import java.io.IOException;

import cpw.mods.fml.common.eventhandler.EventBus;
import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandDual;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class BattlegearUtils {

    /**
     * Event bus to which {@link mods.battlegear2.api.RenderPlayerEventChild} events are post to
     */
    public static final EventBus RENDER_BUS = new EventBus();

    private static String[] itemBlackListMethodNames = {
            BattlegearTranslator.getMapedMethodName("Item", "func_77648_a", "onItemUse"),
            BattlegearTranslator.getMapedMethodName("Item", "func_77659_a", "onItemRightClick")
    };
    private static Class[][] itemBlackListMethodParams = {
                new Class[]{ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class},
                new Class[]{ItemStack.class, World.class, EntityPlayer.class}
    };

    public static boolean isBlockingWithShield(EntityPlayer player){
        //TODO: Use this ?
    	if(!player.isSneaking()){
    		return false;
    	}
    	ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
        return offhand != null && offhand.getItem() instanceof IShield;
    }

    public static boolean isPlayerInBattlemode(EntityPlayer player) {
        return ((InventoryPlayerBattle) player.inventory).isBattlemode();
    }

    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack, int offset) {
        ((InventoryPlayerBattle) (player.inventory)).setInventorySlotContents(player.inventory.currentItem + offset, stack, false);
    }

    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack) {
        setPlayerCurrentItem(player, stack, 0);
    }

    public static boolean isWeapon(ItemStack main) {
        if (main.getItem() instanceof IBattlegearWeapon)
            return true;
        else if(WeaponRegistry.isWeapon(main))
            return true;
        else{
            boolean valid = main.getMaxStackSize()==1 && main.getMaxDamage()>0 && !main.getHasSubtypes();
            if(valid){
                valid = main.getItem() instanceof ItemSword ||
                        main.getItem() instanceof ItemBow ||
                        main.getItem() instanceof ItemTool;
            }
            return valid;
        }
    }

    public static boolean isMainHand(ItemStack main, ItemStack off) {
    	if(main.getItem() instanceof IAllowItem)
            return ((IAllowItem) main.getItem()).allowOffhand(main, off);
        else if(WeaponRegistry.isMainHand(main))
            return true;
        else{
            if(isWeapon(main)){
                //make sure there are no special functions for offhand/mainhand weapons
                boolean rightClick = checkForRightClickFunction(main.getItem(), main);
                boolean offhand = !(main.getItem() instanceof ItemTool || main.getItem() instanceof ItemBow) && !rightClick;
                boolean mainhand = !(main.getItem() instanceof ItemBow) && !rightClick;
                if(mainhand){
                    if(offhand)
                        WeaponRegistry.addDualWeapon(main);
                    else
                        WeaponRegistry.addTwoHanded(main);
                    return true;
                }
                if(offhand){
                    WeaponRegistry.addOffhandWeapon(main);
                }
            }
            return false;
        }
    }

    public static boolean isOffHand(ItemStack off) {
    	if(off.getItem() instanceof IOffhandDual)
            return ((IOffhandDual) off.getItem()).isOffhandHandDual(off);
        else if(off.getItem() instanceof IShield || off.getItem() instanceof ItemBlock)
            return true;
        else if(WeaponRegistry.isOffHand(off))
            return true;
        else{
            if(isWeapon(off)){
                //make sure there are no special functions for offhand/mainhand weapons
                boolean rightClick = checkForRightClickFunction(off.getItem(), off);
                boolean offhand = !(off.getItem() instanceof ItemTool || off.getItem() instanceof ItemBow) && !rightClick;
                boolean mainhand = !(off.getItem() instanceof ItemBow) && !rightClick;
                if(offhand){
                    if(mainhand)
                        WeaponRegistry.addDualWeapon(off);
                    else
                        WeaponRegistry.addOffhandWeapon(off);
                    return true;
                }
                if(mainhand){
                    WeaponRegistry.addTwoHanded(off);
                }
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean checkForRightClickFunction(Item item, ItemStack stack) {
        try {
            if (item.getItemUseAction(stack) == EnumAction.block || item.getItemUseAction(stack) == EnumAction.none) {

                Class c = item.getClass();
                while (!(c.equals(Item.class) || c.equals(ItemTool.class) || c.equals(ItemSword.class))) {
                    try {
                        try {
                            c.getDeclaredMethod(itemBlackListMethodNames[0], itemBlackListMethodParams[0]);
                            return true;
                        } catch (NoSuchMethodException ignored) {
                        }

                        try {
                            c.getDeclaredMethod(itemBlackListMethodNames[1], itemBlackListMethodParams[1]);
                            return true;
                        } catch (NoSuchMethodException ignored) {
                        }
                    } catch (NoClassDefFoundError ignored) {

                    }

                    c = c.getSuperclass();
                }

                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            return true;
        }
    }


    public static ItemStack readItemStack(ByteArrayDataInput par0DataInputStream) throws IOException {
        ItemStack itemstack = null;
        int short1 = par0DataInputStream.readInt();

        if (short1 >= 0) {
            byte b0 = par0DataInputStream.readByte();
            short short2 = par0DataInputStream.readShort();
            itemstack = new ItemStack(Item.getItemById(short1), b0, short2);
            itemstack.stackTagCompound = readNBTTagCompound(par0DataInputStream);
        }

        return itemstack;
    }

    /**
     * Reads a compressed NBTTagCompound from the InputStream
     */
    public static NBTTagCompound readNBTTagCompound(ByteArrayDataInput par0DataInputStream) throws IOException {
        short short1 = par0DataInputStream.readShort();

        if (short1 < 0) {
            return null;
        } else {
            byte[] abyte = new byte[short1];
            par0DataInputStream.readFully(abyte);

            return CompressedStreamTools.func_152457_a(abyte, NBTSizeTracker.field_152451_a);
        }
    }

    public static void writeItemStack(ByteArrayDataOutput par1DataOutputStream, ItemStack par0ItemStack) throws IOException {

        if (par0ItemStack == null) {
            par1DataOutputStream.writeShort(-1);
        } else {
            par1DataOutputStream.writeInt(Item.getIdFromItem(par0ItemStack.getItem()));
            par1DataOutputStream.writeByte(par0ItemStack.stackSize);
            par1DataOutputStream.writeShort(par0ItemStack.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (par0ItemStack.getItem().isDamageable() || par0ItemStack.getItem().getShareTag()) {
                nbttagcompound = par0ItemStack.stackTagCompound;
            }

            writeNBTTagCompound(nbttagcompound, par1DataOutputStream);
        }

    }

    protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, ByteArrayDataOutput par1DataOutputStream) throws IOException {
        if (par0NBTTagCompound == null) {
            par1DataOutputStream.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutputStream.writeShort((short) abyte.length);
            par1DataOutputStream.write(abyte);
        }
    }

    public static InventoryPlayer replaceInventory(EntityPlayer entityPlayer) {
        return new InventoryPlayerBattle(entityPlayer);
    }

    /**
     * Helper method to request a new slot within player inventory
     * All data and hooks are handled by {@link InventoryPlayerBattle}
     * The slot content display is to be done by the modder
     * @param entityPlayer the player whose inventory will be expanded
     * @param type the type of inventory which will be expanded
     * @return the new slot index, or Integer.MIN_VALUE if it is not possible to expand further
     */
    public static int requestInventorySpace(EntityPlayer entityPlayer, InventorySlotType type){
        return ((InventoryPlayerBattle)entityPlayer.inventory).requestNewSlot(type);
    }

    /**
     * Basically, a copy of {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)}, adapted for the offhand
     * Hotswap the "current item" value to the offhand, then refresh the player attributes according to the newly selected item
     * Reset everything back if the attack is cancelled by {@link AttackEntityEvent} or {@link Item#onLeftClickEntity(ItemStack, EntityPlayer, Entity)}
     * Used as a hook by {@link IBattlePlayer}
     * @param player the attacker
     * @param par1Entity the attacked
     */
    public static void attackTargetEntityWithCurrentOffItem(EntityPlayer player, Entity par1Entity){
        final ItemStack oldItem = player.getCurrentEquippedItem();
        player.inventory.currentItem += InventoryPlayerBattle.WEAPON_SETS;
        ItemStack stack = player.getCurrentEquippedItem();
        refreshAttributes(player.getAttributeMap(), oldItem, stack);
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, par1Entity)))
        {
            refreshAttributes(player.getAttributeMap(), player.getCurrentEquippedItem(), oldItem);
            player.inventory.currentItem -= InventoryPlayerBattle.WEAPON_SETS;
            return;
        }
        stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, player, par1Entity))
        {
            refreshAttributes(player.getAttributeMap(), player.getCurrentEquippedItem(), oldItem);
            player.inventory.currentItem -= InventoryPlayerBattle.WEAPON_SETS;
            return;
        }
        if (par1Entity.canAttackWithItem())
        {
            if (!par1Entity.hitByEntity(player))
            {
                float f = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1 = 0.0F;

                if (par1Entity instanceof EntityLivingBase)
                {
                    f1 = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase)par1Entity);
                    i += EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase)par1Entity);
                }

                if (player.isSprinting())
                {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F)
                {
                    boolean flag = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && par1Entity instanceof EntityLivingBase;

                    if (flag && f > 0.0F)
                    {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(player);

                    if (par1Entity instanceof EntityLivingBase && j > 0 && !par1Entity.isBurning())
                    {
                        flag1 = true;
                        par1Entity.setFire(1);
                    }

                    boolean flag2 = par1Entity.attackEntityFrom(DamageSource.causePlayerDamage(player), f);

                    if (flag2)
                    {
                        if (i > 0)
                        {
                            par1Entity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (flag)
                        {
                            player.onCriticalHit(par1Entity);
                        }

                        if (f1 > 0.0F)
                        {
                            player.onEnchantmentCritical(par1Entity);
                        }

                        if (f >= 18.0F)
                        {
                            player.triggerAchievement(AchievementList.overkill);
                        }

                        player.setLastAttacker(par1Entity);

                        if (par1Entity instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)par1Entity, player);
                        }

                        EnchantmentHelper.func_151385_b(player, par1Entity);
                        ItemStack itemstack = player.getCurrentEquippedItem();
                        Object object = par1Entity;

                        if (par1Entity instanceof EntityDragonPart)
                        {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart)par1Entity).entityDragonObj;

                            if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase)
                            {
                                object = ientitymultipart;
                            }
                        }

                        if (itemstack != null && object instanceof EntityLivingBase)
                        {
                            itemstack.hitEntity((EntityLivingBase)object, player);

                            if (itemstack.stackSize <= 0)
                            {
                                player.destroyCurrentEquippedItem();
                            }
                        }

                        if (par1Entity instanceof EntityLivingBase)
                        {
                            player.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                            if (j > 0)
                            {
                                par1Entity.setFire(j * 4);
                            }
                        }

                        player.addExhaustion(0.3F);
                    }
                    else if (flag1)
                    {
                        par1Entity.extinguish();
                    }
                }
            }
        }
        refreshAttributes(player.getAttributeMap(), player.getCurrentEquippedItem(), oldItem);
        player.inventory.currentItem -= InventoryPlayerBattle.WEAPON_SETS;
    }

    /**
     * Refresh the attribute map by removing from the old item and applying the current item
     * @param attributeMap the map to refresh
     * @param oldItem the old item whose attributes will be removed
     * @param currentItem the current item whose attributes will be applied
     */
    public static void refreshAttributes(BaseAttributeMap attributeMap, ItemStack oldItem, ItemStack currentItem) {
        if(oldItem!=null)
            attributeMap.removeAttributeModifiers(oldItem.getAttributeModifiers());
        if(currentItem!=null)
            attributeMap.applyAttributeModifiers(currentItem.getAttributeModifiers());
    }

    public static void closeStream(Closeable c){
        try{
            if(c != null){
                c.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
