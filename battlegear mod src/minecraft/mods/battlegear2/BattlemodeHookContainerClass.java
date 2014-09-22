package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.core.InventoryExceptionEvent;
import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.shield.IArrowCatcher;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.IOffhandDual;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearShieldFlashPacket;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.packet.OffhandPlaceBlockPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;

import java.util.List;

public class BattlemodeHookContainerClass {
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
                throw new RuntimeException("Player inventory has been replaced with " + ((EntityPlayer) event.entity).inventory.getClass());
            }
            if(event.entity instanceof EntityPlayerMP){
                Battlegear.packetHandler.sendPacketToPlayer(
                        new BattlegearSyncItemPacket((EntityPlayer) event.entity).generatePacket(),
                        (EntityPlayerMP) event.entity);

            }
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

        ItemStack mainhand = event.entityPlayer.getCurrentEquippedItem();
        float reachMod = 0;
        if(mainhand == null)
            reachMod = -2.2F;//Reduce bare hands range
        else if(mainhand.getItem() instanceof ItemBlock)
            reachMod = -2.1F;//Reduce block in hands range too
        else if(mainhand.getItem() instanceof IExtendedReachWeapon)
            reachMod = ((IExtendedReachWeapon) mainhand.getItem()).getReachModifierInBlocks(mainhand);
        if(reachMod < 0 && reachMod + (event.entityPlayer.capabilities.isCreativeMode?5.0F:4.5F) < event.entityPlayer.getDistanceToEntity(event.target)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if(isFake(event.entityPlayer))
            return;
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.entityPlayer.isSwingInProgress = false;
        }else if(((IBattlePlayer) event.entityPlayer).isBattlemode()) {
            if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                if(mainHandItem == null || !BattlegearUtils.usagePriorAttack(mainHandItem)) {
                    ItemStack offhandItem = ((InventoryPlayerBattle) event.entityPlayer.inventory).getCurrentOffhandWeapon();
                    if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                        event.setCanceled(true);
                    sendOffSwingEvent(event, mainHandItem, offhandItem);
                }
            }
        }else if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
            if(tile != null && tile instanceof IFlagHolder) {
                ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                if (mainHandItem == null) {
                    if(!event.entityPlayer.worldObj.isRemote) {
                        List<ItemStack> flags = ((IFlagHolder) tile).getFlags();
                        if(flags.size()>0){
                            ItemStack flag = flags.remove(flags.size() - 1);
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, flag);
                            event.entityPlayer.worldObj.markBlockForUpdate(event.x, event.y, event.z);
                        }
                    }
                } else if (mainHandItem.getItem() instanceof IHeraldryItem) {
                    if(event.entityPlayer.worldObj.isRemote) {
                        event.useItem = Event.Result.DENY;
                    }else if(((IFlagHolder) tile).addFlag(mainHandItem)){
                        if(!event.entityPlayer.capabilities.isCreativeMode){
                            event.entityPlayer.inventory.decrStackSize(event.entityPlayer.inventory.currentItem, 1);
                        }
                        event.entityPlayer.worldObj.markBlockForUpdate(event.x, event.y, event.z);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * Attempts to right-click-use an item by the given EntityPlayer
     */
    public static boolean tryUseItem(EntityPlayer entityPlayer, ItemStack itemStack, Side side)
    {
        if(side.isClient()){
            Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(-1, -1, -1, 255, itemStack, 0.0F, 0.0F, 0.0F).generatePacket());
        }
        final int i = itemStack.stackSize;
        final int j = itemStack.getItemDamage();
        ItemStack itemstack1 = itemStack.useItemRightClick(entityPlayer.getEntityWorld(), entityPlayer);

        if (itemstack1 == itemStack && (itemstack1 == null || itemstack1.stackSize == i && (side.isServer()?(itemstack1.getMaxItemUseDuration() <= 0 && itemstack1.getItemDamage() == j):true)))
        {
            return false;
        }
        else
        {
            BattlegearUtils.setPlayerOffhandItem(entityPlayer, itemstack1);
            if (side.isServer() && ((EntityPlayerMP)entityPlayer).theItemInWorldManager.isCreative())
            {
                itemstack1.stackSize = i;
                if (itemstack1.isItemStackDamageable())
                {
                    itemstack1.setItemDamage(j);
                }
            }
            if (itemstack1.stackSize <= 0)
            {
                BattlegearUtils.setPlayerOffhandItem(entityPlayer, null);
                ForgeEventFactory.onPlayerDestroyItem(entityPlayer, itemstack1);
            }
            if (side.isServer() && !entityPlayer.isUsingItem())
            {
                ((EntityPlayerMP)entityPlayer).sendContainerToPlayer(entityPlayer.inventoryContainer);
            }
            return true;
        }
    }

    public static void sendOffSwingEvent(PlayerEvent event, ItemStack mainHandItem, ItemStack offhandItem){
        if(!MinecraftForge.EVENT_BUS.post(new PlayerEventChild.OffhandSwingEvent(event, mainHandItem, offhandItem))){
            ((IBattlePlayer) event.entityPlayer).swingOffItem();
            Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onOffhandSwing(PlayerEventChild.OffhandSwingEvent event){
        if(event.offHand != null && event.parent.getClass().equals(PlayerInteractEvent.class)){
            if (event.offHand.getItem() instanceof IShield || BattlegearUtils.usagePriorAttack(event.offHand)){
                event.setCanceled(true);
            }else if(event.offHand.getItem() instanceof IOffhandDual){
                boolean shouldSwing = true;
                if(((PlayerInteractEvent)event.parent).action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                    shouldSwing = ((IOffhandDual) event.offHand.getItem()).offhandClickAir((PlayerInteractEvent)event.parent, event.mainHand, event.offHand);
                else if(((PlayerInteractEvent)event.parent).action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
                    ((PlayerInteractEvent)event.parent).useItem = Event.Result.DENY;
                    shouldSwing = ((IOffhandDual) event.offHand.getItem()).offhandClickBlock((PlayerInteractEvent)event.parent, event.mainHand, event.offHand);
                }
                if(!shouldSwing){
                    event.setCanceled(true);
                }
            }
        }
        if(event.mainHand !=null && BattlegearUtils.isBow(event.mainHand.getItem()) && event.parent.getClass().equals(PlayerInteractEvent.class)){
            event.setCanceled(true);
            event.setCancelParentEvent(false);
        }
    }

    @SubscribeEvent
    public void playerIntereactEntity(EntityInteractEvent event) {
        if(isFake(event.entityPlayer))
            return;
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
            event.entityPlayer.isSwingInProgress = false;
        } else if (((IBattlePlayer) event.entityPlayer).isBattlemode()) {
            ItemStack offhandItem = ((InventoryPlayerBattle)event.entityPlayer.inventory).getCurrentOffhandWeapon();
            if(offhandItem == null || !BattlegearUtils.usagePriorAttack(offhandItem)){
                ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
                PlayerEventChild.OffhandAttackEvent offAttackEvent = new PlayerEventChild.OffhandAttackEvent(event, mainHandItem, offhandItem);
                if(!MinecraftForge.EVENT_BUS.post(offAttackEvent)){
                    if (offAttackEvent.swingOffhand){
                        sendOffSwingEvent(event, mainHandItem, offhandItem);
                    }
                    if (offAttackEvent.shouldAttack) {
                        ((IBattlePlayer) event.entityPlayer).attackTargetEntityWithCurrentOffItem(event.target);
                    }
                    if (offAttackEvent.cancelParent) {
                        event.setCanceled(true);
                    }
                }
            }

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onOffhandAttack(PlayerEventChild.OffhandAttackEvent event){
        if(event.offHand!=null){
            if(event.offHand.getItem() instanceof IOffhandDual){
                event.swingOffhand =((IOffhandDual) event.offHand.getItem()).offhandAttackEntity(event, event.mainHand, event.offHand);
            }else if(event.offHand.getItem() instanceof IShield || BattlegearUtils.isBow(event.offHand.getItem())){
                event.swingOffhand = false;
                event.shouldAttack = false;
            }else if(event.offHand.getItem() instanceof IArrowContainer2){
                event.shouldAttack = false;
            }
        }
        if(event.mainHand !=null && BattlegearUtils.isBow(event.mainHand.getItem())){
            event.swingOffhand = false;
            event.shouldAttack = false;
        }
    }

    @SubscribeEvent
    public void shieldHook(LivingHurtEvent event){
        if(isFake(event.entity))
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
                        double d0 = opponent.posX - event.entity.posX;
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

                        float blockAngle = ((IShield) shield.getItem()).getBlockAngle(shield);

                        shouldBlock = yaw < blockAngle && yaw > -blockAngle;
                        //player.knockBack(opponent, 50, 100, 100);
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
                                shield.damageItem(Math.round(dmg-red), player);
                                if(shield.stackSize <= 0){
                                    ForgeEventFactory.onPlayerDestroyItem(player, shield);
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem + 3, null);
                                    //TODO Render item break
                                }
                                ((InventoryPlayerBattle)player.inventory).hasChanged = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrop(LivingDropsEvent event){
    	if(BaseEnchantment.bowLoot != null && event.source.getEntity() instanceof EntityLivingBase){
    		ItemStack stack = ((EntityLivingBase) event.source.getEntity()).getHeldItem();
    		if(stack!=null && BaseEnchantment.bowLoot.canApply(stack)){
                addLootFromEnchant(stack, event.drops);
    		}else if(event.source.getEntity() instanceof EntityPlayer && !isFake(event.source.getEntity())){
                stack = ((InventoryPlayerBattle)((EntityPlayer) event.source.getEntity()).inventory).getCurrentOffhandWeapon();
                if(stack!=null && BaseEnchantment.bowLoot.canApply(stack))
                    addLootFromEnchant(stack, event.drops);
            }
    	}
    }

    private void addLootFromEnchant(ItemStack bow, List<EntityItem> drops){
        int lvl = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowLoot.effectId, bow);
        if(lvl>0){
            ItemStack drop;
            for(EntityItem items:drops){
                drop = items.getEntityItem();
                if(drop!=null && drop.getMaxStackSize()<drop.stackSize+lvl){
                    drop.stackSize+=lvl;
                    items.setEntityItemStack(drop);
                }
            }
        }
    }

    @SubscribeEvent
    public void addTracking(PlayerEvent.StartTracking event){
        if(event.target instanceof EntityPlayer && !isFake(event.target)){
            ((EntityPlayerMP)event.entityPlayer).playerNetServerHandler.sendPacket(new BattlegearSyncItemPacket((EntityPlayer) event.target).generatePacket());
        }
    }
}
