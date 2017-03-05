package mods.battlegear2.packet;

import mods.battlegear2.Battlegear;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Hashtable;
import java.util.Map;

public final class BattlegearPacketHandeler {

    private final Map<String, AbstractMBPacket> map = new Hashtable<String, AbstractMBPacket>();
    private final Map<String, FMLEventChannel> channels = new Hashtable<String, FMLEventChannel>();

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
        map.put(WieldSetPacket.packetName, new WieldSetPacket());
        map.put(ReachTargetPacket.packetName, new ReachTargetPacket());
    }

    public void register(){
        FMLEventChannel eventChannel;
        for(String channel:map.keySet()){
            eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channel);
            eventChannel.register(this);
            channels.put(channel, eventChannel);
        }
    }

    @SubscribeEvent
    public void onServerPacket(final FMLNetworkEvent.ServerCustomPacketEvent event) {
        final AbstractMBPacket packet = map.get(event.getPacket().channel());
        if (packet != null) {
            Battlegear.proxy.getThreadListener().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    packet.process(event.getPacket().payload(), ((NetHandlerPlayServer) event.getHandler()).playerEntity);
                }
            });
        }
    }

    @SubscribeEvent
    public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
        final AbstractMBPacket packet = map.get(event.getPacket().channel());
        if (packet != null) {
            Battlegear.proxy.getThreadListener().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    packet.process(event.getPacket().payload(), Battlegear.proxy.getClientPlayer());
                }
            });
        }
    }

    public void sendPacketToPlayer(FMLProxyPacket packet, EntityPlayerMP player){
        channels.get(packet.channel()).sendTo(packet, player);
    }

    public void sendPacketToServer(FMLProxyPacket packet){
        packet.setTarget(Side.SERVER);
        channels.get(packet.channel()).sendToServer(packet);
    }

    public void sendPacketAround(Entity entity, double range, FMLProxyPacket packet){
        channels.get(packet.channel()).sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range));
    }

    public void sendPacketToAll(FMLProxyPacket packet){
        channels.get(packet.channel()).sendToAll(packet);
    }
}
