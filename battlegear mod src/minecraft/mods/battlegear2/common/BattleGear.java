package mods.battlegear2.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.security.cert.Certificate;
import java.security.cert.X509Certificate;

import mods.battlegear2.common.gui.BattlegearGUIHandeler;
import mods.battlegear2.common.heraldry.HeraldricWeaponRecipie;
import mods.battlegear2.common.items.ItemHeradryIcon;
import mods.battlegear2.common.utils.BattlegearConfig;
import mods.battlegear2.common.utils.BattlegearConnectionHandeler;
import mods.battlegear2.common.utils.BattlegearUtils;
import mods.battlegear2.common.utils.JarVerifier;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="MB-Battlegear2", name="Mine & Blade: Battlegear 2", version="HeraldPrev - 0.3.1")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, 
	channels={
		BattlegearPacketHandeler.guiPackets,
		BattlegearPacketHandeler.syncBattlePackets,
		BattlegearPacketHandeler.mbAnimation,
		BattlegearPacketHandeler.guiHeraldryIconChange,
		BattlegearPacketHandeler.bannerUpdate}, 
	packetHandler =BattlegearPacketHandeler.class)
public class BattleGear {
	
	 @Instance("MB-Battlegear2")
     public static BattleGear instance;
	 
	 public static final boolean debug = false;
	 
	 public static EnumArmorMaterial knightArmourMaterial;
	 
	
	@SidedProxy(clientSide="mods.battlegear2.client.ClientProxy",
			serverSide="mods.battlegear2.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static String imageFolder = "/mods/battlegear2/textures/";
	
	private static final int[] ForgeMinVersion = new int[]{7,8,0,704};
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
		instance = this;
		//Knights armour is not as durable as diamond, provides 2 armour points less protection and is more enchantable
		knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
		
		BattlegearConfig.getConfig(event);
        BattlegearConfig.registerRecipes();       
	}
	
	@cpw.mods.fml.common.Mod.Init
	public void Init(FMLInitializationEvent event){
		
		jarCheck();
		
		if(ForgeVersion.getMajorVersion() != ForgeMinVersion[0] ||
				ForgeVersion.getMinorVersion() != ForgeMinVersion[1] || 
				ForgeVersion.getRevisionVersion() < ForgeMinVersion[2] ||
				(ForgeVersion.getBuildVersion() < ForgeMinVersion[3] &&
						ForgeVersion.getRevisionVersion() == ForgeMinVersion[2])){
			proxy.throwDependencyError(new String[]{
					String.format("%c Minecraft Forge %d.%d.%d.%d or higher", 0x2022, ForgeMinVersion[0],  ForgeMinVersion[1], ForgeMinVersion[2], ForgeMinVersion[3])
					});
		}
	}
	
	private void jarCheck() {
		
		ModContainer container = (ModContainer)FMLCommonHandler.instance().findContainerFor(instance);
		File source = container.getSource();
		
		System.out.println(source.getName());
		System.out.println(String.format("%s (%s).jar", "M&B Battlegear 2 - Mod", container.getVersion()));
		
		
		if(!source.getName().endsWith(String.format("%s (%s).jar", "M&B Battlegear 2 - Mod", container.getVersion()))){
			if(!source.getName().equals("bin")){
				proxy.throwError("Mine & Blade 2 Consistency Check Failed", "Invalid name found");
			}
		}else{
			try {
				JarFile jar = new JarFile(source);
				
				JarVerifier.verify(jar, new X509Certificate[0]);
				
			} catch (Exception e) {
				proxy.throwError("Mine & Blade 2 Consistency Check Failed", e.getMessage());
			}
		}
	}	

	@PostInit
	public void postInit(FMLPostInitializationEvent event){
		BattlegearUtils.scanAndProcessItems();
		
		
		proxy.registerKeyHandelers();
		NetworkRegistry.instance().registerGuiHandler(this, new BattlegearGUIHandeler());
		
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
		
		if(debug){
			proxy.registerTickHandelers();
			MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
			NetworkRegistry.instance().registerConnectionHandler(new BattlegearConnectionHandeler());
		}
	}
	
	
}
