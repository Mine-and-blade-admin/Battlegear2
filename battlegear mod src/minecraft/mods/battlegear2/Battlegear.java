package mods.battlegear2;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.weapons.WeaponRegistry;
import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.packet.*;
import mods.battlegear2.recipies.CraftingHandeler;
import mods.battlegear2.utils.*;
import mods.mud.ModUpdateDetector;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import java.net.URL;
import java.util.logging.Logger;

@Mod(modid="battlegear2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
        channels = {
                BattlegearAnimationPacket.packetName,
                BattlegearSyncItemPacket.packetName,
                BattlegearBannerPacket.packetName,
                BattlegearChangeHeraldryPacket.packetName,
                BattlegearGUIPacket.packetName,
                BattlegearShieldBlockPacket.packetName,
                BattlegearShieldFlashPacket.packetName,
                SpecialActionPacket.packetName,
                LoginPacket.packetName,
                OffhandPlaceBlockPacket.packetName
        },
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

	public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Set up the Translator

        BattlegearTranslator.setup("/deobfuscation_data-" + FMLInjectionData.data()[4] + ".lzma");
        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient()){
            try {
                ModUpdateDetector.registerMod(
                        FMLCommonHandler.instance().findContainerFor(this),
                        new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml"),
                        new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/changelog.md")
                );
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BattlegearConfig.registerRecipes();
	    GameRegistry.registerCraftingHandler(new CraftingHandeler());
        QuiverArrowRegistry.addArrowToRegistry(Item.arrow.itemID, 0, EntityArrow.class);
        if(BattlegearConfig.MbArrows!=null){
	        for(int i = 0; i<ItemMBArrow.arrows.length; i++){
	        	QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows.itemID, i, ItemMBArrow.arrows[i]);
	        }
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

        if(Loader.isModLoaded("TConstruct")){//Tinker's Construct support for tabs in main inventory
            proxy.tryUseTConstruct();
        }
    }
    
    @EventHandler
    public void onMessage(IMCEvent event){
    	for(IMCMessage message:event.getMessages()){
    		if(message != null){
                if(message.isItemStackMessage()){
                    ItemStack stack = message.getItemStackValue();
                    if(stack!=null){
                        if(message.key.equals("Dual")){
                            WeaponRegistry.addDualWeapon(stack);
                            continue;
                        }else if(message.key.equals("MainHand")){
                            WeaponRegistry.addTwoHanded(stack);
                            continue;
                        }else if(message.key.equals("OffHand")){
                            WeaponRegistry.addOffhandWeapon(stack);
                            continue;
                        }else if(message.key.startsWith("Arrow:")){
                            Class<?> clazz = null;
                            try {
                                clazz = Class.forName(message.key.split(":")[1]);//Complete key should look like Arrow:class-path
                            } catch (ClassNotFoundException ignored) {
                            }
                            if(clazz!=null && EntityArrow.class.isAssignableFrom(clazz)){//The arrow entity should use EntityArrow, at least as a superclass
                                QuiverArrowRegistry.addArrowToRegistry(stack, (Class<? extends EntityArrow>) clazz);
                                continue;
                            }
                        }
                    }
                    logger.warning("Mod "+message.getSender()+" tried to communicate with Mine&Blade:Battlegear2, but message was not supported!");
                }else if(message.key.equals("QuiverSelection") && message.isStringMessage()){
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(message.getStringValue());//Message should describe the full class path
                        if(clazz!=null && IQuiverSelection.class.isAssignableFrom(clazz)){//The given class should implement our interface
                            QuiverArrowRegistry.addQuiverSelection((IQuiverSelection)clazz.newInstance());
                        }
                    } catch (Exception logged) {
                        logger.warning("Mod "+message.getSender()+" tried to communicate with Mine&Blade:Battlegear2, but message was not supported!");
                    }
                }
            }
    	}
    }
}
