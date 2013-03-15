package mods.battlegear2.common;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;

public class BattlegearTickHandeler implements ITickHandler{

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		if(type.contains(TickType.PLAYER)){
			
			EntityPlayer entityPlayer = (EntityPlayer) tickData[0];
			
			if(entityPlayer.worldObj instanceof WorldServer && entityPlayer.ticksExisted%5 ==0)
				((WorldServer)entityPlayer.worldObj)
				.getEntityTracker().sendPacketToAllPlayersTrackingEntity(
						entityPlayer, BattlegearPacketHandeler.generateSyncBattleItemsPacket(entityPlayer.username, entityPlayer.inventory)
						);
		}
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "battlegear.ticks";
	}
	
	

}
