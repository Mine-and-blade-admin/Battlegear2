package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mods.battlegear2.Battlegear;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

public class BattlegearShieldFlashPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|ShieldFlash";
	private String username;
	private float damage;

    public BattlegearShieldFlashPacket(EntityPlayer player, float damage) {
    	this.username = player.username;
    	this.damage = damage;
    }

	public BattlegearShieldFlashPacket() {
	}
    
    @Override
    public void process(DataInputStream in,EntityPlayer player) {
    	try {
    		username = Packet.readString(in, 30);
            damage = in.readFloat();
		    EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(username);
		    if(targetPlayer!=null)
		    	Battlegear.proxy.startFlash(targetPlayer, damage);
    	}catch (IOException e) {
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
        Packet.writeString(username, out);
        out.writeFloat(damage);
	}
}
