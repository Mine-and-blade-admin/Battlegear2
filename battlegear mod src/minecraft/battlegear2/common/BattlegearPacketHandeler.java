package battlegear2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class BattlegearPacketHandeler implements IPacketHandler {
	
	public static final String guiPackets = "MB-GUI";
	public static final String syncBattlePackets = "MB-SyncAllItems";

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		if(packet.channel.equals(guiPackets)){
			processBattlegearGUIPacket(packet, (EntityPlayer)player);
		}else if(packet.channel.equals(syncBattlePackets)){
			processBattleItemsSync(packet, (EntityPlayer)player);
		}
		
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
	
	private void processBattlegearGUIPacket(Packet250CustomPayload packet, EntityPlayer player) {
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
	
	public static Packet250CustomPayload generateSyncBattleItemsPacket(InventoryPlayer inventory){
		System.out.println("Create Sync Packet");
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5120);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			for(int i = 0; i < InventoryPlayer.MAXSIZE; i++){
				Packet.writeItemStack(inventory.getStackInSlot(i+InventoryPlayer.offset), outputStream);
			}
		}catch (Exception ex) {
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = syncBattlePackets;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}
	
	private void processBattleItemsSync(Packet250CustomPayload packet, EntityPlayer player){
		System.out.println("Sync Items");
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int windowID = 0;
		try{

			for(int i = 0; i < InventoryPlayer.MAXSIZE; i++){
				ItemStack stack = Packet.readItemStack(inputStream);
				
				if(stack!=null){
					player.inventory.setInventorySlotContents(InventoryPlayer.offset+i, stack);
				}
			}
			
			
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
	}
}
