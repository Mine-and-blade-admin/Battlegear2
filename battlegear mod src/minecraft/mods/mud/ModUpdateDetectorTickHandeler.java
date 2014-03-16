package mods.mud;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.TickType;
import mods.mud.gui.GuiModUpdateButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;

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
    private int timer;
    private GuiScreen lastScreen;

    public ModUpdateDetectorTickHandeler(int timer) {
        this.timer_interval = timer;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        if(type.contains(TickType.PLAYER)){
            if(timer == 0){
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
                lastScreen = Minecraft.getMinecraft().currentScreen;
                List buttonList = getButtonList(lastScreen);
                if(buttonList!=null){
                    boolean hasMumButton = false;
                    for(Object o : buttonList){
                        if(o instanceof GuiModUpdateButton){
                            hasMumButton = true;
                            break;
                        }
                    }
                    if(!hasMumButton){
                        int x = lastScreen.width / 2 + 105;
                        int y = lastScreen.height / 4 + 8;
                        if(lastScreen instanceof GuiModList){
                            x = lastScreen.width - 110;
                            y = 10;
                        }
                        buttonList.add(new GuiModUpdateButton(99, x, y, lastScreen));
                    }
                }
            }
        }
    }

    private List getButtonList(GuiScreen currentScreen) {
        return ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, currentScreen, "buttonList", "field_73887_h");
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
