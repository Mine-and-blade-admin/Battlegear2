package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public final class BattlegearShieldBlockPacket extends AbstractMBPacket {
	public static final String packetName = "MB2|Block";
	private boolean block;
	private String username;

	public BattlegearShieldBlockPacket(boolean block, EntityPlayer user) {
		this.block = block;
		this.username = user.getName();
	}

	public BattlegearShieldBlockPacket() {
	}

	@Override
	public void process(ByteBuf in, EntityPlayer player) {
        try {
            block = in.readBoolean();
            username = ByteBufUtils.readUTF8String(in);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if (username != null) {
            EntityPlayer entity = player.worldObj.getPlayerEntityByName(username);
            if(entity!=null) {
                if (entity.worldObj instanceof WorldServer) {
                    ((WorldServer) entity.worldObj).getEntityTracker().func_151248_b(entity, this.generatePacket());
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
