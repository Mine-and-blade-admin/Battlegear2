package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import mods.battlegear2.WeaponHookContainerClass;
import net.minecraft.entity.player.EntityPlayer;

public final class LoginPacket extends AbstractMBPacket{
    public static final String packetName = "MB|Login";

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        if(player.world.isRemote){
            Battlegear.battlegearEnabled = true;
            WeaponHookContainerClass.INSTANCE.doBlocking = inputStream.readBoolean();
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
		out.writeBoolean(WeaponHookContainerClass.INSTANCE.doBlocking);
	}
}
