package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.player.EntityPlayer;

public class LoginPacket extends AbstractMBPacket{
    public static final String packetName = "MB|Login";

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        if(player.worldObj.isRemote){
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
	public void write(ByteBuf out) {
		out.writeBytes(new byte[0]);
	}
}
