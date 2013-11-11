package mods.battlegear2.client.gui;

import java.util.EnumSet;

import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BattlegearGuiKeyHandler extends KeyBindingRegistry.KeyHandler{

	public static KeyBinding battleInv = new KeyBinding("Battle Inventory", Keyboard.KEY_I);
	public static KeyBinding openSigilEditor = new KeyBinding("Open Sigil Editor", Keyboard.KEY_P);

	public BattlegearGuiKeyHandler() {
		super(new KeyBinding[]{battleInv, openSigilEditor}, new boolean[]{false, false});
	}

	@Override
	public String getLabel() {
		return "BG2-GuiKeys";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if(Battlegear.battlegearEnabled){
            Minecraft mc = FMLClientHandler.instance().getClient();
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {
                EntityClientPlayerMP player = mc.thePlayer;
                if (kb.keyCode == battleInv.keyCode) {
                    //send packet to open container on server
                    PacketDispatcher.sendPacketToServer(new BattlegearGUIPacket(BattlegearGUIHandeler.equipID).generatePacket());
                    //Also open on client
                    player.openGui(
                            Battlegear.INSTANCE, BattlegearGUIHandeler.equipID, mc.theWorld,
                            (int) player.posX, (int) player.posY, (int) player.posZ);
                }
                else if (kb.keyCode == openSigilEditor.keyCode) {
                    //send packet to open container on server
                    //PacketDispatcher.sendPacketToServer(BattlegearGUIPacket.generatePacket(BattlegearGUIHandeler.sigilEditor));
                    player.openGui(
                            Battlegear.INSTANCE, BattlegearGUIHandeler.sigilEditor, mc.theWorld,
                            (int) player.posX, (int) player.posY, (int) player.posZ);
                }
            }
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
