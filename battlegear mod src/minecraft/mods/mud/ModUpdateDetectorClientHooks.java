package mods.mud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@SuppressWarnings("unused")
public class ModUpdateDetectorClientHooks {

    @ForgeSubscribe
    public void onEntityJoinWorld(EntityJoinWorldEvent event){

        if(Minecraft.getMinecraft().thePlayer == event.entity){
            ModUpdateDetectorTickHandeler.timer = 0;
        }

    }
}
