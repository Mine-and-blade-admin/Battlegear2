package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:35 PM
 */
public abstract class AbstractMBPacket {

	public final FMLProxyPacket generatePacket() {
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        write(buf);
        return new FMLProxyPacket(buf, getChannel());
    }
	
	public abstract String getChannel();
	public abstract void write(ByteBuf out);
	public abstract void process(ByteBuf in, EntityPlayer player);
}
