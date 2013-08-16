package mods.battlegear2;


import mods.battlegear2.client.LanguageHelper;
import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.*;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;


@Mod(modid="battlegear2", name="Mine & Blade: Battlegear 2", version="0.5 dev")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        channels = {
                BattlegearAnimationPacket.packetName,
                BattlegearSyncItemPacket.packetName,
                //BattlegearBannerPacket.packetName,
                //BattlegearChangeHeraldryPacket.packetName,
                BattlegearGUIPacket.packetName,
                BattlegearShieldBlockPacket.packetName},
        packetHandler = BattlegearPacketHandeler.class)
public class Battlegear {

    @Instance("battlegear2")
    public static Battlegear INSTANCE;

    @SidedProxy(clientSide = "mods.battlegear2.client.ClientProxy",
            serverSide = "mods.battlegear2.CommonProxy")
    public static CommonProxy proxy;

    public static String imageFolder = "assets/battlegear2/textures/";

    public static EnumArmorMaterial knightArmourMaterial;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Set up the Translator
        BattlegearTranslator.setup("/deobfuscation_data-" + FMLInjectionData.data()[4] + ".lzma");

        LanguageHelper.loadAllLanguages();

        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
        //System.out.println("Config File");
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BattlegearConfig.registerRecipes();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        BattlegearUtils.scanAndProcessItems();
        proxy.registerKeyHandelers();
        proxy.registerTickHandelers();
        proxy.registerItemRenderers();

        NetworkRegistry.instance().registerGuiHandler(this, new BattlegearGUIHandeler());
    }

}
