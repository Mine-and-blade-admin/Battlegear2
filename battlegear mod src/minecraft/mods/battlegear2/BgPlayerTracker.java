package mods.battlegear2;

import mods.battlegear2.packet.LoginPacket;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class BgPlayerTracker implements IPlayerTracker{

    @Override
    public void onPlayerLogin(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer()){
            PacketDispatcher.sendPacketToPlayer(new LoginPacket().generatePacket(), (Player)player);
        }
    }

    @Override
    public void onPlayerLogout(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            Battlegear.battlegearEnabled = false;
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
