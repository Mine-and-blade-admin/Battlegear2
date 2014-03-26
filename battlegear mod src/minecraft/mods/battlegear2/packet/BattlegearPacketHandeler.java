package mods.battlegear2.packet;

import java.util.Hashtable;
import java.util.Map;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

public class BattlegearPacketHandeler {

    public Map<String, AbstractMBPacket> map = new Hashtable<String, AbstractMBPacket>();
    public Map<String, FMLEventChannel> channels = new Hashtable<String, FMLEventChannel>();

    public BattlegearPacketHandeler() {
        map.put(BattlegearSyncItemPacket.packetName, new BattlegearSyncItemPacket());
        map.put(BattlegearAnimationPacket.packetName, new BattlegearAnimationPacket());
        map.put(BattlegearBannerPacket.packetName, new BattlegearBannerPacket());
        map.put(BattlegearChangeHeraldryPacket.packetName, new BattlegearChangeHeraldryPacket());
        map.put(BattlegearGUIPacket.packetName, new BattlegearGUIPacket());
        map.put(BattlegearShieldBlockPacket.packetName, new BattlegearShieldBlockPacket());
        map.put(BattlegearShieldFlashPacket.packetName, new BattlegearShieldFlashPacket());
        map.put(SpecialActionPacket.packetName, new SpecialActionPacket());
        map.put(LoginPacket.packetName, new LoginPacket());
        map.put(OffhandPlaceBlockPacket.packetName, new OffhandPlaceBlockPacket());
        map.put(PickBlockPacket.packetName, new PickBlockPacket());
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        map.get(event.packet.channel()).process(event.packet.payload(), ((NetHandlerPlayServer)event.handler).playerEntity);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        map.get(event.packet.channel()).process(event.packet.payload(), Battlegear.proxy.getClientPlayer());
    }

    public void sendPacketToPlayer(FMLProxyPacket packet, EntityPlayerMP player){
        channels.get(packet.channel()).sendTo(packet, player);
    }

    public void sendPacketToServer(FMLProxyPacket packet){
        channels.get(packet.channel()).sendToServer(packet);
    }

    public void sendPacketAround(Entity entity, double range, FMLProxyPacket packet){
        channels.get(packet.channel()).sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range));
    }

    public void sendPacketToAll(FMLProxyPacket packet){
        channels.get(packet.channel()).sendToAll(packet);
    }
}
