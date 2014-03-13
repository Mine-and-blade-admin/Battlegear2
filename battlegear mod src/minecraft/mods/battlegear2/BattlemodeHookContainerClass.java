package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;

public class BattlemodeHookContainerClass {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoin(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayerMP){
            Battlegear.packetHandler.sendPacketToPlayer(
                    new BattlegearSyncItemPacket((EntityPlayer) event.entity).generatePacket(),
                    (EntityPlayerMP) event.entity);

        }
    }

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
        if(reachMod < 0 && reachMod + 4 < event.entityPlayer.getDistanceToEntity(event.target)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void playerInterect(PlayerInteractEvent event) {
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.entityPlayer.isSwingInProgress = false;
        }else if(((IBattlePlayer) event.entityPlayer).isBattlemode()) {
            ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
            ItemStack offhandItem = ((InventoryPlayerBattle)event.entityPlayer.inventory).getCurrentOffhandWeapon();

            switch (event.action) {
                case LEFT_CLICK_BLOCK:
                    break;
                case RIGHT_CLICK_BLOCK:
                    sendOffSwingEvent(event, mainHandItem, offhandItem);
                    break;
                case RIGHT_CLICK_AIR:
                    if (mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem, offhandItem)) {
                        event.setCanceled(true);
                        sendOffSwingEvent(event, mainHandItem, offhandItem);
                    }
                    break;
            }
        }

    }

    private static void sendOffSwingEvent(PlayerEvent event, ItemStack mainHandItem, ItemStack offhandItem){
        if(!MinecraftForge.EVENT_BUS.post(new PlayerEventChild.OffhandSwingEvent(event, mainHandItem, offhandItem))){
            ((IBattlePlayer) event.entityPlayer).swingOffItem();
            Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onOffhandSwing(PlayerEventChild.OffhandSwingEvent event){
        if(event.offHand != null && event.parent.getClass().equals(PlayerInteractEvent.class)){
            if (event.offHand.getItem() instanceof IShield){
                ((PlayerInteractEvent)event.parent).useItem = Event.Result.DENY;
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
    }

    @SubscribeEvent
    public void playerIntereactEntity(EntityInteractEvent event) {
        if(((IBattlePlayer) event.entityPlayer).getSpecialActionTimer() > 0){
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);
            event.entityPlayer.isSwingInProgress = false;
        } else if (((IBattlePlayer) event.entityPlayer).isBattlemode()) {
            ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
            ItemStack offhandItem = ((InventoryPlayerBattle)event.entityPlayer.inventory).getCurrentOffhandWeapon();
            if(mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem, offhandItem)){
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
                        event.setResult(Event.Result.DENY);
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
            }else if(event.offHand.getItem() instanceof IShield){
                event.swingOffhand = false;
                event.shouldAttack = false;
            }else if(event.offHand.getItem() instanceof IArrowContainer2){
                event.shouldAttack = false;
            }
        }
    }

    @SubscribeEvent
    public void shieldHook(LivingHurtEvent event){

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
                        event.setCanceled(true);
                        PlayerEventChild.ShieldBlockEvent blockEvent = new PlayerEventChild.ShieldBlockEvent(new PlayerEvent(player), shield, event.source, dmg);
                        MinecraftForge.EVENT_BUS.post(blockEvent);

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
                                if(shield.getItemDamage() <= 0){
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
    	if(event.source.getEntity() instanceof EntityLivingBase){
    		ItemStack stack = ((EntityLivingBase) event.source.getEntity()).getEquipmentInSlot(0);
    		if(stack!=null && stack.getItem() instanceof ItemBow){
    			int lvl = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowLoot.effectId, stack);
    			if(lvl>0){
    				ItemStack drop;
    				for(EntityItem items:event.drops){
    					drop = items.getEntityItem();
    					if(drop!=null && drop.getMaxStackSize()<drop.stackSize+lvl){
    						drop.stackSize+=lvl;
    						items.setEntityItemStack(drop);
    					}
    				}
    			}
    		}
    	}
    }

}
