package mods.battlegear2.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;


import mods.battlegear2.client.gui.BattlegearGUITickHandeler;
import mods.battlegear2.client.keybinding.BattlegearKeyHandeler;
import mods.battlegear2.client.utils.SigilRendererTest;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.BattlegearPacketHandeler;
import mods.battlegear2.common.BattlegearTickHandeler;
import mods.battlegear2.common.CommonProxy;
import mods.battlegear2.common.utils.BattlegearConfig;
import mods.battlegear2.common.utils.EnumBGAnimations;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.StitchSlot;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{
	
	public static Icon[] icons= new Icon[1];
	
	private boolean isIcononsInitialised = false;

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
	public void registerTickHandelers(){
		
		super.registerTickHandelers();
		TickRegistry.registerTickHandler(new BattlegearGUITickHandeler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.CLIENT);
		
		MinecraftForgeClient.registerItemRenderer(BattlegearConfig.shield[2].itemID, new SigilRendererTest());
	}

	@Override
	public void setSlotIcon(Slot slot, int slotID) {
		//slot.setBackgroundIconIndex(slotIcons[slotID]);
	}

	@Override
	public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {

		if(entityPlayer instanceof EntityClientPlayerMP){
			((EntityClientPlayerMP)entityPlayer).sendQueue.addToSendQueue(
					BattlegearPacketHandeler.generateBgAnimationPacket(animation, entityPlayer.username));
		}
		
	}
	
		
	
}
