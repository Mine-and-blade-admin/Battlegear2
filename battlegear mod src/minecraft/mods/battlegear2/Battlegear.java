package mods.battlegear2;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.items.arrows.EntityEnderArrow;
import mods.battlegear2.items.arrows.EntityExplossiveArrow;
import mods.battlegear2.items.arrows.EntityFlameArrow;
import mods.battlegear2.packet.*;
import mods.battlegear2.recipies.CraftingHandeler;
import mods.battlegear2.utils.*;
import mods.mud.ModUpdateDetector;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;

import java.net.MalformedURLException;
import java.net.URL;


@Mod(modid="battlegear2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        channels = {
                BattlegearAnimationPacket.packetName,
                BattlegearSyncItemPacket.packetName,
                //BattlegearBannerPacket.packetName,
                //BattlegearChangeHeraldryPacket.packetName,
                BattlegearGUIPacket.packetName,
                BattlegearShieldBlockPacket.packetName,
                BattlegearShieldFlashPacket.packetName,
                SpecialActionPacket.packetName,
                LoginPacket.packetName},
        packetHandler = BattlegearPacketHandeler.class)
public class Battlegear {

    @Instance("battlegear2")
    public static Battlegear INSTANCE;

    @SidedProxy(clientSide = "mods.battlegear2.client.ClientProxy",
            serverSide = "mods.battlegear2.CommonProxy")
    public static CommonProxy proxy;

    public static String imageFolder = "assets/battlegear2/textures/";

    public static EnumArmorMaterial knightArmourMaterial;

    public static boolean battlegearEnabled = true;

    public static boolean debug = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Set up the Translator

        BattlegearTranslator.setup("/deobfuscation_data-" + FMLInjectionData.data()[4] + ".lzma");
        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));
        /*
        try {
            ModUpdateDetector.registerMod(
                    FMLCommonHandler.instance().findContainerFor(this),
                    new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml"),
                    new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/changelog.md")
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BattlegearConfig.registerRecipes();
	    GameRegistry.registerCraftingHandler(new CraftingHandeler());

        if(Battlegear.debug){
            QuiverArrowRegistry.addArrowToRegistry(Item.arrow.itemID, 0, EntityArrow.class);
            QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, 0, EntityExplossiveArrow.class);
            QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, 1, EntityEnderArrow.class);
            QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, 2, EntityFlameArrow.class);
        }


    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        BattlegearUtils.scanAndProcessItems();
        proxy.registerKeyHandelers();
        proxy.registerTickHandelers();
        proxy.registerItemRenderers();

        NetworkRegistry.instance().registerGuiHandler(this, new BattlegearGUIHandeler());
        GameRegistry.registerPlayerTracker(new BgPlayerTracker());


    }
}
