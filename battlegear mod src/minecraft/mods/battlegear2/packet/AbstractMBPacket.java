package mods.battlegear2.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.battlegear2.utils.BattlegearUtils;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:35 PM
 */
public abstract class AbstractMBPacket {

	public final Packet generatePacket() {

        ByteArrayOutputStream bos = null;
        DataOutputStream outputStream = null;
        try {
            bos = new ByteArrayOutputStream();
            outputStream = new DataOutputStream(bos);

            write(outputStream);

            return new Packet250CustomPayload(getChannel(), bos.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(outputStream);
        }
        return null;
    }
	
	public abstract String getChannel();
	public abstract void write(DataOutput out) throws IOException;
    public abstract void process(Packet250CustomPayload packet, EntityPlayer player);
}
