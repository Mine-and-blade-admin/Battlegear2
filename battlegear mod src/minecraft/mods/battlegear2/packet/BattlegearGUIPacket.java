package mods.battlegear2.packet;

import mods.battlegear2.Battlegear;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

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
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        int windowID = -1;
        try {
            windowID = inputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            BattlegearUtils.closeStream(inputStream);
        }

        if(windowID != -1){
            player.openGui(Battlegear.INSTANCE, windowID, player.worldObj, 0, 0, 0);
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
