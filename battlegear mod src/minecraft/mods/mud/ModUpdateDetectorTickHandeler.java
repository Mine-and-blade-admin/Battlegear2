package mods.mud;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mods.mud.gui.GuiModUpdateButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 24/08/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModUpdateDetectorTickHandeler {

    private final int timer_interval;
    private int timer;
    private GuiScreen lastScreen;

    public ModUpdateDetectorTickHandeler(int timer) {
        this.timer_interval = timer;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            if(timer == 0){
                ModUpdateDetector.runUpdateChecker();
            }

            if(timer_interval > 0){
                timer = (timer+1) % timer_interval;
            }else{
                timer = -1;
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.Phase.START){
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
        try{
            return ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, currentScreen, "buttonList", "field_146292_n");
        }catch (Exception e){
            return null;
        }
    }
}
