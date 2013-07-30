package mods.battlegear2;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.api.OffhandAttackEvent;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import mods.battlegear2.utils.BattlegearUtils;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class BattlemodeHookContainerClass {

    @ForgeSubscribe
    public void onEntityJoin(EntityJoinWorldEvent event){
        if(event.entity instanceof EntityPlayer){

            EntityPlayer entityPlayer = (EntityPlayer)event.entity;

            PacketDispatcher.sendPacketToPlayer(
                    BattlegearSyncItemPacket.generatePacket(
                            entityPlayer.username, entityPlayer.inventory),
                    (Player)entityPlayer);

        }
    }

    @ForgeSubscribe
    public void attackEntity(AttackEntityEvent event){

        ItemStack mainhand = event.entityPlayer.getCurrentEquippedItem();
        if(mainhand != null){
            if(mainhand.getItem() instanceof IExtendedReachWeapon){
                float reachMod = ((IExtendedReachWeapon) mainhand.getItem()).getReachModifierInBlocks(mainhand);
                if(reachMod < 0){
                    if(reachMod + 4 < event.entityPlayer.getDistanceToEntity(event.target)){
                        event.setCanceled(true);
                    }
                }
            }
        }

    }

    @ForgeSubscribe
    public void playerInterect(PlayerInteractEvent event) {

        if (event.entityPlayer.isBattlemode()) {
            ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
            ItemStack offhandItem = event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem + 3);

            switch (event.action) {
                case LEFT_CLICK_BLOCK:
                    break;
                case RIGHT_CLICK_BLOCK:

                    if (offhandItem != null && offhandItem.getItem() instanceof IBattlegearWeapon) {
                        event.useItem = Result.DENY;
                        boolean shouldSwing = ((IBattlegearWeapon) offhandItem.getItem()).offhandClickBlock(event, mainHandItem, offhandItem);

                        if (shouldSwing) {
                            event.entityPlayer.swingOffItem();
                            Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                        }

                    } else {
                        event.entityPlayer.swingOffItem();
                        Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                    }
                    break;

                case RIGHT_CLICK_AIR:

                    if (mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem.itemID)) {

                        event.useItem = Result.DENY;
                        event.setCanceled(true);

                        if (offhandItem != null && offhandItem.getItem() instanceof IBattlegearWeapon) {
                            boolean shouldSwing = ((IBattlegearWeapon) offhandItem.getItem()).offhandClickAir(event, mainHandItem, offhandItem);

                            if (shouldSwing) {
                                event.entityPlayer.swingOffItem();
                                Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                            }

                        } else {
                            event.entityPlayer.swingOffItem();
                            Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                        }
                        break;
                    } else {
                        break;
                    }
            }
        }

    }

    @ForgeSubscribe
    public void playerIntereactEntity(EntityInteractEvent event) {

        if (event.entityPlayer.isBattlemode()) {

            ItemStack mainHandItem = event.entityPlayer.getCurrentEquippedItem();
            ItemStack offhandItem = event.entityPlayer.inventory.getStackInSlot(event.entityPlayer.inventory.currentItem + 3);

            if (offhandItem != null && offhandItem.getItem() instanceof IBattlegearWeapon) {

                if(mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem.itemID)){
                    OffhandAttackEvent offAttackEvent = new OffhandAttackEvent(event);

                    ((IBattlegearWeapon) offhandItem.getItem()).offhandAttackEntity(offAttackEvent, mainHandItem, offhandItem);

                    if (offAttackEvent.swingOffhand) {
                        event.entityPlayer.swingOffItem();
                        Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                    }

                    if (offAttackEvent.shouldAttack) {
                        event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
                    }
                }

            } else{
                if(mainHandItem == null || BattlegearUtils.isMainHand(mainHandItem.itemID)){
                    event.setCanceled(true);
                    event.entityPlayer.swingOffItem();
                    event.entityPlayer.attackTargetEntityWithCurrentOffItem(event.target);
                    Battlegear.proxy.sendAnimationPacket(EnumBGAnimations.OffHandSwing, event.entityPlayer);
                }
            }


        }
    }

}
