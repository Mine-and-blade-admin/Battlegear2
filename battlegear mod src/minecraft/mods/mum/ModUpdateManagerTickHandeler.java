package mods.mum;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 24/08/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModUpdateManagerTickHandeler implements ITickHandler {

    private final int timer_interval;
    private int timer;


    public ModUpdateManagerTickHandeler(int timer) {
        this.timer_interval = timer;
        timer = 0;
    }


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {

        if(timer == 0 &&
                tickData[0] == Minecraft.getMinecraft().thePlayer &&
                FMLCommonHandler.instance().getMinecraftServerInstance() instanceof IntegratedServer
                ){
            ModUpdateManager.runUpdateChecker();
        }

        if(timer_interval > 0){
            timer = (timer+1) % timer_interval;
        }else{
            timer = -1;
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        //To change body of implemented methods use File | Settings | File Templates.
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
