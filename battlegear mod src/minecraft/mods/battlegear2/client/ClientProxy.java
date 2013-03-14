package mods.battlegear2.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


import mods.battlegear2.client.keybinding.BattlegearKeyHandeler;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.BattlegearPacketHandeler;
import mods.battlegear2.common.CommonProxy;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texture.StitchSlot;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class ClientProxy extends CommonProxy{
	
	public Icon[] slotIcons= new Icon[2];

	public static RenderEngine renderEngine = FMLClientHandler.instance().getClient().renderEngine;
	
	@Override
	public void registerTextures(){
		/*
		 * TODO: I find out how to do something about the slotIcons
		 * I did get it working by using the IconRegister in an items method,
		 * may have to wait until we implement items and register then.
		 */
	}

	@Override
	public void registerKeyHandelers() {
		KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
	}

	@Override
	public void setSlotIcon(Slot slot, int slotID) {
		slot.setBackgroundIconIndex(slotIcons[slotID]);
	}

	@Override
	public void syncBattleItems(EntityPlayer entityPlayer) {}

	@Override
	public void processAnimationPacket(Packet250CustomPayload packet,
			EntityPlayer entityPlayer) {
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int animationID = 0;
		int playerEntID = 0;
		try{
			playerEntID = inputStream.readInt();
			animationID = inputStream.readInt();
		}catch (IOException e) {
            e.printStackTrace();
            return;
		}
		
		Entity targetEntity = entityPlayer.worldObj.getEntityByID(playerEntID);
		if(targetEntity instanceof EntityPlayer){
			if(animationID == 1){
				((EntityPlayer)targetEntity).swingOffItem();
			}
		}

	}

	@Override
	public void sendAnimationPacket(int i, EntityPlayer entityPlayer) {
		if(entityPlayer instanceof EntityClientPlayerMP){
			((EntityClientPlayerMP)entityPlayer).sendQueue.addToSendQueue(BattlegearPacketHandeler.generateBgAnimationPacket(i, entityPlayer.entityId));
		}
	}
	
		
	
}
