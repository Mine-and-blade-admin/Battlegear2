package mods.battlegear2.client;

import java.util.EnumSet;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.IShield;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BattlegearClientTickHandeler implements ITickHandler {


    public static float blockBar = 1;
    public static boolean wasBlocking = false;
    public static final float[] COLOUR_DEFAULT = new float[]{0, 0.75F, 1};
    public static final float[] COLOUR_RED = new float[]{1, 0.1F, 0.1F};
    public static final float[] COLOUR_YELLOW = new float[]{1, 1F, 0.1F};
    public static int flashTimer;

    public static float partialTick;


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {

        if(type.contains(TickType.PLAYER)){
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            if(!Battlegear.battlegearEnabled && ! player.worldObj.isRemote){
                Battlegear.battlegearEnabled = true;
            }

            ItemStack offhand = player.inventory.getStackInSlot(player.inventory.currentItem + 3);
            if(player.isBattlemode() &&
                    offhand != null &&
                    offhand.getItem() instanceof IShield){

                if(flashTimer == 30){
                    player.motionY = player.motionY/2;

                }

                if(flashTimer > 0){
                    flashTimer --;
                }


                if(Mouse.isButtonDown(1) && !player.isSwingInProgress){
                    blockBar -= ((IShield) offhand.getItem()).getDecayRate(offhand);
                    if(blockBar > 0){
                        if(!wasBlocking){

                            PacketDispatcher.sendPacketToServer(new BattlegearShieldBlockPacket(true, player.username).generatePacket());
                        }
                        wasBlocking = true;
                    }else{
                        if(wasBlocking){
                            //Send packet
                            PacketDispatcher.sendPacketToServer(new BattlegearShieldBlockPacket(false, player.username).generatePacket());

                        }
                        wasBlocking = false;
                        blockBar = 0;
                    }
                }else{

                    if(wasBlocking){
                        //send packet
                        PacketDispatcher.sendPacketToServer(new BattlegearShieldBlockPacket(false, player.username).generatePacket());
                    }
                    wasBlocking = false;

                    blockBar += ((IShield) offhand.getItem()).getRecoveryRate(offhand);
                    if(blockBar > 1){
                        blockBar = 1;
                    }

                }
            }



        }else if (type.contains(TickType.RENDER)){

            partialTick = (Float)tickData[0];

            if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu){
                Battlegear.battlegearEnabled = false;
            }

        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if(type.contains(TickType.PLAYER)){
            ItemStack offhand = player.inventory.getStackInSlot(player.inventory.currentItem + 3);
            if(player.isBattlemode() &&
                    offhand != null &&
                    offhand.getItem() instanceof IShield){

                if(Mouse.isButtonDown(1) && !player.isSwingInProgress && blockBar > 0){
                    player.motionX = player.motionX/5;
                    player.motionZ = player.motionZ/5;
                }

            }
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER, TickType.RENDER);
    }

    @Override
    public String getLabel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
