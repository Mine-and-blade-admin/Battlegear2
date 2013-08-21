package mods.battlegear2;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.packet.LoginPacket;
import net.minecraft.entity.player.EntityPlayer;

public class BgPlayerTracker implements IPlayerTracker{


    @Override
    public void onPlayerLogin(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            PacketDispatcher.sendPacketToPlayer(LoginPacket.generate(), (Player)player);
    }

    @Override
    public void onPlayerLogout(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
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
