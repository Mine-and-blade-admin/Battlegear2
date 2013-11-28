package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mods.battlegear2.Battlegear;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:38 PM
 * TODO: Add discription
 */
public class BattlegearGUIPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|GUI";

    public BattlegearGUIPacket(int equipid) {
        this.equipid = equipid;
    }

	public BattlegearGUIPacket() {
	}

	private int equipid;
	
    @Override
    public void process(DataInputStream in, EntityPlayer player) {
    	try {
		equipid = -1;
		equipid = in.readInt();
        if(equipid != -1){
            player.openGui(Battlegear.INSTANCE, equipid, player.worldObj, 0, 0, 0);
        }
    	}catch(IOException e){
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
		out.writeInt(equipid);
	}
}
