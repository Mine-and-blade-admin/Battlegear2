package mods.battlegear2;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.QuiverArrowRegistry;
import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.items.arrows.EntityEnderArrow;
import mods.battlegear2.items.arrows.EntityExplossiveArrow;
import mods.battlegear2.packet.*;
import mods.battlegear2.recipies.CraftingHandeler;
import mods.battlegear2.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraftforge.common.MinecraftForge;

import static mods.battlegear2.utils.BattlegearUpdateChecker.*;


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

    public static boolean debug = false;


    public static Release latestRelease;
    public static boolean hasDisplayedVersionCheck;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Set up the Translator
        BattlegearTranslator.setup("/deobfuscation_data-" + FMLInjectionData.data()[4] + ".lzma");
        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));

        Thread t = new Thread(new UpdateThread(Release.EnumReleaseType.Beta));
        t.start();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BattlegearConfig.registerRecipes();
	    GameRegistry.registerCraftingHandler(new CraftingHandeler());

        QuiverArrowRegistry.addArrowToRegistry(Item.arrow.itemID, 0, EntityArrow.class);
        QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, 0, EntityExplossiveArrow.class);
        QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, 1, EntityEnderArrow.class);



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

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        if(FMLCommonHandler.instance().getSide() == Side.SERVER){
            RConConsoleSource.consoleBuffer.sendChatToPlayer(ChatMessageComponent.func_111066_d(proxy.getVersionCheckerMessage()));
        }

        event.registerServerCommand(new BattlegearCommands());
    }




    public static class UpdateThread implements Runnable{

        private String modid;
        private String minecraftVersion;
        private Release.EnumReleaseType level;

        public UpdateThread(Release.EnumReleaseType level) {
            this.level = level;
            this.minecraftVersion = Loader.instance().getMCVersionString().replaceAll("Minecraft ", "");
            ModContainer mc = FMLCommonHandler.instance().findContainerFor(INSTANCE);
            this.modid = mc.getModId();
        }

        @Override
        public void run() {

            BattlegearUpdateChecker buc = new BattlegearUpdateChecker("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml");

            latestRelease = buc.getUpToDateRelease(modid, minecraftVersion, level);
            hasDisplayedVersionCheck = false;
        }
    }
}
