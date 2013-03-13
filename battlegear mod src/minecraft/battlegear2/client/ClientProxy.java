package battlegear2.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import battlegear2.client.keybinding.BattlegearKeyHandeler;
import battlegear2.common.CommonProxy;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerKeyHandelers() {
		KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
	}
}
