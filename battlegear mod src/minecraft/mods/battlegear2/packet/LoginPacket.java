package mods.battlegear2.packet;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class LoginPacket extends AbstractMBPacket{
    public static final String packetName = "MB|Login";

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
            Battlegear.battlegearEnabled = true;
        }
    }

    public static Packet generate() {
        return new Packet250CustomPayload(packetName, new byte[0]);
    }
}
