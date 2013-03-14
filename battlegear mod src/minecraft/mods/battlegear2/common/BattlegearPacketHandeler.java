package mods.battlegear2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class BattlegearPacketHandeler implements IPacketHandler {
	
	public static final String guiPackets = "MB-GUI";
	public static final String syncBattlePackets = "MB-SyncAllItems";
	public static final String mbAnimation = "MB-animation";

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		if(packet.channel.equals(syncBattlePackets)){
			processBattleItemsSync(packet, (EntityPlayer)player);
		}else if(packet.channel.equals(guiPackets)){
			processBattlegearGUIPacket(packet, (EntityPlayer)player);
		}else if (packet.channel.equals(mbAnimation)){
			processOffHandAnimationPacket(packet, (EntityPlayer)player);
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
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5120);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			for(int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++){
				Packet.writeItemStack(inventory.getStackInSlot(i+InventoryPlayerBattle.OFFSET), outputStream);
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
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int windowID = 0;
		try{

			for(int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++){
				ItemStack stack = Packet.readItemStack(inputStream);
				
				if(stack!=null){
					player.inventory.setInventorySlotContents(InventoryPlayerBattle.OFFSET+i, stack);
				}
			}
			
			
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
	}
	
	public static Packet250CustomPayload generateBgAnimationPacket(int animationID, int entityId){

		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(entityId);
			outputStream.writeInt(animationID);
		}catch (Exception ex) {
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = mbAnimation;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}
	
	private void processOffHandAnimationPacket(Packet250CustomPayload packet,
			EntityPlayer player) {
		
		BattleGear.proxy.processAnimationPacket(packet, player);
		
	}
}
