package mods.mud;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import mods.mud.gui.GuiModUpdateButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;

import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 24/08/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModUpdateDetectorTickHandeler implements ITickHandler {

    private final int timer_interval;
    protected static int timer;

    private static GuiScreen lastScreen;


    public ModUpdateDetectorTickHandeler(int timer) {
        this.timer_interval = timer;
        timer = 0;
    }


    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if(type.contains(TickType.PLAYER)){
            if(timer == 0 &&
                    tickData[0] == Minecraft.getMinecraft().thePlayer &&
                    FMLCommonHandler.instance().getMinecraftServerInstance() instanceof IntegratedServer
                    ){
                ModUpdateDetector.runUpdateChecker();
            }

            if(timer_interval > 0){
                timer = (timer+1) % timer_interval;
            }else{
                timer = -1;
            }
        }else if(type.contains(TickType.RENDER)){
            if(Minecraft.getMinecraft().currentScreen != null &&
                    //Minecraft.getMinecraft().currentScreen != lastScreen &&
                    Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu ||
                    Minecraft.getMinecraft().currentScreen instanceof GuiModList){
                int x = Minecraft.getMinecraft().currentScreen.width / 2 + 105;
                int y =  Minecraft.getMinecraft().currentScreen.height / 4 + 24 -16;
                if(Minecraft.getMinecraft().currentScreen instanceof GuiModList){
                    x = Minecraft.getMinecraft().currentScreen.width - 110;
                    y = 10;
                }

                List buttonList = getButtonList(Minecraft.getMinecraft().currentScreen);
                boolean hasMumButton = false;
                for(Object o : buttonList){
                    hasMumButton = hasMumButton || o instanceof GuiModUpdateButton;
                }
                if(!hasMumButton){
                    buttonList.add(new GuiModUpdateButton(99, x, y,125, 20, Minecraft.getMinecraft().currentScreen));
                }
                lastScreen = Minecraft.getMinecraft().currentScreen;
            }
        }
    }

    private List getButtonList(GuiScreen screen){
        return ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, screen, 3);
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        //To change body of implemented methods use File | Settings | File Templates.
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
