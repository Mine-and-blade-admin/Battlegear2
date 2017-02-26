package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.player.EntityPlayer;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:38 PM
 * Packet for opening player GUI depending on id
 */
public final class BattlegearGUIPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|GUI";

    public BattlegearGUIPacket(int equipid) {
        this.equipid = equipid;
    }

	public BattlegearGUIPacket() {
	}

	private int equipid = -1;
	
    @Override
    public void process(ByteBuf in, EntityPlayer player) {
		equipid = in.readInt();
        if(equipid != -1){
            player.openGui(Battlegear.INSTANCE, equipid, player.world, 0, 0, 0);
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(equipid);
	}
}
