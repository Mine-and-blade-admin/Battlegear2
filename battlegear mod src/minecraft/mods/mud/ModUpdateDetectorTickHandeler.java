package mods.mud;

import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import mods.mud.gui.GuiModUpdateButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.event.GuiScreenEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 24/08/13
 * Time: 4:20 PM
 */
public class ModUpdateDetectorTickHandeler {
    public static final int BUTTON_ID = 99;
    private final int timer_interval;
    private int timer;

    public ModUpdateDetectorTickHandeler(int timer) {
        this.timer_interval = timer;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
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
    public void onPostInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.gui instanceof GuiIngameMenu || event.gui instanceof GuiModList){
            int x = event.gui.width / 2 + 105;
            int y = event.gui.height / 4 + 8;
            if(event.gui instanceof GuiModList){
                x = event.gui.width - 110;
                y = 10;
            }
            event.buttonList.add(new GuiModUpdateButton(BUTTON_ID, x, y, event.gui));
        }
    }
}
