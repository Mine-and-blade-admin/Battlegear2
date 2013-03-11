package battlegear2.common;

import battlegear2.client.keybinding.BattlegearKeyHandeler;
import battlegear2.common.utils.BattlegearUtils;


import net.minecraftforge.common.MinecraftForge;


import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;

@Mod(modid="MB-Battlegear2", name="Mine & Blade: Battlegear 2")
public class BattleGear {
	
	@SidedProxy(clientSide="battlegear2.client.ClientProxy",
			serverSide="battlegear2.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static String imageFolder = "/battlegear2/client/images/";
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event){
		BattlegearUtils.scanAndProcessItems();
		
		
		proxy.registerKeyHandelers();
		
		MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
		
	}
	
	
}
