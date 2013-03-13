package battlegear2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class BattlegearPacketHandeler implements IPacketHandler {
	
	public static final String guiPackets = "BattlegearGUI";

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		if(packet.channel.equals(guiPackets)){
			processBattlegearGUIPacket(packet, (EntityPlayer)player);
		}
		
	}

	private void processBattlegearGUIPacket(Packet250CustomPayload packet,
			EntityPlayer player) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int windowID = 0;
		try{
			windowID = inputStream.readInt();
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
		player.openGui(BattleGear.instance, windowID, player.worldObj, 0, 0, 0);
	}
	
	
	public static Packet250CustomPayload generateGUIPacket(int equipid) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
		        outputStream.writeInt(equipid);
		}catch (Exception ex) {
	        ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = guiPackets;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}
}
