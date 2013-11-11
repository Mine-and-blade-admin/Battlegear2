package mods.battlegear2.packet;


import java.io.DataOutput;
import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class LoginPacket extends AbstractMBPacket{
    public static final String packetName = "MB|Login";

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
            Battlegear.battlegearEnabled = true;
        }
    }

    public LoginPacket() {
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.write(new byte[0]);
	}
}
