package mods.battlegear2.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:35 PM
 */
public abstract class AbstractMBPacket {

    public abstract void process(Packet250CustomPayload packet, EntityPlayer player);
}
