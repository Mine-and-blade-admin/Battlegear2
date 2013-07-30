package mods.battlegear2.packet;

import mods.battlegear2.Battlegear;
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

    public static Packet250CustomPayload generatePacket(int equipid) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeInt(equipid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = packetName;
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        return packet;
    }


    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        int windowID;
        try {
            windowID = inputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        player.openGui(Battlegear.INSTANCE, windowID, player.worldObj, 0, 0, 0);
    }
}
