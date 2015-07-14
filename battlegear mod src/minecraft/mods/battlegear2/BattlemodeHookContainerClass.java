package mods.battlegear2;

import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.IHandListener;
import mods.battlegear2.api.IOffhandListener;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryExceptionEvent;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.shield.IArrowCatcher;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.Attributes;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.BattlegearShieldFlashPacket;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import mods.battlegear2.packet.OffhandPlaceBlockPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public final class BattlemodeHookContainerClass {

    public static final BattlemodeHookContainerClass INSTANCE = new BattlemodeHookContainerClass();

    private BattlemodeHookContainerClass(){}

    private boolean isFake(Entity entity){
        return entity instanceof FakePlayer;
    }
    /**
     * Crash the game if our inventory has been replaced by something else, or the coremod failed
     * Also synchronize battle inventory
     * @param event that spawned the player
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoin(EntityJoinWorldEvent event){
        if (event.entity instanceof EntityPlayer && !(isFake(event.entity))) {
            if (!(((EntityPlayer) event.entity).inventory instanceof InventoryPlayerBattle) && !MinecraftForge.EVENT_BUS.post(new InventoryExceptionEvent((EntityPlayer)event.entity))) {
                throw new RuntimeException("Player inventory has been replaced with " + ((EntityPlayer) event.entity).inventory.getClass() + " which is incompatible with Mine & Blade:Battlegear.");
            }
            if(event.entity instanceof EntityPlayerMP){
                Battlegear.packetHandler.sendPacketToPlayer(
                        new BattlegearSyncItemPacket((EntityPlayer) event.entity).generatePacket(),
                        (EntityPlayerMP) event.entity);

            }
        }
        if (event.entity instanceof EntityLivingBase) {
            ((EntityLivingBase) event.entity).getAttributeMap().getAttributeInstanceByName(Attributes.daze.getAttributeUnlocalizedName()).setBaseValue(0);
        }
    }

    /**
     * Cancel the attack if the player reach is lowered by some types of items, or if barehanded
     * Note: Applies to either hands, since item is hotswap before this event for offhand weapons
     * @param event for the player attacking an entity
     */
    @SubscribeEvent
    public void attackEntity(AttackEntityEvent event){
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            return;
        }
        double reachMod = maxReachDistance(event.entityPlayer);
        if (reachMod < event.entityPlayer.getDistanceToEntity(event.target)) {
            event.setCanceled(true);
        }
    }

    public double maxReachDistance(EntityPlayer player) {
        IAttributeInstance instance = player.getEntityAttribute(Attributes.extendedReach);
        double reachMod = 0;
        if (instance != null) {
            reachMod = instance.getAttributeValue();
            ItemStack itemStack = player.getCurrentEquippedItem();
            if (itemStack != null) {
                if (itemStack.getItem() instanceof IExtendedReachWeapon && !itemStack.getAttributeModifiers().containsKey(Attributes.extendedReach.getAttributeUnlocalizedName()))
                    reachMod = ((IExtendedReachWeapon) itemStack.getItem()).getReachModifierInBlocks(itemStack);
                else if (itemStack.getItem() instanceof ItemBlock)
                    reachMod += 0.1F;//Don't reduce block in hands range as much
                else
                    reachMod -= instance.getBaseValue();
            }
        }
        return reachMod + defaultReachDistance(player.capabilities.isCreativeMode);
    }

    public static float defaultReachDistance(boolean creative) {
        return creative ? 5.0F : 4.5F;
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if(isFake(event.entityPlayer))
            return;
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.entityPlayer.isSwingInProgress = false;
        }else if(BattlegearUtils.isPlayerInBattlemode(event.entityPlayer)) {
            if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {//Left click
                ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                if(mainHandItem!=null && mainHandItem.getItem() instanceof IHandListener){
                    PlayerInteractEvent copy = copy(event);
                    copy.useItem = Event.Result.DENY;
                    Event.Result swing = ((IHandListener) mainHandItem.getItem()).onClickBlock(copy, mainHandItem, ((InventoryPlayerBattle) event.entityPlayer.inventory).getCurrentOffhandWeapon(), false);
                    if(swing != Event.Result.DEFAULT){
                        event.entityPlayer.isSwingInProgress = false;
                    }
                    if(swing == Event.Result.DENY){
                        event.setCanceled(true);
                    }else {
                        event.useBlock = copy.useBlock;
                        event.useItem = copy.useItem;
                    }
                }
            }else {//Right click
                ItemStack offhandItem = ((InventoryPlayerBattle) event.entityPlayer.inventory).getCurrentOffhandWeapon();
                if(offhandItem == null) {
                    sendOffSwingEvent(event, null, null);
                }else if(BattlegearUtils.usagePriorAttack(offhandItem, event.entityPlayer, true)) {
                    ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                    if (mainHandItem == null || !BattlegearUtils.usagePriorAttack(mainHandItem, event.entityPlayer, false)) {
                        event.setCanceled(true);
                    }
                }
            }
        }else if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.pos);
            if(tile != null && tile instanceof IFlagHolder) {
                ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                if (mainHandItem == null) {
                    if(!event.entityPlayer.worldObj.isRemote) {
                        List<ItemStack> flags = ((IFlagHolder) tile).getFlags();
                        if(flags.size()>0){
                            ItemStack flag = flags.remove(flags.size() - 1);
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, flag);
                            event.entityPlayer.worldObj.markBlockForUpdate(event.pos);
                        }
                    }
                } else if (mainHandItem.getItem() instanceof IHeraldryItem) {
                    if(event.entityPlayer.worldObj.isRemote) {
                        event.useItem = Event.Result.DENY;
                    }else if(((IFlagHolder) tile).addFlag(mainHandItem)){
                        if(!event.entityPlayer.capabilities.isCreativeMode){
                            event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
                        }
                        event.entityPlayer.worldObj.markBlockForUpdate(event.pos);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * Make a complete copy of the argument.
     * @param event to copy over
     * @return the copy
     */
    private static PlayerInteractEvent copy(PlayerInteractEvent event){
        PlayerInteractEvent copy = new PlayerInteractEvent(event.entityPlayer, event.action, event.pos, event.face, event.world);
        if(event.isCanceled()){
            copy.setCanceled(true);
        }
        copy.useItem = event.useItem;
        copy.useBlock = event.useBlock;
        return copy;
    }

    /**
     * Attempts to right-click-use an item by the given EntityPlayer
     */
    public static boolean tryUseItem(EntityPlayer entityPlayer, ItemStack itemStack, Side side)
    {
        if(side.isClient()){
            Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(itemStack).generatePacket());
        }
        final int i = itemStack.stackSize;
        final int j = itemStack.getMetadata();
        ItemStack itemstack1 = itemStack.useItemRightClick(entityPlayer.getEntityWorld(), entityPlayer);

        if (itemstack1 == itemStack && (itemstack1 == null || itemstack1.stackSize == i && (side.isServer() ? (itemstack1.getMaxItemUseDuration() <= 0 && itemstack1.getMetadata() == j) : true)))
        {
            return false;
        }else{
            BattlegearUtils.setPlayerCurrentItem(entityPlayer, itemstack1);
            if (side.isServer() && ((EntityPlayerMP)entityPlayer).theItemInWorldManager.isCreative()){
                itemstack1.stackSize = i;
                if (itemstack1.isItemStackDamageable())
                {
                    itemstack1.setItemDamage(j);
                }
            }
            if (itemstack1.stackSize <= 0){
                entityPlayer.destroyCurrentEquippedItem();
            }
            if (side.isServer() && !entityPlayer.isUsingItem()){
                ((EntityPlayerMP) entityPlayer).sendContainerToPlayer(entityPlayer.inventoryContainer);
            }
            return true;
        }
    }

    public static void sendOffSwingEvent(PlayerInteractEvent player, ItemStack mainHandItem, ItemStack offhandItem){
        PlayerEventChild.OffhandSwingEvent event = new PlayerEventChild.OffhandSwingEvent(copy(player), offhandItem);
        if(event.mainHand != null && BattlegearUtils.usagePriorAttack(event.mainHand, event.getPlayer(), false)) {
            event.setCanceled(true);
        }
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            ((IBattlePlayer) event.entityPlayer).swingOffItem();
        }
    }

    /**
     * Receive the swing event, send packet around for animation on other clients
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onOffhandSwingLast(PlayerEventChild.OffhandSwingEvent event) {
        Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerInteractEntity(EntityInteractEvent event) {
        if(isFake(event.entityPlayer))
            return;
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.entityPlayer.isSwingInProgress = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandAttack(PlayerEventChild.OffhandAttackEvent event){
        if(event.offHand != null){
            if (event.offHand.getItem() instanceof IOffhandListener) {
                ((IOffhandListener) event.offHand.getItem()).onAttackEntity(event, true);
            }else if(event.offHand.getItem() instanceof IShield || BattlegearUtils.usagePriorAttack(event.offHand, event.getPlayer(), true)){
                event.swingOffhand = false;
                event.shouldAttack = false;
            }else if(event.offHand.getItem() instanceof IArrowContainer2){
                event.setCanceled(true);
            }
        }
        if(event.mainHand != null) {
            if(event.mainHand.getItem() instanceof IOffhandListener) {
                ((IOffhandListener) event.mainHand.getItem()).onAttackEntity(event, false);
            }else if (event.shouldAttack && !event.isCanceled() && BattlegearUtils.usagePriorAttack(event.mainHand, event.getPlayer(), false)) {
                event.cancelParent = false;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandUse(PlayerEventChild.UseOffhandItemEvent offhandItemEvent){
        if(offhandItemEvent.offhand!=null){
            ItemStack offhandItem = offhandItemEvent.offhand;
            if(!BattlegearUtils.usagePriorAttack(offhandItem, offhandItemEvent.getPlayer(), true)){
                offhandItemEvent.event.useItem = Event.Result.DENY;
                if(offhandItemEvent.onBlock()) {
                    offhandItemEvent.event.useBlock = Event.Result.DENY;
                }
            }
            if (offhandItem.getItem() instanceof IShield || offhandItem.getItem() instanceof IArrowContainer2 || BattlegearUtils.usagePriorAttack(offhandItem, offhandItemEvent.getPlayer(), true)) {
                offhandItemEvent.swingOffhand = false;
            }
            Event.Result cancel = offhandItemEvent.swingOffhand ? Event.Result.DEFAULT : Event.Result.ALLOW;
            if (offhandItem.getItem() instanceof IHandListener) {
                if (offhandItemEvent.onBlock()) {
                    cancel = ((IHandListener) offhandItem.getItem()).onClickBlock(offhandItemEvent.event, offhandItemEvent.getPlayer().getCurrentEquippedItem(), offhandItem, true);
                } else if(offhandItem.getItem() instanceof IOffhandListener){
                    cancel = ((IOffhandListener) offhandItem.getItem()).onClickAir(offhandItemEvent.getPlayer(), offhandItemEvent.getPlayer().getCurrentEquippedItem(), offhandItem);
                }
            }
            if (cancel == Event.Result.DENY)
                offhandItemEvent.setCanceled(true);
            else if (cancel == Event.Result.DEFAULT)
                offhandItemEvent.swingOffhand = true;
        }
        if(!offhandItemEvent.isCanceled() && offhandItemEvent.swingOffhand){
            ItemStack mainHand = offhandItemEvent.getPlayer().getCurrentEquippedItem();
            if(mainHand != null && BattlegearUtils.usagePriorAttack(mainHand, offhandItemEvent.getPlayer(), false)) {
               offhandItemEvent.swingOffhand = false;
            }
        }
    }

    @SubscribeEvent
    public void shieldHook(LivingHurtEvent event){
        if (isFake(event.entity))
            return;
        if(event.entity instanceof IBattlePlayer){
            EntityPlayer player = (EntityPlayer)event.entity;
            if(((IBattlePlayer) player).getSpecialActionTimer() > 0){
                event.setCanceled(true);
            } else if(((IBattlePlayer) player).isBlockingWithShield()){
                final ItemStack shield = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
                final float dmg = event.ammount;
                if(((IShield)shield.getItem()).canBlock(shield, event.source)){
                    boolean shouldBlock = true;
                    Entity opponent = event.source.getEntity();
                    if(opponent != null){
                        float yaw = getAngle(opponent, player);
                        float blockAngle = ((IShield) shield.getItem()).getBlockAngle(shield);
                        shouldBlock = yaw < blockAngle && yaw > -blockAngle;
                    }

                    if(shouldBlock){
                        PlayerEventChild.ShieldBlockEvent blockEvent = new PlayerEventChild.ShieldBlockEvent(new PlayerEvent(player), shield, event.source, dmg);
                        MinecraftForge.EVENT_BUS.post(blockEvent);
                        if (blockEvent.ammountRemaining > 0.0F) {
                            event.ammount = blockEvent.ammountRemaining;
                        } else {
                            event.setCanceled(true);
                        }

                        if(blockEvent.performAnimation){
                            Battlegear.packetHandler.sendPacketAround(player, 32, new BattlegearShieldFlashPacket(player, dmg).generatePacket());
                            ((IShield)shield.getItem()).blockAnimation(player, dmg);
                        }

                        if(event.source.isProjectile() && event.source.getSourceOfDamage() instanceof IProjectile){
                            if(shield.getItem() instanceof IArrowCatcher){
                                if(((IArrowCatcher)shield.getItem()).catchArrow(shield, player, (IProjectile)event.source.getSourceOfDamage())){
                                    ((InventoryPlayerBattle)player.inventory).hasChanged = true;
                                }
                            }
                        }

                        if(blockEvent.damageShield && !player.capabilities.isCreativeMode){
                            float red = ((IShield)shield.getItem()).getDamageReduction(shield, event.source);
                            if(red<dmg){
                                player.inventory.currentItem += InventoryPlayerBattle.WEAPON_SETS;
                                shield.damageItem(Math.round(dmg-red), player);
                                if(shield.stackSize <= 0){
                                    player.destroyCurrentEquippedItem();
                                }
                                player.inventory.currentItem -= InventoryPlayerBattle.WEAPON_SETS;
                            }
                        }
                    }
                }
            }
        }
    }

    private float getAngle(Entity opponent, Entity player){
        double d0 = opponent.posX - player.posX;
        double d1;

        for (d1 = opponent.posZ - player.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D){
            d0 = (Math.random() - Math.random()) * 0.01D;
        }

        float yaw = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - player.rotationYaw;
        yaw = yaw - 90;

        while(yaw < -180){
            yaw+= 360;
        }
        while(yaw >= 180){
            yaw-=360;
        }
        return yaw;
    }

    /**
     * Apply the "bow loot" enchantment, on drops from mobs dying of projectile based damage
     * Search first the right hand of the archer, then the left hand (if archer is a player in battlemode)
     */
    @SubscribeEvent
    public void onDrop(LivingDropsEvent event){
    	if(BaseEnchantment.bowLoot.isPresent() && event.source.isProjectile() && event.source.getEntity() instanceof EntityLivingBase){
    		ItemStack stack = ((EntityLivingBase) event.source.getEntity()).getHeldItem();
    		if(!addLootFromEnchant(stack, event.drops) && event.recentlyHit && event.source.getEntity() instanceof IBattlePlayer && !isFake(event.source.getEntity())){
                EntityPlayer player = (EntityPlayer) event.source.getEntity();
                if(BattlegearUtils.isPlayerInBattlemode(player)) {
                    stack = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
                    addLootFromEnchant(stack, event.drops);
                }
            }
    	}
    }

    /**
     * The "bow loot" enchantment effect:
     * Adds to each stack size the enchantment level
     *
     * @param bow the stack to check for the enchantment
     * @param drops to add drops to
     * @return true if the stack is enchanted with "bow loot"
     */
    private boolean addLootFromEnchant(ItemStack bow, List<EntityItem> drops){
        int lvl = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowLoot, bow);
        if(lvl>0){
            ItemStack drop;
            for(EntityItem items:drops){
                drop = items.getEntityItem();
                if(drop!=null && drop.getMaxStackSize()>=drop.stackSize+lvl){
                    drop.stackSize+=lvl;
                }
            }
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void addTracking(PlayerEvent.StartTracking event){
        if(event.target instanceof EntityPlayer && !isFake(event.target)){
            ((EntityPlayerMP)event.entityPlayer).playerNetServerHandler.sendPacket(new BattlegearSyncItemPacket((EntityPlayer) event.target).generatePacket());
        }
    }
}
