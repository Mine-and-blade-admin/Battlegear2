package mods.battlegear2.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.world.WorldServer;

public class CommonProxy {

	public void registerKeyHandelers(){}

	public void setSlotIcon(Slot weaponSlot, int slotID) {}

	public void registerTextures() {}

	public void syncBattleItems(EntityPlayer entityPlayer) {
		
		((WorldServer)entityPlayer.worldObj)
			.getEntityTracker().sendPacketToAllPlayersTrackingEntity(
					entityPlayer, BattlegearPacketHandeler.generateSyncBattleItemsPacket(entityPlayer.inventory)
					);
		
	}

	public void processAnimationPacket(Packet250CustomPayload packet, EntityPlayer entityPlayer) {
		if(entityPlayer.worldObj instanceof WorldServer){
			System.out.println("Re-distribute");
			
			((WorldServer)entityPlayer.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(
					entityPlayer, packet);
		}
		
	}

	public void sendAnimationPacket(int i, EntityPlayer entityPlayer) {
		processAnimationPacket(BattlegearPacketHandeler.generateBgAnimationPacket(i, entityPlayer.entityId), entityPlayer);
	}


}
