package mods.battlegear2.common;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.common.utils.EnumBGAnimations;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;

public class CommonProxy {

	public void registerKeyHandelers(){}

	public void registerTextures(Object iconRegister) {
		
	}

	public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

	public void registerTickHandelers() {
		TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.SERVER);
	}

	public MovingObjectPosition getMouseOver(float tickPart, float maxDist) {
		return null;
	}

	public void attackCreatureWithItem(EntityPlayer entityPlayer, Entity target) {}

	public Icon getBackgroundIcon(int i) {
		return null;
	}

}
