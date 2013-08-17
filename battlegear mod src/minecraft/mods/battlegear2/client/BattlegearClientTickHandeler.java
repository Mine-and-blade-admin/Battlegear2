package mods.battlegear2.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

import java.util.EnumSet;

public class BattlegearClientTickHandeler implements ITickHandler {


    public static float blockBar = 1;
    public static boolean wasBlocking = false;
    public static final float recoveryRate = 0.01F; //should take 5 secods to fully recover
    public static boolean isFlashing = false;
    public static final float[] COLOUR_DEFAULT = new float[]{0, 0.75F, 1};
    public static final float[] COLOUR_RED = new float[]{1, 0.1F, 0.1F};
    public static final float[] COLOUR_YELLOW = new float[]{1, 1F, 0.1F};


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ItemStack offhand = player.inventory.getStackInSlot(player.inventory.currentItem + 3);
        if(player.isBattlemode() &&
                offhand != null &&
                offhand.getItem() instanceof ItemShield){

            if(Mouse.isButtonDown(1) && !player.isSwingInProgress){

                blockBar -= ((ItemShield) offhand.getItem()).getDecayRate(offhand);
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
                if(blockBar > 1){
                    blockBar = 1;
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
