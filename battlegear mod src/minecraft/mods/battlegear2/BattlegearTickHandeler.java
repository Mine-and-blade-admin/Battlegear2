package mods.battlegear2;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
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

    public HashMap<String, Integer> currentItemCache = new HashMap<String, Integer>();


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {

        if (type.contains(TickType.PLAYER)) {

            EntityPlayer entityPlayer = (EntityPlayer) tickData[0];

            if (entityPlayer.worldObj instanceof WorldServer) {
            	/*boolean changed = false;
                if(!currentItemCache.containsKey(entityPlayer.username)||entityPlayer.inventory.currentItem!=currentItemCache.get(entityPlayer.username))
                {
                	changed = true;
                }
            	
            	((InventoryPlayerBattle)entityPlayer.inventory).hasChanged |= changed;*/

                if(((InventoryPlayerBattle)entityPlayer.inventory).hasChanged){

                    ((WorldServer)entityPlayer.worldObj)
                            .getEntityTracker().sendPacketToAllAssociatedPlayers(
                            entityPlayer,
                            BattlegearSyncItemPacket.generatePacket(entityPlayer)
                    );

                    entityPlayer.specialActionTimer = 0;

                    ((InventoryPlayerBattle)entityPlayer.inventory).hasChanged = entityPlayer.ticksExisted < 10;

                }


                //Force update every 3 seconds
                //TODO: This is a temp fix
                if(entityPlayer.ticksExisted % (20*1) == 0 && !entityPlayer.isUsingItem()){
                    ((WorldServer)entityPlayer.worldObj)
                            .getEntityTracker().sendPacketToAllAssociatedPlayers(
                            entityPlayer,
                            BattlegearSyncItemPacket.generatePacket(entityPlayer)
                    );
                }
                
                /*if(changed)
                {
                	currentItemCache.put(entityPlayer.username, entityPlayer.inventory.currentItem);
                }*/
            }
            
            //If we JUST swung an Item
            if (entityPlayer.swingProgressInt == 1) {
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
                }else if (offhand != null && offhand.getItem() instanceof IArrowContainer2){
                    targetTime = 0;
                }
                if(entityPlayer.specialActionTimer == targetTime && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
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
