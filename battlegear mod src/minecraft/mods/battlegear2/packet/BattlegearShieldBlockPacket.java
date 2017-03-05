package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public final class BattlegearShieldBlockPacket extends AbstractMBPacket {
	public static final String packetName = "MB2|Block";
	private boolean block;
	private String username;

	public BattlegearShieldBlockPacket(boolean block, EntityPlayer user) {
		this.block = block;
		this.username = user.getCachedUniqueIdString();
	}

	public BattlegearShieldBlockPacket() {
	}

	@Override
	public void process(ByteBuf in, EntityPlayer player) {
		UUID id;
        try {
            block = in.readBoolean();
            username = ByteBufUtils.readUTF8String(in);
            id = UUID.fromString(username);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if (username != null) {
            EntityPlayer entity = player.world.getPlayerEntityByUUID(id);
            if(entity!=null) {
                if (entity.world instanceof WorldServer) {
                    ((WorldServer) entity.world).getEntityTracker().sendToTrackingAndSelf(entity, this.generatePacket());
                }
				((IBattlePlayer) entity).setBlockingWithShield(block);
            }
        }
	}

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
		out.writeBoolean(block);
        ByteBufUtils.writeUTF8String(out, username);
	}
}
