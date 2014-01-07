package mods.battlegear2.client.gui;

import java.util.EnumSet;

import mods.battlegear2.Battlegear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;

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
                	BattleEquipGUI.open(player);
                }
                else if (kb.keyCode == openSigilEditor.keyCode) {
                    BattlegearSigilGUI.open(player);
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
