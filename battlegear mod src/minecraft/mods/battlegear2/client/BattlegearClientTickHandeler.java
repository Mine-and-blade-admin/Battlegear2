package mods.battlegear2.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.util.EnumSet;

public class BattlegearClientTickHandeler implements ITickHandler {


    public static short blockBar = 1000;
    public static boolean wasBlocking = false;

    public static final short recoveryRate = 10;


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player.isBattlemode() &&
                player.inventory.getStackInSlot(player.inventory.currentItem + 3) != null &&
                player.inventory.getStackInSlot(player.inventory.currentItem + 3).getItem() instanceof ItemShield){

            if(Mouse.isButtonDown(1)){

                blockBar -= 10;
                if(blockBar > 0){
                    if(!wasBlocking){

                        PacketDispatcher.sendPacketToServer(BattlegearShieldBlockPacket.generatePacket(true, player.username));
                        //Send packet
                        System.out.println("Start Blocking");
                    }
                    wasBlocking = true;
                }else{
                    if(wasBlocking){
                        //Send packet
                        PacketDispatcher.sendPacketToServer(BattlegearShieldBlockPacket.generatePacket(false, player.username));

                        System.out.println("Stop Blocking");
                    }
                    wasBlocking = false;
                    blockBar = 0;
                }
            }else{

                if(wasBlocking){
                    //send packet
                    PacketDispatcher.sendPacketToServer(BattlegearShieldBlockPacket.generatePacket(false, player.username));
                    System.out.println("Stop Blocking");
                }
                wasBlocking = false;

                blockBar += recoveryRate;
                if(blockBar > 1000){
                    blockBar = 1000;
                }

            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {

    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
