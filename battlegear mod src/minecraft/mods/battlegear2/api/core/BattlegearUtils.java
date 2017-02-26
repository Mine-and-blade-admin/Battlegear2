package mods.battlegear2.api.core;

import mods.battlegear2.api.*;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.ISpecialBow;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

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
    private static String[] itemBlackListMethodNames = {//TODO:check srg names
            BattlegearTranslator.getMapedMethodName("func_180614_a", "onItemUse"),
            BattlegearTranslator.getMapedMethodName("onItemUseFirst", "onItemUseFirst"),//Added by Forge
            BattlegearTranslator.getMapedMethodName("func_77659_a", "onItemRightClick")
    };
    /**
     * Method arguments classes that are not allowed in {@link Item} subclasses for common wielding
     */
    private static Class[][] itemBlackListMethodParams = {
            new Class[]{EntityPlayer.class, World.class, BlockPos.class, EnumHand.class, EnumFacing.class, float.class, float.class, float.class},
            new Class[]{EntityPlayer.class, World.class, BlockPos.class, EnumFacing.class, float.class, float.class, float.class, EnumHand.class},
            new Class[]{World.class, EntityPlayer.class, EnumHand.class}
    };
    private static ItemStack prevNotWieldable;
    /**
     * The generic attack damage key for {@link ItemStack#getAttributeModifiers(EntityEquipmentSlot)}
     */
    private static String genericAttack = SharedMonsterAttributes.ATTACK_DAMAGE.getName();

    /**
     * Helper method to check if player can use {@link IShield}
     */
    public static boolean canBlockWithShield(EntityPlayer player){
        return player.getHeldItemOffhand().getItem() instanceof IShield;
    }

    /**
     * Helper method to check if player is using {@link IShield}
     */
    public static boolean isBlockingWithShield(EntityPlayer player){
    	return player instanceof IBattlePlayer && ((IBattlePlayer)player).isBlockingWithShield();
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

    /**
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
        else
            player.inventory.offHandInventory.set(0, stack);
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
        WeaponRegistry.addDualUsable(main);
        prevNotWieldable = main;
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
        if(main.isEmpty())
            return true;
        if(main.getItem() instanceof IWield && !((IWield) main.getItem()).getWieldStyle(main, wielder).isMainhand())
            return false;
        if(main.getItem() instanceof IAllowItem)//An item using the API
            return ((IAllowItem) main.getItem()).allowOffhand(main, off, wielder);//defined by the item
        else if(main.getItem() instanceof IShield)//A shield
            return false;
        else if(main.getItem() instanceof IArrowContainer2)//A quiver
            return true;//anything ?
        else if(usagePriorAttack(main, wielder, false))//"Usable" item
            return off.isEmpty() || !usagePriorAttack(off, wielder, true);//With empty hand or non "usable item"
        else if(isWeapon(main))//A generic weapon
            return main.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).containsKey(genericAttack) || WeaponRegistry.isMainHand(main);//With either generic attack, or registered
        return false;
    }

    /**
     * Defines a item which can be wield in the left hand
     * @param off The item to be wield in left hand
     * @param main Item to be wield in the right hand
     * @param wielder The player trying to wield this item
     * @return true if the item is allowed in left hand
     */
    public static boolean isOffHand(ItemStack off, ItemStack main, EntityPlayer wielder) {
        if(off.isEmpty())
            return true;
        if(off.getItem() instanceof IWield && !((IWield) off.getItem()).getWieldStyle(off, wielder).isOffhand())
            return false;
        if(off.getItem() instanceof IAllowItem)//An item using the API
            return ((IAllowItem) off.getItem()).allowOffhand(off, main, wielder);//defined by the item
        else if(off.getItem() instanceof IShield || off.getItem() instanceof IArrowContainer2 || usagePriorAttack(off, wielder, true))//Shield, Quiver, or "usable"
            return true;//always
        else if(isWeapon(off))//A generic weapon
            return off.getAttributeModifiers(EntityEquipmentSlot.OFFHAND).containsKey(genericAttack) || WeaponRegistry.isOffHand(off);//with a generic attack or registered
        return false;
    }

    /**
     * Defines a item which "use" (effect on right click) should have priority over its "attack" (effect on left click)
     * @param itemStack the item which will be "used", instead of attacking
     * @param wielder The player trying to use or attack with this item
     * @return true if such item prefer being "used"
     */
    public static boolean usagePriorAttack(ItemStack itemStack, EntityPlayer wielder, boolean off){
        if(itemStack.getItem() instanceof IUsableItem)
            return ((IUsableItem) itemStack.getItem()).isUsedOverAttack(itemStack, wielder);
        else {
            EnumAction useAction = itemStack.getItemUseAction();
            return useAction == EnumAction.BOW || useAction == EnumAction.DRINK || useAction == EnumAction.EAT || isCommonlyUsable(itemStack.getItem()) || WeaponRegistry.useOverAttack(itemStack, off);
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

    public static boolean checkForRightClickFunction(ItemStack stack) {
        if (stack.getItemUseAction() == EnumAction.BLOCK || stack.getItemUseAction() == EnumAction.NONE) {
            Class<?> c = stack.getItem().getClass();
            while (!c.equals(Item.class)) {
                if(c.equals(ItemTool.class) || c.equals(ItemSword.class)){
                    return false;
                }
                if(getBlackListedMethodIn(c)){
                    return true;
                }
                c = c.getSuperclass();
            }

            return stack.getMaxItemUseDuration() > 0;
        }
        return true;
    }

    private static boolean getBlackListedMethodIn(Class<?> c){
        for(int i = 0; i < itemBlackListMethodNames.length; i++) {
            try {
                c.getDeclaredMethod(itemBlackListMethodNames[i], itemBlackListMethodParams[i]);
                return true;
            } catch(NoSuchMethodException noMethod){
                continue;
            } catch(Throwable ignored) {
                return true;
            }
        }
        return false;
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
     * Basically, a copy of {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)}, adapted for the offhand
     * Hotswap the "current item" value to the offhand, then refresh the player attributes according to the newly selected item
     * Reset everything back if the attack is cancelled by {@link AttackEntityEvent} or {@link Item#onLeftClickEntity(ItemStack, EntityPlayer, Entity)}
     * Used as a hook by {@link IBattlePlayer}
     * @param player the attacker
     * @param target the attacked
     */
    public static void attackTargetEntityWithCurrentOffItem(EntityPlayer player, Entity target){
        refreshAttributes(player);
        if (!ForgeHooks.onPlayerAttackTarget(player, target)){
            refreshAttributes(player);
            return;
        }
        if (target.canBeAttackedWithItem() && !target.hitByEntity(player)){
            float f = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float f1;

            if (target instanceof EntityLivingBase){
                f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), ((EntityLivingBase) target).getCreatureAttribute());
            }else{
                f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
            }
            //TODO Check cooldown system
            float f2 = player.getCooledAttackStrength(0.5F);
            f = f * (0.2F + f2 * f2 * 0.8F);
            f1 = f1 * f2;
            player.resetCooldown();

            if (f > 0.0F || f1 > 0.0F){
                boolean flag = f2 > 0.9F;
                //Knockback stuff
                boolean flag1 = false;
                int i = EnchantmentHelper.getKnockbackModifier(player);
                if (player.isSprinting() && flag){
                    player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                    ++i;//Knockback on sprint+weapon charged
                    flag1 = true;
                }
                boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && target instanceof EntityLivingBase && !player.isSprinting();
                if (flag2){
                    f *= 1.5F;//Add 'jump-slash' force
                }
                f += f1;
                //Conditions for sword 'swipe' special move
                boolean flag3 = false;
                double d0 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
                if (flag && !flag2 && !flag1 && player.onGround && d0 < (double)player.getAIMoveSpeed()) {
                    ItemStack itemstack = player.getHeldItemMainhand();
                    if (itemstack.getItem() instanceof ItemSword)  {
                        flag3 = true;
                    }
                }
                //Fire aspect, record target previous health
                float f4 = 0.0F;
                boolean flag4 = false;
                int j = EnchantmentHelper.getFireAspectModifier(player);
                if (target instanceof EntityLivingBase) {
                    f4 = ((EntityLivingBase) target).getHealth();
                    if (j > 0 && !target.isBurning()) {
                        flag4 = true;
                        target.setFire(1);
                    }
                }

                double d1 = target.motionX;
                double d2 = target.motionY;
                double d3 = target.motionZ;

                if (target.attackEntityFrom(DamageSource.causePlayerDamage(player), f)){
                    if (i > 0){//Knockback effect
                        if(target instanceof EntityLivingBase){
                            ((EntityLivingBase) target).knockBack(player, i * 0.5F, MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F), -MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F));
                        }else
                            target.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                        player.motionX *= 0.6D;
                        player.motionZ *= 0.6D;
                        player.setSprinting(false);
                    }
                    if(flag3){//Sword swipe effect
                        for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().expand(1.0D, 0.25D, 1.0D)))
                        {
                            if (entitylivingbase != player && entitylivingbase != target && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSqToEntity(entitylivingbase) < 9.0D)
                            {
                                entitylivingbase.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F), -MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F));
                                entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
                            }
                        }

                        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
                        player.spawnSweepParticles();
                    }
                    if (target instanceof EntityPlayerMP && target.velocityChanged)
                    {
                        ((EntityPlayerMP)target).connection.sendPacket(new SPacketEntityVelocity(target));
                        target.velocityChanged = false;
                        target.motionX = d1;
                        target.motionY = d2;
                        target.motionZ = d3;
                    }

                    if (flag2){
                        player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 1.0F);
                        player.onCriticalHit(target);
                    }else if(!flag3){
                        if (flag)
                            player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
                        else
                            player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, 1.0F, 1.0F);
                    }

                    if (f1 > 0.0F){
                        player.onEnchantmentCritical(target);
                    }
                    if (f >= 18.0F){
                        player.addStat(AchievementList.OVERKILL);
                    }

                    player.setLastAttacker(target);

                    if (target instanceof EntityLivingBase){
                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase) target, player);//Call #onUserHurt for each enchantment from target inventory
                    }

                    EnchantmentHelper.applyArthropodEnchantments(player, target);//Call #onEntityDamaged for each enchantment from player inventory
                    Entity entity = target;
                    if (target instanceof EntityDragonPart) {
                        IEntityMultiPart ientitymultipart = ((EntityDragonPart) target).entityDragonObj;
                        if (ientitymultipart instanceof EntityLivingBase) {
                            entity = (EntityLivingBase) ientitymultipart;
                        }
                    }

                    if (entity instanceof EntityLivingBase){
                        ItemStack itemstack = player.getHeldItemMainhand();
                        if(!itemstack.isEmpty()) {
                            ItemStack copy = itemstack.copy();
                            itemstack.hitEntity((EntityLivingBase) entity, player);
                            if (itemstack.isEmpty()) {
                                player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                                ForgeEventFactory.onPlayerDestroyItem(player, copy, EnumHand.MAIN_HAND);
                            }
                        }
                    }

                    if (target instanceof EntityLivingBase){
                        float damage = f4 - ((EntityLivingBase)target).getHealth();
                        player.addStat(StatList.DAMAGE_DEALT, Math.round(damage * 10.0F));
                        if (j > 0){
                            target.setFire(j * 4);
                        }
                        if(player.world instanceof WorldServer && damage > 2){
                            int k = (int)(damage * 0.5);
                            ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + (double)(target.height * 0.5F), target.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                        }
                    }

                    player.addExhaustion(0.3F);
                }
                else {
                    player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0F, 1.0F);

                    if (flag4)
                        target.extinguish();
                }
            }
        }
        refreshAttributes(player);
    }
    /**
     * Patch over {@link EntityPlayer#getItemStackFromSlot(EntityEquipmentSlot)}, adapted for the dual wielding
     */
    public static ItemStack getItemStackFromSlot(EntityPlayer player, EntityEquipmentSlot slot){
        if(slot == EntityEquipmentSlot.MAINHAND)
            return player.inventory.getCurrentItem();
        else if(slot == EntityEquipmentSlot.OFFHAND)
            return ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
        else if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
            return player.inventory.armorInventory.get(slot.getIndex());
        return ItemStack.EMPTY;
    }

    /**
     * Patch over {@link EntityPlayer#setItemStackToSlot(EntityEquipmentSlot, ItemStack)}, adapted for the dual wielding
     */
    public static boolean setItemStackToSlot(EntityPlayer player, EntityEquipmentSlot slot, ItemStack stack){
        if(slot == EntityEquipmentSlot.MAINHAND){
            setPlayerCurrentItem(player, stack);
            return true;
        }else if(slot == EntityEquipmentSlot.OFFHAND){
            setPlayerOffhandItem(player, stack);
            return true;
        }else if(slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR){
            player.inventory.armorInventory.set(slot.getIndex(), stack);
            return true;
        }
        return false;
    }

    /**TODO
     * Patch over {@link EntityPlayer#interactOn(Entity,EnumHand)}, adapted for the dual wielding
     * In battlemode, try to interact with {@link Entity#processInitialInteract(EntityPlayer, EnumHand)} in right hand, then left hand if no success
     * then try to interact with {@link ItemStack#interactWithEntity(EntityPlayer, EntityLivingBase, EnumHand)} in the same order
     * When necessary, hotswap the "current item" value to the offhand, then refresh the player attributes according to the newly selected item
     * @return SUCCESS if any interaction happened, actually bypassing subsequent PlayerInteractEvent.Action.RIGHT_CLICK_AIR and PlayerControllerMP#sendUseItem on client side
     */
    public static EnumActionResult interactWith(EntityPlayer entityPlayer, Entity entity, EnumHand hand){
        if (entityPlayer.isSpectator()){
            if (entity instanceof IInventory){
                entityPlayer.displayGUIChest((IInventory)entity);
            }
            return EnumActionResult.PASS;
        }
        final PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(entityPlayer, hand, entity);
        if (MinecraftForge.EVENT_BUS.post(event)) return EnumActionResult.PASS;
        ItemStack itemstack = entityPlayer.getHeldItem(hand);
        ItemStack copyStack = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
        if(entity.processInitialInteract(entityPlayer, hand)) {//Had interaction with the entity
            if (entityPlayer.capabilities.isCreativeMode && itemstack == entityPlayer.getHeldItem(hand) && itemstack.getCount() < copyStack.getCount()) {//The interaction kept the stack identity
                itemstack.setCount(copyStack.getCount());
            }else if (!entityPlayer.capabilities.isCreativeMode && itemstack.isEmpty()){//The interaction emptied the stack
                ForgeEventFactory.onPlayerDestroyItem(entityPlayer, copyStack, hand);
            }
            return EnumActionResult.SUCCESS;
        }
        else{//No interaction with the entity
            if (entity instanceof EntityLivingBase) {
                if (entityPlayer.capabilities.isCreativeMode) {
                    itemstack = copyStack;
                }
                if(!itemstack.isEmpty() && itemstack.interactWithEntity(entityPlayer, (EntityLivingBase) entity, hand)){//Had item interaction in either hand
                    if (itemstack.isEmpty() && !entityPlayer.capabilities.isCreativeMode){
                        ForgeEventFactory.onPlayerDestroyItem(entityPlayer, copyStack, hand);
                        entityPlayer.setHeldItem(hand, ItemStack.EMPTY);
                    }
                    return EnumActionResult.SUCCESS;
                }
                else if(hand == EnumHand.OFF_HAND && isPlayerInBattlemode(entityPlayer)){//No interaction with left hand item
                    PlayerEventChild.OffhandAttackEvent offAttackEvent = new PlayerEventChild.OffhandAttackEvent(event);
                    if(!MinecraftForge.EVENT_BUS.post(offAttackEvent)){
                        if (offAttackEvent.swingOffhand){
                            sendOffSwingEvent(event);
                        }
                        if (offAttackEvent.shouldAttack) {
                            ((IBattlePlayer) entityPlayer).attackTargetEntityWithCurrentOffItem(offAttackEvent.getTarget());
                        }
                        if(offAttackEvent.cancelParent){
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
            }
            return EnumActionResult.PASS;
        }
    }

    /**
     * Helper to send {@link PlayerEventChild.OffhandSwingEvent}
     * @param event the "parent" event
     */
    public static void sendOffSwingEvent(PlayerEvent event) {
        if (!MinecraftForge.EVENT_BUS.post(new PlayerEventChild.OffhandSwingEvent(event))) {
            event.getEntityPlayer().swingArm(EnumHand.OFF_HAND);
        }
    }

    /**
     * Refresh the player attribute map by swapping the wielding hand
     * WARNING Calling this <strong>twice</strong> is required to put the game back on its feet
     * @return the currently equipped stack, after swapping
     */
    public static ItemStack refreshAttributes(EntityPlayer entityPlayer){
        final ItemStack oldItem = entityPlayer.getHeldItemMainhand();
        final ItemStack newStack = entityPlayer.getHeldItemOffhand();
        refreshAttributes(entityPlayer.getAttributeMap(), oldItem, newStack);
        ((InventoryPlayerBattle)entityPlayer.inventory).swapHandItem();
        return newStack;
    }

    /**
     * Refresh the attribute map by removing from the old item and applying the current item
     * @param attributeMap the map to refresh
     * @param oldItem the old item whose attributes will be removed
     * @param currentItem the current item whose attributes will be applied
     */
    public static void refreshAttributes(AbstractAttributeMap attributeMap, ItemStack oldItem, ItemStack currentItem) {
        if(!oldItem.isEmpty())
            attributeMap.removeAttributeModifiers(oldItem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
        if(!currentItem.isEmpty())
            attributeMap.applyAttributeModifiers(currentItem.getAttributeModifiers(EntityEquipmentSlot.OFFHAND));
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
}
