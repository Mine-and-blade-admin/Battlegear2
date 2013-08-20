package mods.battlegear2;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.api.IShield;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;

import java.util.EnumSet;
import java.util.HashMap;

public class BattlegearTickHandeler implements ITickHandler {

    public HashMap<String, Integer> currentItemCahce = new HashMap<String, Integer>();


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {

        if (type.contains(TickType.PLAYER)) {

            EntityPlayer entityPlayer = (EntityPlayer) tickData[0];

            if (entityPlayer.worldObj instanceof WorldServer) {

                if(currentItemCahce.containsKey(entityPlayer.username)){

                }

                if(((InventoryPlayerBattle)entityPlayer.inventory).hasChanged){

                    ((WorldServer)entityPlayer.worldObj)
                        .getEntityTracker().sendPacketToAllAssociatedPlayers(
                                entityPlayer,
                            BattlegearSyncItemPacket.generatePacket(entityPlayer.username, entityPlayer.inventory)
                                );

                    entityPlayer.specialActionTimer = 0;

                }
                ((InventoryPlayerBattle)entityPlayer.inventory).hasChanged = entityPlayer.ticksExisted < 10;

                //Force update every 3 seconds
                //TODO: This is a temp fix
                if(entityPlayer.ticksExisted % (20*3) == 0){
                    ((WorldServer)entityPlayer.worldObj)
                            .getEntityTracker().sendPacketToAllAssociatedPlayers(
                            entityPlayer,
                            BattlegearSyncItemPacket.generatePacket(entityPlayer.username, entityPlayer.inventory)
                    );
                }
            }

            //If we JUST swung an Item
            if (entityPlayer.field_110158_av == 1) {
                ItemStack mainhand = entityPlayer.getCurrentEquippedItem();
                if (mainhand != null && mainhand.getItem() instanceof IExtendedReachWeapon) {
                    float extendedReach = ((IExtendedReachWeapon) mainhand.getItem()).getReachModifierInBlocks(mainhand);
                    if(extendedReach > 0){
                        MovingObjectPosition mouseOver = Battlegear.proxy.getMouseOver(0, extendedReach + 4);
                        if (mouseOver != null && mouseOver.typeOfHit == EnumMovingObjectType.ENTITY) {
                            Entity target = mouseOver.entityHit;
                            if (target instanceof EntityLiving && target != entityPlayer) {
                                if (target.hurtResistantTime != ((EntityLiving) target).maxHurtResistantTime) {
                                    FMLClientHandler.instance().getClient().playerController.attackEntity(entityPlayer, target);
                                }
                            }
                        }
                    }
                }
            }


            if(entityPlayer.specialActionTimer > 0){
                entityPlayer.specialActionTimer --;

                int targetTime = 0;

                ItemStack offhand = ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon();
                if(offhand != null && offhand.getItem() instanceof IShield){
                    targetTime = ((IShield) offhand.getItem()).getBashTimer(offhand) / 2;
                }

                if(entityPlayer.specialActionTimer == targetTime){
                    Battlegear.proxy.doSpecialAction(entityPlayer);
                }


            }

        }

    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return "battlegear.ticks";
    }

}
