package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import mods.battlegear2.packet.LoginPacket;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class BgPlayerTracker {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP){
            Battlegear.packetHandler.sendPacketToPlayer(new LoginPacket().generatePacket(), (EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            Battlegear.battlegearEnabled = false;
    }
}
