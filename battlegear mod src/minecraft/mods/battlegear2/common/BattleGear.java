package mods.battlegear2.common;



import mods.battlegear2.common.gui.BattlegearGUIHandeler;
import mods.battlegear2.common.utils.BattlegearConfig;
import mods.battlegear2.common.utils.BattlegearConnectionHandeler;
import mods.battlegear2.common.utils.BattlegearUtils;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="MB-Battlegear2", name="Mine & Blade: Battlegear 2", version="dev")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, 
	channels={
		BattlegearPacketHandeler.guiPackets,
		BattlegearPacketHandeler.syncBattlePackets,
		BattlegearPacketHandeler.mbAnimation,
		BattlegearPacketHandeler.guiHeraldryIconChange}, 
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
		instance = this;
		BattlegearConfig.getConfig(event);
        BattlegearConfig.registerRecipes();       
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event){
		BattlegearUtils.scanAndProcessItems();
		
		proxy.registerKeyHandelers();
		proxy.registerTickHandelers();
		
		MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
		
		NetworkRegistry.instance().registerGuiHandler(this, new BattlegearGUIHandeler());
		NetworkRegistry.instance().registerConnectionHandler(new BattlegearConnectionHandeler());
		
		
		for (Item item : Item.itemsList) {
			if(item != null){
				
				if(item.itemID == Item.swordWood.itemID ||
						item.itemID == Item.swordStone.itemID ||
						item.itemID == Item.swordIron.itemID ||
						item.itemID == Item.swordDiamond.itemID ||
						item.itemID == Item.swordGold.itemID){
					
					GameRegistry.addRecipe(new HeraldricWeaponRecipie(item));
				}
				
			}
		}
		
	}
	
	
}
