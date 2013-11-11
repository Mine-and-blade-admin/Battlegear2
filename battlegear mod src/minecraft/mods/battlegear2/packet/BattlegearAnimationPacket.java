package mods.battlegear2.packet;

import mods.battlegear2.utils.BattlegearUtils;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import java.io.*;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:47 PM
 */
public class BattlegearAnimationPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|Animation";
	private Enum<EnumBGAnimations> animation;
	private String username;

    public BattlegearAnimationPacket(EnumBGAnimations animation, String username) {
    	this.animation = animation;
    	this.username = username;
    }

    public BattlegearAnimationPacket() {
	}

	@Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        String playername = null;
        EnumBGAnimations animation = null;
        try {
            animation = EnumBGAnimations.values()[inputStream.readInt()];
            playername = Packet.readString(inputStream, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            BattlegearUtils.closeStream(inputStream);
        }

        if (playername != null && animation != null) {

            EntityPlayer entity = player.worldObj.getPlayerEntityByName(playername);

            if(entity!=null){
	            if (player.worldObj instanceof WorldServer) {
	                ((WorldServer) player.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, packet);
	            }
	
	            animation.processAnimation(entity);
			}
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(animation.ordinal());
        Packet.writeString(username, out);
	}
}
