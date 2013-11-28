package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mods.battlegear2.utils.BattlegearUtils;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.WorldServer;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:47 PM
 */
public class BattlegearAnimationPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|Animation";
	private EnumBGAnimations animation;
	private String username;

    public BattlegearAnimationPacket(EnumBGAnimations animation, String username) {
    	this.animation = animation;
    	this.username = username;
    }

    public BattlegearAnimationPacket() {
	}
    
	@Override
    public void process(DataInputStream in,EntityPlayer player) {
        try {
			animation = EnumBGAnimations.values()[in.readInt()];
			username = Packet.readString(in, 16);
	        if (username != null && animation != null) {
	            EntityPlayer entity = player.worldObj.getPlayerEntityByName(username);
	            if(entity!=null){
		            if (player.worldObj instanceof WorldServer) {
		                ((WorldServer) player.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, this.generatePacket());
		            }
		            animation.processAnimation(entity);
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
		out.writeInt(animation.ordinal());
        Packet.writeString(username, out);
	}
}
