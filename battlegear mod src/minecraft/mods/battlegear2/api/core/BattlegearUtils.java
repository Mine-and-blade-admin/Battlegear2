package mods.battlegear2.api.core;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.eventhandler.EventBus;
import mods.battlegear2.api.*;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.ISpecialBow;
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
import net.minecraft.item.*;
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
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.Closeable;
import java.io.IOException;

/**
 * Store commonly used method, mostly for the {@link EntityPlayer} {@link ItemStack}s management
 */
public class BattlegearUtils {

    /**
     * Event bus to which {@link mods.battlegear2.api.RenderPlayerEventChild} events are post to
     */
    public static final EventBus RENDER_BUS = new EventBus();
    /**
     * Method names that are not allowed in {@link Item} subclasses for common wielding
     */
    private static String[] itemBlackListMethodNames = {
            BattlegearTranslator.getMapedMethodName("func_77648_a", "onItemUse"),
            BattlegearTranslator.getMapedMethodName("onItemUseFirst", "onItemUseFirst"),//Added by Forge
            BattlegearTranslator.getMapedMethodName("func_77659_a", "onItemRightClick")
    };
    /**
     * Method arguments classes that are not allowed in {@link Item} subclasses for common wielding
     */
    private static Class[][] itemBlackListMethodParams = {
            new Class[]{ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class},
            new Class[]{ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class},
            new Class[]{ItemStack.class, World.class, EntityPlayer.class}
    };
    private static ItemStack prevNotWieldable;
    /**
     * The generic attack damage key for {@link ItemStack#getAttributeModifiers()}
     */
    private static String genericAttack = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();

    /**
     * Helper method to check if player can use {@link IShield}
     */
    public static boolean canBlockWithShield(EntityPlayer player){
        if(!(player.inventory instanceof InventoryPlayerBattle)){
            return false;
        }
        ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
        return offhand != null && offhand.getItem() instanceof IShield;
    }

    /**
     * Helper method to check if player is using {@link IShield}
     */
    public static boolean isBlockingWithShield(EntityPlayer player){
    	return ((IBattlePlayer)player).isBlockingWithShield();
    }

    /**
     * Helper method to check if player is in battlemode
     * @param player the target player entity
     * @return true if in battlemode
     */
    public static boolean isPlayerInBattlemode(EntityPlayer player) {
        return player.inventory instanceof InventoryPlayerBattle && ((InventoryPlayerBattle) player.inventory).isBattlemode();
    }

    /**
     * Helper method to set a player item, offset from the current one
     * @param player the target player entity
     * @param stack holding the item to set
     * @param offset from the current item
     */
    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack, int offset) {
        ((InventoryPlayerBattle) (player.inventory)).setInventorySlotContents(player.inventory.currentItem + offset, stack, false);
    }

    /*
    * Helper method to set the mainhand item
     */
    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack) {
        setPlayerCurrentItem(player, stack, 0);
    }

    /**
     * Helper method to set the offhand item, if battlemode is activated
     */
    public static void setPlayerOffhandItem(EntityPlayer player, ItemStack stack){
        if(isPlayerInBattlemode(player))
            setPlayerCurrentItem(player, stack, InventoryPlayerBattle.WEAPON_SETS);
    }

    /**
     * Defines a generic weapon
     * @param main the item to check
     * @return true if the item is a generic weapon
     */
    public static boolean isWeapon(ItemStack main) {
        if (main.getItem() instanceof IBattlegearWeapon)//Our generic weapon flag
            return true;
        else if(main.getMaxStackSize()==1 && main.getMaxDamage()>0 && !main.getHasSubtypes())//Usual values for tools, sword, and bow
            return true;
        else if(main == prevNotWieldable)//Prevent lag from check spam
            return false;
        else if(WeaponRegistry.isWeapon(main))//Registered as such
            return true;
        else if(!checkForRightClickFunction(main)){//Make sure there are no special functions for offhand/mainhand weapons
            WeaponRegistry.addDualWeapon(main);//register so as not to make that costly check again
            return true;
        }
        prevNotWieldable = main;
        return false;
    }

    /**
     * @deprecated see below
     */
    public static boolean isMainHand(ItemStack main, ItemStack off) {
        if(main == null)
            return true;
    	else if(main.getItem() instanceof IAllowItem)//An item using the API
            return ((IAllowItem) main.getItem()).allowOffhand(main, off);//defined by the item
        else if(main.getItem() instanceof IArrowContainer2)//A quiver
            return true;//anything ?
        else if(usagePriorAttack(main))//"Usable" item
            return off == null || !usagePriorAttack(off);//With empty hand or non "usable item"
        else if(isWeapon(main))//A generic weapon
            return main.getAttributeModifiers().containsKey(genericAttack) || WeaponRegistry.isMainHand(main);//With either generic attack, or registered
        return false;
    }

    /**
     * Defines a combination of left hand/right hand items that is valid to wield
     *
     * @param main Item to be wield in the right hand
     * @param off Item to be wield in the left hand
     * @param wielder The player trying to wield this combination of items
     * @return true if the right hand item allows left hand item
     */
    public static boolean isMainHand(ItemStack main, ItemStack off, EntityPlayer wielder) {
        if(main == null)
            return true;
        else if(main.getItem() instanceof IAllowItem)//An item using the API
            return ((IAllowItem) main.getItem()).allowOffhand(main, off);//defined by the item TODO pass through third parameter
        else if(main.getItem() instanceof IArrowContainer2)//A quiver
            return true;//anything ?
        else if(usagePriorAttack(main, wielder, false))//"Usable" item
            return off == null || !usagePriorAttack(off, wielder, true);//With empty hand or non "usable item"
        else if(isWeapon(main))//A generic weapon
            return main.getAttributeModifiers().containsKey(genericAttack) || WeaponRegistry.isMainHand(main);//With either generic attack, or registered
        return false;
    }

    /**
     * @deprecated see below
     */
    public static boolean isOffHand(ItemStack off) {
        if(off == null)
            return true;
    	else if(off.getItem() instanceof IOffhandDual)//An item using the API
            return ((IOffhandDual) off.getItem()).isOffhandHandDual(off);//defined by the item
        else if(off.getItem() instanceof IShield || off.getItem() instanceof IArrowContainer2 || usagePriorAttack(off))//Shield, Quiver, or "usable"
            return true;//always
        else if(isWeapon(off))//A generic weapon
            return off.getAttributeModifiers().containsKey(genericAttack) || WeaponRegistry.isOffHand(off);//with a generic attack or registered
        return false;
    }

    /**
     * Defines a item which can be wield in the left hand
     * @param off The item to be wield in left hand
     * @param wielder The player trying to wield this item
     * @return true if the item is allowed in left hand
     */
    public static boolean isOffHand(ItemStack off, EntityPlayer wielder) {
        if(off == null)
            return true;
        else if(off.getItem() instanceof IOffhandDual)//An item using the API
            return ((IOffhandDual) off.getItem()).isOffhandHandDual(off);//defined by the item
        else if(off.getItem() instanceof IOffhandWield)//An item using the API
            return ((IOffhandWield) off.getItem()).isOffhandWieldable(off, wielder);//defined by the item
        else if(off.getItem() instanceof IShield || off.getItem() instanceof IArrowContainer2 || usagePriorAttack(off, wielder, true))//Shield, Quiver, or "usable"
            return true;//always
        else if(isWeapon(off))//A generic weapon
            return off.getAttributeModifiers().containsKey(genericAttack) || WeaponRegistry.isOffHand(off);//with a generic attack or registered
        return false;
    }

    /**
     * @deprecated see below
     */
    public static boolean usagePriorAttack(ItemStack itemStack){
        if(itemStack.getItem() instanceof IUsableItem)
            return ((IUsableItem) itemStack.getItem()).isUsedOverAttack(itemStack);
        else {
            EnumAction useAction = itemStack.getItemUseAction();
            return useAction == EnumAction.bow || useAction == EnumAction.drink || useAction == EnumAction.eat || isCommonlyUsable(itemStack.getItem());
        }
    }

    /**
     * Defines a item which "use" (effect on right click) should have priority over its "attack" (effect on left click)
     * @param itemStack the item which will be "used", instead of attacking
     * @param wielder The player trying to use or attack with this item
     * @return true if such item prefer being "used"
     */
    public static boolean usagePriorAttack(ItemStack itemStack, EntityPlayer wielder, boolean off){
        if(itemStack.getItem() instanceof IUsableItem)//TODO pass through wielding player
            return ((IUsableItem) itemStack.getItem()).isUsedOverAttack(itemStack);
        else {
            EnumAction useAction = itemStack.getItemUseAction();
            return useAction == EnumAction.bow || useAction == EnumAction.drink || useAction == EnumAction.eat || isCommonlyUsable(itemStack.getItem()) || WeaponRegistry.useOverAttack(itemStack, off);
        }
    }

    /**
     * Defines items that are usually usable (the vanilla instances do, at least), and that battlemode can support
     * @param item the instance to consider for usability
     * @return true if it is commonly usable
     */
    public static boolean isCommonlyUsable(Item item){
        return isBow(item) || item instanceof ItemBlock || item instanceof ItemFlintAndSteel || item instanceof ItemFireball || item instanceof ItemBucket || item instanceof ItemSnowball || item instanceof ItemEnderPearl;
    }

    /**
     * Defines a bow
     * @param item the instance
     * @return true if it is considered a generic enough bow
     */
    public static boolean isBow(Item item){
        return item instanceof ItemBow || item instanceof ISpecialBow;
    }

    @Deprecated//See method below
    public static boolean checkForRightClickFunction(Item item, ItemStack stack){
        return checkForRightClickFunction(stack);
    }

    public static boolean checkForRightClickFunction(ItemStack stack) {
        if (stack.getItemUseAction() == EnumAction.block || stack.getItemUseAction() == EnumAction.none) {
            Class<?> c = stack.getItem().getClass();
            while (!(c.equals(Item.class) || c.equals(ItemTool.class) || c.equals(ItemSword.class))) {
                if(getBlackListedMethodIn(c)){
                    return true;
                }

                c = c.getSuperclass();
            }

            return false;
        }
        return true;
    }

    private static boolean getBlackListedMethodIn(Class<?> c){
        for(int i = 0; i < itemBlackListMethodNames.length; i++) {
            try {
                c.getDeclaredMethod(itemBlackListMethodNames[i], itemBlackListMethodParams[i]);
                return true;
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    /**
     * Reads a {@link ItemStack} from the InputStream
     */
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
     * Reads a compressed {@link NBTTagCompound} from the InputStream
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

    /**
     * Writes a {@link ItemStack} to the OutputStream
     * @param par1DataOutputStream the output stream
     * @param par0ItemStack to write
     * @throws IOException
     */
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

    /**
     * Writes a compressed {@link NBTTagCompound} to the output
     * @param par0NBTTagCompound
     * @param par1DataOutputStream
     * @throws IOException
     */
    protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, ByteArrayDataOutput par1DataOutputStream) throws IOException {
        if (par0NBTTagCompound == null) {
            par1DataOutputStream.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutputStream.writeShort((short) abyte.length);
            par1DataOutputStream.write(abyte);
        }
    }

    /**
     * Helper to recreate a player battle inventory from reflection with minimal effort
     * @param entityPlayer the target player
     * @return the new InventoryPlayerBattle instance
     */
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
        refreshAttributes(player, false);
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, par1Entity))){
            refreshAttributes(player, true);
            return;
        }
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, player, par1Entity)){
            refreshAttributes(player, true);
            return;
        }
        if (par1Entity.canAttackWithItem()) {
            if (!par1Entity.hitByEntity(player)){
                float f = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1 = 0.0F;

                if (par1Entity instanceof EntityLivingBase){
                    f1 = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase)par1Entity);
                    i += EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase)par1Entity);
                }
                if (player.isSprinting()){
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F){
                    boolean flag = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && par1Entity instanceof EntityLivingBase;

                    if (flag && f > 0.0F){
                        f *= 1.5F;
                    }
                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(player);

                    if (par1Entity instanceof EntityLivingBase && j > 0 && !par1Entity.isBurning()){
                        flag1 = true;
                        par1Entity.setFire(1);
                    }

                    boolean flag2 = par1Entity.attackEntityFrom(DamageSource.causePlayerDamage(player), f);

                    if (flag2){
                        if (i > 0){
                            par1Entity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (flag){
                            player.onCriticalHit(par1Entity);
                        }

                        if (f1 > 0.0F){
                            player.onEnchantmentCritical(par1Entity);
                        }

                        if (f >= 18.0F){
                            player.triggerAchievement(AchievementList.overkill);
                        }

                        player.setLastAttacker(par1Entity);

                        if (par1Entity instanceof EntityLivingBase){
                            EnchantmentHelper.func_151384_a((EntityLivingBase)par1Entity, player);
                        }

                        EnchantmentHelper.func_151385_b(player, par1Entity);
                        ItemStack itemstack = player.getCurrentEquippedItem();
                        Object object = par1Entity;

                        if (par1Entity instanceof EntityDragonPart){
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart)par1Entity).entityDragonObj;
                            if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase){
                                object = ientitymultipart;
                            }
                        }

                        if (itemstack != null && object instanceof EntityLivingBase){
                            itemstack.hitEntity((EntityLivingBase)object, player);
                            if (itemstack.stackSize <= 0){
                                player.destroyCurrentEquippedItem();
                            }
                        }

                        if (par1Entity instanceof EntityLivingBase){
                            player.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                            if (j > 0){
                                par1Entity.setFire(j * 4);
                            }
                        }

                        player.addExhaustion(0.3F);
                    }
                    else if (flag1){
                        par1Entity.extinguish();
                    }
                }
            }
        }
        refreshAttributes(player, true);
    }

    /**
     * Patch over {@link EntityPlayer#interactWith(Entity)}, adapted for the dual wielding
     * In battlemode, try to interact with {@link Entity#interactFirst(EntityPlayer)} in right hand, then left hand if no success
     * then try to interact with {@link ItemStack#interactWithEntity(EntityPlayer, EntityLivingBase)} in the same order
     * When necessary, hotswap the "current item" value to the offhand, then refresh the player attributes according to the newly selected item
     * @return true if any interaction happened, actually bypassing subsequent PlayerInteractEvent.Action.RIGHT_CLICK_AIR and PlayerControllerMP#sendUseItem on client side
     */
    public static boolean interactWith(EntityPlayer entityPlayer, Entity entity){
        final EntityInteractEvent event = new EntityInteractEvent(entityPlayer, entity);
        if (MinecraftForge.EVENT_BUS.post(event)) return false;
        ItemStack itemstack = entityPlayer.getCurrentEquippedItem();
        boolean offset = false;
        ItemStack copyStack = itemstack != null ? itemstack.copy() : null;
        boolean entityInteract = entity.interactFirst(entityPlayer);
        if(!entityInteract){//Entity interaction didn't happen
            if(BattlegearUtils.isPlayerInBattlemode(entityPlayer)){//We can try left hand
                offset = true;
                itemstack = refreshAttributes(entityPlayer, false);
                copyStack = itemstack != null ? itemstack.copy() : null;
                entityInteract = entity.interactFirst(entityPlayer);
            }
        }
        if(!entityInteract){//No interaction with the entity
            boolean itemInteract = false;
            if (itemstack != null && entity instanceof EntityLivingBase) {
                if (entityPlayer.capabilities.isCreativeMode) {
                    itemstack = copyStack;
                }
                itemInteract = itemstack.interactWithEntity(entityPlayer, (EntityLivingBase) entity);
                if(!itemInteract && !offset && BattlegearUtils.isPlayerInBattlemode(entityPlayer)){//No interaction with right hand item
                    offset = true;
                    itemstack = refreshAttributes(entityPlayer, false);
                    if(itemstack != null) {//Try left hand item
                        itemInteract = itemstack.interactWithEntity(entityPlayer, (EntityLivingBase) entity);
                    }
                }
                if(itemInteract){//Had item interaction in either hand
                    if (itemstack.stackSize <= 0 && !entityPlayer.capabilities.isCreativeMode){
                        entityPlayer.destroyCurrentEquippedItem();
                    }
                }
            }
            if(offset){//Hand was swapped, unswap
                refreshAttributes(entityPlayer, true);
            }
            if(!itemInteract && BattlegearUtils.isPlayerInBattlemode(entityPlayer)){
                ItemStack offhandItem = ((InventoryPlayerBattle)event.entityPlayer.inventory).getCurrentOffhandWeapon();
                PlayerEventChild.OffhandAttackEvent offAttackEvent = new PlayerEventChild.OffhandAttackEvent(event, offhandItem);
                if(!MinecraftForge.EVENT_BUS.post(offAttackEvent)){
                    if (offAttackEvent.swingOffhand){
                        sendOffSwingEvent(event, offAttackEvent.offHand);
                    }
                    if (offAttackEvent.shouldAttack) {
                        ((IBattlePlayer) event.entityPlayer).attackTargetEntityWithCurrentOffItem(offAttackEvent.getTarget());
                    }
                    if(offAttackEvent.cancelParent){
                        return true;
                    }
                }
            }
            return itemInteract;
        }else{//Had interaction with the entity
            if (itemstack != null && itemstack == entityPlayer.getCurrentEquippedItem()){//The interaction kept the stack identity
                if (!entityPlayer.capabilities.isCreativeMode){
                    if (itemstack.stackSize <= 0)
                        entityPlayer.destroyCurrentEquippedItem();
                }else if (itemstack.stackSize < copyStack.stackSize){
                    itemstack.stackSize = copyStack.stackSize;
                }
            }
            if(offset){//Hand was swapped, unswap
                refreshAttributes(entityPlayer, true);
            }
            return true;
        }
    }

    /**
     * Helper to send {@link PlayerEventChild.OffhandSwingEvent}
     * @param event the "parent" event
     * @param offhandItem the item stack held in offhand
     */
    public static void sendOffSwingEvent(PlayerEvent event, ItemStack offhandItem) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerEventChild.OffhandSwingEvent(event, offhandItem))) {
            ((IBattlePlayer) event.entityPlayer).swingOffItem();
        }
    }

    /**
     * Refresh the player attribute map by swapping the wielding hand
     * Warning: does NOT check if battlemode!
     *
     * @param fromOffhand if true, sets from left hand to right hand, else, the opposite
     * @return the currently equipped stack, after swapping
     *
     */
    public static ItemStack refreshAttributes(EntityPlayer entityPlayer, boolean fromOffhand){
        final ItemStack oldItem = entityPlayer.getCurrentEquippedItem();
        if(fromOffhand){
            entityPlayer.inventory.currentItem -= InventoryPlayerBattle.WEAPON_SETS;
        }else{
            entityPlayer.inventory.currentItem += InventoryPlayerBattle.WEAPON_SETS;
        }
        final ItemStack newStack = entityPlayer.getCurrentEquippedItem();
        refreshAttributes(entityPlayer.getAttributeMap(), oldItem, newStack);
        return newStack;
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

    /**
     * Helper to close a stream fail-safely by printing the error stack trace
     * @param c the stream to close
     */
    public static void closeStream(Closeable c){
        try{
            if(c != null){
                c.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Patch over the PlayerUseItemEvent.Finish in EntityPlayer#onItemUseFinish() to pass the previous stacksize
     * @param entityPlayer the {@link EntityPlayer} who finished using the itemInUse
     * @param itemInUse the {@link ItemStack} which finished being used
     * @param itemInUseCount the {@link EntityPlayer} item use count
     * @param previousStackSize the itemInUse {@link ItemStack#stackSize} before {@link ItemStack#onFoodEaten(World, EntityPlayer)}
     * @param result from itemInUse#onFoodEaten(entityPlayer.worldObj, entityPlayer)
     * @return the final resulting {@link ItemStack}
     */
    public static ItemStack beforeFinishUseEvent(EntityPlayer entityPlayer, ItemStack itemInUse, int itemInUseCount, ItemStack result, int previousStackSize) {
        result = ForgeEventFactory.onItemUseFinish(entityPlayer, itemInUse, itemInUseCount, result);
        if(isPlayerInBattlemode(entityPlayer)) {
            if (result != itemInUse || (result != null && result.stackSize != previousStackSize)) {
                //Compare with either hands content
                if (itemInUse == entityPlayer.getCurrentEquippedItem()) {
                    if (result != null && result.stackSize == 0) {
                        setPlayerCurrentItem(entityPlayer, null);
                    } else {
                        setPlayerCurrentItem(entityPlayer, result);
                    }
                } else if (itemInUse == ((InventoryPlayerBattle) entityPlayer.inventory).getCurrentOffhandWeapon()) {
                    if (result != null && result.stackSize == 0) {
                        setPlayerOffhandItem(entityPlayer, null);
                    } else {
                        setPlayerOffhandItem(entityPlayer, result);
                    }
                }
            }
            //Reset stuff so that vanilla doesn't do anything
            entityPlayer.clearItemInUse();
            return null;
        }
        return result;
    }

    /**
     * Patch in EntityPlayer#onUpdate() to support hotswap of itemInUse
     * @param entityPlayer
     * @param itemInUse
     * @return
     */
    public static ItemStack getCurrentItemOnUpdate(EntityPlayer entityPlayer, ItemStack itemInUse) {
        if(isPlayerInBattlemode(entityPlayer)) {
            ItemStack itemStack = ((InventoryPlayerBattle) entityPlayer.inventory).getCurrentOffhandWeapon();
            if (itemInUse == itemStack) {
                return itemStack;
            }
        }
        return entityPlayer.getCurrentEquippedItem();
    }

    /**
     * Patch in ItemStack#damageItem() to fix bow stack weird depletion
     *
     * @param itemStack which item is instance of ItemBow, and size is 0
     * @param entityPlayer who has damaged and depleted the stack
     */
    public static void onBowStackDepleted(EntityPlayer entityPlayer, ItemStack itemStack){
        if(itemStack == entityPlayer.getCurrentEquippedItem()){
            entityPlayer.destroyCurrentEquippedItem();
        }else{
            ItemStack orig = ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon();
            if(orig == itemStack) {
                setPlayerOffhandItem(entityPlayer, null);
                ForgeEventFactory.onPlayerDestroyItem(entityPlayer, orig);
            }
        }
    }
}
