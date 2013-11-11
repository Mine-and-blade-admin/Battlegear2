package mods.battlegear2.packet;

import mods.battlegear2.Battlegear;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.DamageSource;

import java.io.*;

public class BattlegearShieldFlashPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|ShieldFlash";
	private String username;
	private float damage;

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = null;

        try {
            inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            float damage = inputStream.readFloat();
            if(targetPlayer!=null)
            	Battlegear.proxy.startFlash(targetPlayer, damage);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }

    public BattlegearShieldFlashPacket(EntityPlayer player, float damage) {
    	this.username = player.username;
    	this.damage = damage;
    }

	public BattlegearShieldFlashPacket() {
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
