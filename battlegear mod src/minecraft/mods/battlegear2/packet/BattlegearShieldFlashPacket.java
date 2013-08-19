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

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        try {
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            float damage = inputStream.readFloat();

            Battlegear.proxy.startFlash(targetPlayer, damage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Packet250CustomPayload generatePacket(EntityPlayer player, float damage) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(300);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            Packet.writeString(player.username, outputStream);
            outputStream.writeFloat(damage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload(packetName, bos.toByteArray());

        return packet;
    }
}
