package mods.battlegear2.client;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.EnumSet;

public class BattlegearClientTickHandeler implements ITickHandler {
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if(Mouse.isButtonDown(1)){
            Minecraft.getMinecraft().thePlayer.setBlockingWithShield(true);
        }else{
            Minecraft.getMinecraft().thePlayer.setBlockingWithShield(false);
        }
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
