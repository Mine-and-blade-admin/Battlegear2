package mods.battlegear2;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.FMLEventChannel;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.quiver.IArrowFireHandler;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.weapons.CommandWeaponWield;
import mods.battlegear2.api.weapons.WeaponRegistry;
import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.packet.BattlegearPacketHandeler;
import mods.battlegear2.utils.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;

import java.net.URL;

@Mod(modid = "battlegear2", useMetadata = true, guiFactory = "mods.battlegear2.gui.BattlegearGuiFactory")
public class Battlegear {

    @Mod.Instance("battlegear2")
    public static Battlegear INSTANCE;

    @SidedProxy(modId="battlegear2", clientSide = "mods.battlegear2.client.ClientProxy", serverSide = "mods.battlegear2.CommonProxy")
    public static CommonProxy proxy;

    public static final String imageFolder = "battlegear2:textures/";
    public static final String CUSTOM_DAMAGE_SOURCE = "battlegearExtra";
    public static ItemArmor.ArmorMaterial knightArmourMaterial;

    public static boolean battlegearEnabled = true;
    public static boolean debug = false;

	public static org.apache.logging.log4j.Logger logger;
    public static BattlegearPacketHandeler packetHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Set up the Translator

        BattlegearTranslator.setup("/deobfuscation_data-" + FMLInjectionData.data()[4] + ".lzma");
        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", 25, new int[]{3, 7, 5, 3}, 15);
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient()){
            try {
                Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml"),
                        new URL("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/changelog.md")
                );
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        logger = event.getModLog();
        proxy.registerKeyHandelers();
        proxy.registerTickHandelers();
        proxy.registerItemRenderers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BattlegearConfig.registerRecipes();
        QuiverArrowRegistry.addArrowToRegistry(Items.arrow, EntityArrow.class);
        if(BattlegearConfig.MbArrows!=null){
	        for(int i = 0; i<ItemMBArrow.arrows.length; i++){
	        	QuiverArrowRegistry.addArrowToRegistry(BattlegearConfig.MbArrows, i, ItemMBArrow.arrows[i]);
	        }
        }
        packetHandler = new BattlegearPacketHandeler();
        FMLEventChannel eventChannel;
        for(String channel:packetHandler.map.keySet()){
            eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channel);
            eventChannel.register(packetHandler);
            packetHandler.channels.put(channel, eventChannel);
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new BattlegearGUIHandeler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if(Loader.isModLoaded("TConstruct")){//Tinker's Construct support for tabs in main inventory
            proxy.tryUseTConstruct();
        }
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandWeaponWield());
    }
    
    @Mod.EventHandler
    public void onMessage(IMCEvent event){
        boolean success;
    	for(IMCMessage message:event.getMessages()){
    		if(message != null){
                success = false;
                if(message.isItemStackMessage()){
                    ItemStack stack = message.getItemStackValue();
                    if(stack!=null){
                        if(message.key.equals("Dual")){
                            if(!BattlegearUtils.checkForRightClickFunction(stack.getItem(), stack)){
                                WeaponRegistry.addDualWeapon(stack);
                                success = true;
                            }
                        }else if(message.key.equals("MainHand")){
                            WeaponRegistry.addTwoHanded(stack);
                            success = true;
                        }else if(message.key.equals("OffHand")){
                            if(!BattlegearUtils.checkForRightClickFunction(stack.getItem(), stack)){
                                WeaponRegistry.addOffhandWeapon(stack);
                                success = true;
                            }
                        }else if(message.key.startsWith("Arrow:")){
                            Class<?> clazz = null;
                            try {
                                clazz = Class.forName(message.key.split(":")[1]);//Complete key should look like Arrow:class-path
                            } catch (Exception ignored) {
                            }
                            if(clazz!=null && EntityArrow.class.isAssignableFrom(clazz)){//The arrow entity should use EntityArrow, at least as a superclass
                                QuiverArrowRegistry.addArrowToRegistry(stack, (Class<? extends EntityArrow>) clazz);
                                success = true;
                            }
                        }
                    }
                }else if(message.isStringMessage()){
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(message.getStringValue());//Message should describe the full class path
                        if(clazz!=null){//The given class should implement the interface according to the key
                            if(message.key.equals("QuiverSelection") && IQuiverSelection.class.isAssignableFrom(clazz)){
                                QuiverArrowRegistry.addQuiverSelection((IQuiverSelection)clazz.newInstance());
                                success = true;
                            }else if(message.key.equals("FireHandler") && IArrowFireHandler.class.isAssignableFrom(clazz)){
                                QuiverArrowRegistry.addArrowFireHandler((IArrowFireHandler)clazz.newInstance());
                                success = true;
                            }
                        }
                    } catch (Exception logged) {
                    }
                }
                if(success){
                    logger.trace("Mine&Blade:Battlegear2 successfully managed message from "+ message.getSender());
                }else{
                    logger.warn(message.getSender()+" tried to communicate with Mine&Blade:Battlegear2, but message was not supported!");
                }
            }
    	}
    }
}
