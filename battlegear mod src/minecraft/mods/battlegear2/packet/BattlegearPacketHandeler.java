package mods.battlegear2.packet;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.util.Hashtable;
import java.util.Map;

public class BattlegearPacketHandeler implements IPacketHandler {

    private Map<String, AbstractMBPacket> map = new Hashtable<String, AbstractMBPacket>();


    public BattlegearPacketHandeler() {

        map.put(BattlegearAnimationPacket.packetName, new BattlegearAnimationPacket());
        //map.put(BattlegearBannerPacket.packetName, new BattlegearBannerPacket());
        //map.put(BattlegearChangeHeraldryPacket.packetName, new BattlegearChangeHeraldryPacket());
        map.put(BattlegearGUIPacket.packetName, new BattlegearGUIPacket());
        map.put(BattlegearSyncItemPacket.packetName, new BattlegearSyncItemPacket());
        map.put(BattlegearShieldBlockPacket.packetName, new BattlegearShieldBlockPacket());
        map.put(BattlegearShieldFlashPacket.packetName, new BattlegearShieldFlashPacket());
        map.put(SpecialActionPacket.packetName, new SpecialActionPacket());
        map.put(LoginPacket.packetName, new LoginPacket());

    }

    @Override
    public void onPacketData(INetworkManager manager,
                             Packet250CustomPayload packet, Player player) {
        map.get(packet.channel).process(packet, (EntityPlayer) player);

    }


}
