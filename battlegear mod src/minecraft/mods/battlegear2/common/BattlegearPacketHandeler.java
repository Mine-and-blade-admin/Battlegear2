package mods.battlegear2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


import mods.battlegear2.client.utils.HeraldryItemRenderer;
import mods.battlegear2.common.gui.ContainerHeraldry;
import mods.battlegear2.common.inventory.InventoryPlayerBattle;
import mods.battlegear2.common.utils.BattlegearUtils;
import mods.battlegear2.common.utils.EnumBGAnimations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
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
	public static final String guiHeraldryIconChange = "MB-HeraldChange";

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		
		if(packet.channel.equals(syncBattlePackets)){
			processBattleItemsSync(packet, (EntityPlayer)player);
		}else if(packet.channel.equals(guiPackets)){
			processBattlegearGUIPacket(packet, (EntityPlayer)player);
		}else if (packet.channel.equals(mbAnimation)){
			processOffHandAnimationPacket(packet, ((EntityPlayer)player).worldObj);
		}else if(packet.channel.equals(guiHeraldryIconChange)){
			processHeraldryChangePacket(packet, (EntityPlayer)player);
		}
		
	}
	
	public static Packet250CustomPayload generateHeraldryChangeGUIPacket(int code, EntityPlayer player){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(player.openContainer.windowId);
			outputStream.writeInt(code);
		}catch (Exception ex) {
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = guiHeraldryIconChange;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}

	private void processHeraldryChangePacket(Packet250CustomPayload packet, EntityPlayer player) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int windowID = 0;
		int code = 0;
		try{
			windowID = inputStream.readInt();
			code = inputStream.readInt();
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
		System.out.println(player.openContainer.windowId +", "+ windowID);
		
		if(player.openContainer.windowId == windowID &&
				player.openContainer.isPlayerNotUsingContainer(player)){
			
			((ContainerHeraldry)player.openContainer).setCode(code);
			player.openContainer.detectAndSendChanges();
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
	
	public static Packet250CustomPayload generateSyncBattleItemsPacket(String user, InventoryPlayer inventory){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(5120);
		DataOutputStream outputStream = new DataOutputStream(bos);
		
		try {
			Packet.writeString(user, outputStream);
			outputStream.writeInt(inventory.currentItem);
			Packet.writeItemStack(inventory.getCurrentItem(), outputStream);
			
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
		
		try{
			EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
			
			targetPlayer.inventory.currentItem = inputStream.readInt();
			BattlegearUtils.setPlayerCurrentItem(targetPlayer, Packet.readItemStack(inputStream));
			
			for(int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++){
				ItemStack stack = Packet.readItemStack(inputStream);
				
				if(stack!=null){
					targetPlayer.inventory.setInventorySlotContents(InventoryPlayerBattle.OFFSET+i, stack);
				}
			}
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
	}
	
	public static Packet250CustomPayload generateBgAnimationPacket(EnumBGAnimations animation, String username){

		ByteArrayOutputStream bos = new ByteArrayOutputStream(300);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(animation.ordinal());
			Packet.writeString(username, outputStream);
			
		}catch (Exception ex) {
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = mbAnimation;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		return packet;
	}
	
	private void processOffHandAnimationPacket(Packet250CustomPayload packet, World world) {
				
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		String playername = null;
		EnumBGAnimations animation = null;
		try{
			animation = EnumBGAnimations.values()[inputStream.readInt()];
			playername = Packet.readString(inputStream, 16);
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}

		if(playername != null && animation != null){
			
			EntityPlayer entity = world.getPlayerEntityByName(playername);
			
		
				
				if(world instanceof WorldServer){
					((WorldServer)world).getEntityTracker().sendPacketToAllPlayersTrackingEntity(entity, packet);
				}
				
				
				animation.processAnimation(entity);
		}
	}
}
