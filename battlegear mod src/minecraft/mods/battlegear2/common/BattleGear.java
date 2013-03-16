package mods.battlegear2.common;



import mods.battlegear2.client.gui.BattlegearGUITickHandeler;
import mods.battlegear2.client.keybinding.BattlegearKeyHandeler;
import mods.battlegear2.common.gui.BattlegearGUIHandeler;
import mods.battlegear2.common.utils.BattlegearConnectionHandeler;
import mods.battlegear2.common.utils.BattlegearUtils;
import net.minecraftforge.common.MinecraftForge;


import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="MB-Battlegear2", name="Mine & Blade: Battlegear 2")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, 
	channels={
		BattlegearPacketHandeler.guiPackets,
		BattlegearPacketHandeler.syncBattlePackets,
		BattlegearPacketHandeler.mbAnimation}, 
	packetHandler =BattlegearPacketHandeler.class)
public class BattleGear {
	
	 @Instance("MB-Battlegear2")
     public static BattleGear instance;
	 
	
	@SidedProxy(clientSide="mods.battlegear2.client.ClientProxy",
			serverSide="mods.battlegear2.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static String imageFolder = "/mods/battlegear2/textures/";
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
		proxy.registerTextures();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event){
		BattlegearUtils.scanAndProcessItems();
		
		proxy.registerKeyHandelers();
		proxy.registerTickHandelers();
		
		MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
		
		NetworkRegistry.instance().registerGuiHandler(this, new BattlegearGUIHandeler());
		NetworkRegistry.instance().registerConnectionHandler(new BattlegearConnectionHandeler());
	}
	
	
}
