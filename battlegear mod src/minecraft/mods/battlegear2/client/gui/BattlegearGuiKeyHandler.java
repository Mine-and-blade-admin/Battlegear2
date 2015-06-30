package mods.battlegear2.client.gui;

import mods.battlegear2.Battlegear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public final class BattlegearGuiKeyHandler {

	private final KeyBinding battleInv, openSigilEditor;
    public static final BattlegearGuiKeyHandler INSTANCE = new BattlegearGuiKeyHandler();

    private BattlegearGuiKeyHandler() {
        battleInv = new KeyBinding("Battle Inventory", Keyboard.KEY_I, "key.categories.inventory");
        openSigilEditor = new KeyBinding("Open Sigil Editor", Keyboard.KEY_P, "key.categories.misc");
		ClientRegistry.registerKeyBinding(battleInv);
        ClientRegistry.registerKeyBinding(openSigilEditor);
	}

	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) {
		if(Battlegear.battlegearEnabled){
            Minecraft mc = FMLClientHandler.instance().getClient();
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {
                if (Keyboard.getEventKey() == battleInv.getKeyCode()) {
                	BattleEquipGUI.open(mc.thePlayer);
                }
                else if (Keyboard.getEventKey() == openSigilEditor.getKeyCode()) {
                    BattlegearSigilGUI.open(mc.thePlayer);
                }
            }
		}
	}

}
