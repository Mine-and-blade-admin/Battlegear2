package mods.mud;

import mods.mud.gui.GuiModUpdateButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
        if(event.getGui() instanceof GuiIngameMenu || event.getGui() instanceof GuiModList){
            int x = event.getGui().width / 2 + 105;
            int y = event.getGui().height / 4 + 8;
            if(event.getGui() instanceof GuiModList){
                x = event.getGui().width - 110;
                y = 10;
            }
            event.getButtonList().add(new GuiModUpdateButton(BUTTON_ID, x, y, event.getGui()));
        }
    }
}
