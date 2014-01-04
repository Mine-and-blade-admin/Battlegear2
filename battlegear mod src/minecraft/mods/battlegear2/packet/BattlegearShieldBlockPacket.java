package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.WorldServer;

public class BattlegearShieldBlockPacket extends AbstractMBPacket {
	public static final String packetName = "MB2|Block";
	private boolean block;
	private String username;

	public BattlegearShieldBlockPacket(boolean block, String username) {
		this.block = block;
		this.username = username;
	}

	public BattlegearShieldBlockPacket() {
	}

	@Override
	public void process(DataInputStream in, EntityPlayer player) {
		try {
			block = true;
			block = in.readBoolean();
			username = Packet.readString(in, 16);
			if (username != null) {
				EntityPlayer entity = player.worldObj
						.getPlayerEntityByName(username);
				if (entity instanceof IBattlePlayer) {
					if (player.worldObj instanceof WorldServer) {
						((WorldServer) player.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity,	this.generatePacket());
					}
					((IBattlePlayer) entity).setBlockingWithShield(block);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			BattlegearUtils.closeStream(in);
		}
	}

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(block);
		Packet.writeString(username, out);
	}
}
