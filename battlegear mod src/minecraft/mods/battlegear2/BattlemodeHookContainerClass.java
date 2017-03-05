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
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.BattlegearShieldFlashPacket;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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

    private boolean isValid(Entity entity){
        return entity instanceof EntityPlayer && !(entity instanceof FakePlayer);
    }
    /**
     * Crash the game if our inventory has been replaced by something else, or the coremod failed
     * Also synchronize battle inventory
     * @param event that spawned the player
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoin(EntityJoinWorldEvent event){
        if (isValid(event.getEntity())) {
            if (!(((EntityPlayer) event.getEntity()).inventory instanceof InventoryPlayerBattle) && !MinecraftForge.EVENT_BUS.post(new InventoryExceptionEvent((EntityPlayer)event.getEntity()))) {
                throw new RuntimeException("Player inventory has been replaced with " + ((EntityPlayer) event.getEntity()).inventory.getClass() + " which is incompatible with Mine & Blade:Battlegear.");
            }
            if(event.getEntity() instanceof EntityPlayerMP){
                Battlegear.packetHandler.sendPacketToPlayer(
                        new BattlegearSyncItemPacket((EntityPlayer) event.getEntity()).generatePacket(),
                        (EntityPlayerMP) event.getEntity());

            }
        }
        if (event.getEntity() instanceof EntityLivingBase) {
            ((EntityLivingBase) event.getEntity()).getEntityAttribute(Attributes.daze).setBaseValue(0);
        }
    }

    /**
     * Cancel the attack if the player reach is lowered by some types of items, or if barehanded
     * Note: Applies to either hands, since item is hotswap before this event for offhand weapons
     * @param event for the player attacking an entity
     */
    @SubscribeEvent
    public void attackEntity(AttackEntityEvent event){
        if(((IBattlePlayer) event.getEntityPlayer()).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            return;
        }
        double reachMod = maxReachDistance(event.getEntityPlayer());
        if (reachMod < event.getEntityPlayer().getDistanceToEntity(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    public double maxReachDistance(EntityPlayer player) {
        IAttributeInstance instance = player.getEntityAttribute(Attributes.extendedReach);
        double reachMod = instance.getAttributeValue();
        ItemStack itemStack = player.getHeldItemMainhand();
        if (!itemStack.isEmpty()) {
            if (itemStack.getItem() instanceof ItemBlock)
                reachMod += 0.1F;//Don't reduce block in hands range as much
            else
                reachMod -= instance.getBaseValue();
        }
        return reachMod + defaultReachDistance(player.capabilities.isCreativeMode);
    }

    public static float defaultReachDistance(boolean creative) {
        return creative ? 5.0F : 4.5F;
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if(event.getEntityPlayer().isSpectator() || !isValid(event.getEntity()))
            return;
        if(((IBattlePlayer) event.getEntityPlayer()).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.getEntityPlayer().isSwingInProgress = false;
        }else if(BattlegearUtils.isPlayerInBattlemode(event.getEntityPlayer())) {
            if(event instanceof PlayerInteractEvent.LeftClickBlock) {//Left click
                ItemStack mainHandItem = event.getEntityPlayer().getHeldItemMainhand();
                if(mainHandItem.getItem() instanceof IHandListener){
                    PlayerInteractEvent.LeftClickBlock copy = (PlayerInteractEvent.LeftClickBlock)copy(event);
                    copy.setUseItem(Event.Result.DENY);
                    Event.Result swing = ((IHandListener) mainHandItem.getItem()).onClickBlock(copy, mainHandItem, ((InventoryPlayerBattle) event.getEntityPlayer().inventory).getCurrentOffhandWeapon(), false);
                    if(swing != Event.Result.DEFAULT){
                        event.getEntityPlayer().isSwingInProgress = false;
                    }
                    if(swing == Event.Result.DENY){
                        event.setCanceled(true);
                    }else {
                        ((PlayerInteractEvent.LeftClickBlock) event).setUseBlock(copy.getUseBlock());
                        ((PlayerInteractEvent.LeftClickBlock) event).setUseItem(copy.getUseItem());
                    }
                }
            }else if(event.getHand() == EnumHand.OFF_HAND) {//Right click
                ItemStack offhandItem = event.getItemStack();
                if(offhandItem.isEmpty()) {
                    sendOffSwingEvent(event);
                }else if(!(event instanceof PlayerInteractEvent.EntityInteract) && BattlegearUtils.usagePriorAttack(offhandItem, event.getEntityPlayer(), true)) {
                    ItemStack mainHandItem = event.getEntityPlayer().getHeldItemMainhand();
                    if (mainHandItem.isEmpty() || !BattlegearUtils.usagePriorAttack(mainHandItem, event.getEntityPlayer(), false)) {
                        event.setCanceled(true);
                    }
                }
            }
        }else if(event instanceof PlayerInteractEvent.RightClickBlock) {
            TileEntity tile = event.getEntityPlayer().world.getTileEntity(event.getPos());
            if(tile instanceof IFlagHolder) {
                ItemStack mainHandItem = event.getEntityPlayer().getHeldItemMainhand();
                if (mainHandItem.isEmpty()) {
                    if(!event.getEntityPlayer().world.isRemote) {
                        List<ItemStack> flags = ((IFlagHolder) tile).getFlags();
                        if(flags.size()>0){
                            ItemStack flag = flags.remove(flags.size() - 1);
                            event.getEntityPlayer().inventory.setInventorySlotContents(event.getEntityPlayer().inventory.currentItem, flag);
                            event.getEntityPlayer().world.scheduleUpdate(event.getPos(), event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock(), 1);
                        }
                    }
                } else if (mainHandItem.getItem() instanceof IHeraldryItem) {
                    if(event.getEntityPlayer().world.isRemote) {
                        ((PlayerInteractEvent.RightClickBlock) event).setUseItem(Event.Result.DENY);
                    }else if(((IFlagHolder) tile).addFlag(mainHandItem)){
                        if(!event.getEntityPlayer().capabilities.isCreativeMode){
                            event.getEntityPlayer().inventory.decrStackSize(event.getEntityPlayer().inventory.currentItem, 1);
                        }
                        event.getEntityPlayer().world.scheduleUpdate(event.getPos(), event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock(), 1);
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
        PlayerInteractEvent copy = null;
        if(event instanceof PlayerInteractEvent.EntityInteract)
            copy = new PlayerInteractEvent.EntityInteract(event.getEntityPlayer(), event.getHand(), ((PlayerInteractEvent.EntityInteract) event).getTarget());
        else if(event instanceof PlayerInteractEvent.RightClickBlock) {
            copy = new PlayerInteractEvent.RightClickBlock(event.getEntityPlayer(), event.getHand(), event.getPos(), event.getFace(), ((PlayerInteractEvent.RightClickBlock) event).getHitVec());
            ((PlayerInteractEvent.RightClickBlock)copy).setUseItem(((PlayerInteractEvent.RightClickBlock) event).getUseItem());
            ((PlayerInteractEvent.RightClickBlock)copy).setUseBlock(((PlayerInteractEvent.RightClickBlock) event).getUseBlock());
        }
        else if(event instanceof PlayerInteractEvent.RightClickItem)
            copy = new PlayerInteractEvent.RightClickItem(event.getEntityPlayer(),event.getHand());
        else if(event instanceof PlayerInteractEvent.LeftClickBlock) {
            copy = new PlayerInteractEvent.LeftClickBlock(event.getEntityPlayer(), event.getPos(), event.getFace(), ((PlayerInteractEvent.LeftClickBlock) event).getHitVec());
            ((PlayerInteractEvent.LeftClickBlock)copy).setUseItem(((PlayerInteractEvent.LeftClickBlock) event).getUseItem());
            ((PlayerInteractEvent.LeftClickBlock)copy).setUseBlock(((PlayerInteractEvent.LeftClickBlock) event).getUseBlock());
        }
        if(event.isCanceled()){
            copy.setCanceled(true);
        }
        return copy;
    }

    public static void sendOffSwingEvent(PlayerInteractEvent player){
        if(!player.getEntityPlayer().isSwingInProgress) {
            PlayerEventChild.OffhandSwingEvent event = new PlayerEventChild.OffhandSwingEvent(copy(player));
            if (!event.mainHand.isEmpty() && BattlegearUtils.usagePriorAttack(event.mainHand, event.getPlayer(), false)) {
                event.setCanceled(true);
            }
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                event.getEntityPlayer().swingArm(EnumHand.OFF_HAND);
            }
        }
    }

    /**
     * Receive the swing event, send packet around for animation on other clients
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onOffhandSwingLast(PlayerEventChild.OffhandSwingEvent event) {
        Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.getEntityPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandAttack(PlayerEventChild.OffhandAttackEvent event){
        if(!event.offHand.isEmpty()){
            if (event.offHand.getItem() instanceof IOffhandListener) {
                ((IOffhandListener) event.offHand.getItem()).onAttackEntity(event, true);
            }else if(event.offHand.getItem() instanceof IShield || BattlegearUtils.usagePriorAttack(event.offHand, event.getPlayer(), true)){
                event.swingOffhand = false;
                event.shouldAttack = false;
            }else if(event.offHand.getItem() instanceof IArrowContainer2){
                event.setCanceled(true);
            }
        }
        if(!event.mainHand.isEmpty()) {
            if(event.mainHand.getItem() instanceof IOffhandListener) {
                ((IOffhandListener) event.mainHand.getItem()).onAttackEntity(event, false);
            }else if (event.shouldAttack && !event.isCanceled() && BattlegearUtils.usagePriorAttack(event.mainHand, event.getPlayer(), false)) {
                event.cancelParent = false;
            }
        }
        if(event.shouldAttack && !event.getEntityPlayer().capabilities.isCreativeMode && !event.getEntityPlayer().isHandActive()){
            if(Battlegear.proxy.handleAttack(event.getEntityPlayer())){
                event.shouldAttack = false;
                event.swingOffhand = false;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandItem(PlayerInteractEvent.RightClickItem offhandItemEvent){
        if(offhandItemEvent.getHand() == EnumHand.OFF_HAND){
            onOffhandUse(new PlayerEventChild.UseOffhandItemEvent(offhandItemEvent));
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandBlock(PlayerInteractEvent.RightClickBlock offhandItemEvent){
        if(offhandItemEvent.getHand() == EnumHand.OFF_HAND){
            onOffhandUse(new PlayerEventChild.UseOffhandItemEvent(offhandItemEvent));
        }
    }

    public void onOffhandUse(PlayerEventChild.UseOffhandItemEvent offhandItemEvent){
        if(!offhandItemEvent.offhand.isEmpty()){
            ItemStack offhandItem = offhandItemEvent.offhand;
            if(!BattlegearUtils.usagePriorAttack(offhandItem, offhandItemEvent.getPlayer(), true)){
                offhandItemEvent.event.setCanceled(true);
                if(offhandItemEvent.onBlock()) {
                    offhandItemEvent.setUseBlock(Event.Result.DENY);
                }
            }
            if (offhandItem.getItem() instanceof IShield || offhandItem.getItem() instanceof IArrowContainer2 || BattlegearUtils.usagePriorAttack(offhandItem, offhandItemEvent.getPlayer(), true)) {
                offhandItemEvent.swingOffhand = false;
            }
            Event.Result cancel = offhandItemEvent.swingOffhand ? Event.Result.DEFAULT : Event.Result.ALLOW;
            if (offhandItem.getItem() instanceof IHandListener) {
                if (offhandItemEvent.onBlock()) {
                    cancel = ((IHandListener) offhandItem.getItem()).onClickBlock(offhandItemEvent.event, offhandItemEvent.getPlayer().getHeldItemMainhand(), offhandItem, true);
                } else if(offhandItem.getItem() instanceof IOffhandListener){
                    cancel = ((IOffhandListener) offhandItem.getItem()).onClickAir(offhandItemEvent.getPlayer(), offhandItemEvent.getPlayer().getHeldItemMainhand(), offhandItem);
                }
            }
            if (cancel == Event.Result.DENY)
                offhandItemEvent.setCanceled(true);
            else if (cancel == Event.Result.DEFAULT)
                offhandItemEvent.swingOffhand = true;
        }
        if(!offhandItemEvent.isCanceled() && offhandItemEvent.swingOffhand){
            ItemStack mainHand = offhandItemEvent.getPlayer().getHeldItemMainhand();
            if(!mainHand.isEmpty() && BattlegearUtils.usagePriorAttack(mainHand, offhandItemEvent.getPlayer(), false)) {
               offhandItemEvent.swingOffhand = false;
            }else
                BattlegearUtils.sendOffSwingEvent(offhandItemEvent.event);
        }
    }

    @SubscribeEvent
    public void shieldHook(LivingHurtEvent event){
        if(isValid(event.getEntity())){
            EntityPlayer player = (EntityPlayer)event.getEntity();
            if(BattlegearUtils.isBlockingWithShield(player)){
                final ItemStack shield = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
                final float dmg = event.getAmount();
                if(((IShield)shield.getItem()).canBlock(shield, event.getSource())){
                    boolean shouldBlock = true;
                    Entity opponent = event.getSource().getEntity();
                    if(opponent != null){
                        float yaw = getAngle(opponent, player);
                        float blockAngle = ((IShield) shield.getItem()).getBlockAngle(shield);
                        shouldBlock = yaw < blockAngle && yaw > -blockAngle;
                    }

                    if(shouldBlock){
                        PlayerEventChild.ShieldBlockEvent blockEvent = new PlayerEventChild.ShieldBlockEvent(new PlayerEvent(player), shield, event.getSource(), dmg);
                        MinecraftForge.EVENT_BUS.post(blockEvent);
                        if (blockEvent.ammountRemaining > 0.0F) {
                            event.setAmount(blockEvent.ammountRemaining);
                        } else {
                            event.setCanceled(true);
                        }

                        if(blockEvent.performAnimation){
                            Battlegear.packetHandler.sendPacketAround(player, 32, new BattlegearShieldFlashPacket(player, dmg).generatePacket());
                            ((IShield)shield.getItem()).blockAnimation(player, dmg);
                        }

                        if(event.getSource().isProjectile() && event.getSource().getSourceOfDamage() instanceof IProjectile){
                            if(shield.getItem() instanceof IArrowCatcher){
                                if(((IArrowCatcher)shield.getItem()).catchArrow(shield, player, (IProjectile)event.getSource().getSourceOfDamage())){
                                    ((InventoryPlayerBattle)player.inventory).hasChanged = true;
                                }
                            }
                        }

                        if(blockEvent.damageShield && !player.capabilities.isCreativeMode){
                            float red = ((IShield)shield.getItem()).getDamageReduction(shield, event.getSource());
                            if(red<dmg){
                                shield.damageItem(Math.round(dmg-red), player);
                                if(shield.isEmpty()){
                                    BattlegearUtils.setPlayerOffhandItem(player, ItemStack.EMPTY);
                                }
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
    	if(BaseEnchantment.bowLoot.isPresent() && event.getSource().isProjectile() && event.getSource().getEntity() instanceof EntityLivingBase){
    		ItemStack stack = ((EntityLivingBase) event.getSource().getEntity()).getHeldItemMainhand();
    		if(!addLootFromEnchant(stack, event.getDrops()) && event.isRecentlyHit() && isValid(event.getSource().getEntity())){
                EntityPlayer player = (EntityPlayer) event.getSource().getEntity();
                if(BattlegearUtils.isPlayerInBattlemode(player)) {
                    stack = player.getHeldItemOffhand();
                    addLootFromEnchant(stack, event.getDrops());
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
                if(!drop.isEmpty() && drop.getMaxStackSize()>=drop.getCount()+lvl){
                    drop.grow(lvl);
                }
            }
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void addTracking(PlayerEvent.StartTracking event){
        if(isValid(event.getTarget())){
            ((EntityPlayerMP)event.getEntityPlayer()).connection.sendPacket(new BattlegearSyncItemPacket((EntityPlayer) event.getTarget()).generatePacket());
        }
    }
}
