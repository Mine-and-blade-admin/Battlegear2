package mods.battlegear2.packet;


import mods.battlegear2.Battlegear;
import mods.battlegear2.utils.BattlegearUtils;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import java.io.*;

public class BattlegearShieldBlockPacket extends AbstractMBPacket{
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
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        String playername = null;
        boolean block = true;

        try {
            block = inputStream.readBoolean();
            playername = Packet.readString(inputStream, 16);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            BattlegearUtils.closeStream(inputStream);
        }

        if (playername != null) {

            EntityPlayer entity = player.worldObj.getPlayerEntityByName(playername);

            if(entity!=null){
	            if (player.worldObj instanceof WorldServer) {
	                ((WorldServer) player.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, packet);
	            }
	
	            entity.setBlockingWithShield(block);
            }
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
