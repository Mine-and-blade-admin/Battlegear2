package mods.battlegear2;

import mods.battlegear2.api.quiver.IArrowFireHandler;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.weapons.WeaponRegistry;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearPacketHandeler;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod(modid = Battlegear.MODID, version = "$version", guiFactory = "mods.battlegear2.gui.BattlegearGuiFactory")
public class Battlegear {

    public static final String MODID = "battlegear2";
    public static final String imageFolder = MODID+":textures/";
    public static final String CUSTOM_DAMAGE_SOURCE = "battlegearExtra";

    @Mod.Instance(MODID)
    public static Battlegear INSTANCE;
    @SidedProxy(modId=MODID, clientSide = "mods.battlegear2.client.ClientProxy", serverSide = "mods.battlegear2.CommonProxy")
    public static CommonProxy proxy;

    public static ItemArmor.ArmorMaterial knightArmourMaterial;
    public static SoundEvent shieldSound;

    public static boolean battlegearEnabled = true;
    public static boolean debug = false;

	public static org.apache.logging.log4j.Logger logger;
    public static BattlegearPacketHandeler packetHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        knightArmourMaterial = EnumHelper.addArmorMaterial("knights.armour", "battlegear2:knight", 25, new int[]{3, 5, 7, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.5F);
        ResourceLocation sound = new ResourceLocation(MODID, "shield");
        shieldSound = GameRegistry.register(new SoundEvent(sound).setRegistryName(sound));
        BattlegearConfig.getConfig(new Configuration(event.getSuggestedConfigurationFile()));

        if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient()){
            try {
                Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        "https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml",
                        "https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/changelog.md"
                );
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        proxy.registerHandlers();
        //Fetch early messages
        IMCEvent imc = new IMCEvent();
        imc.applyModContainer(Loader.instance().activeModContainer());
        onMessage(imc);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        QuiverArrowRegistry.addArrowToRegistry(Items.ARROW, EntityTippedArrow.class);
        QuiverArrowRegistry.addArrowToRegistry(Items.TIPPED_ARROW, EntityTippedArrow.class);
        QuiverArrowRegistry.addArrowToRegistry(Items.SPECTRAL_ARROW, EntitySpectralArrow.class);
        proxy.registerItemRenderers();
        BattlegearConfig.registerRecipes();
        packetHandler = new BattlegearPacketHandeler();
        packetHandler.register();
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
        event.registerServerCommand(CommandWeaponWield.INSTANCE);
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
                        if(WeaponRegistry.setWeapon(message.key, stack)){
                            success = true;
                        }else if(message.key.startsWith("Arrow")){
                            Class<?> clazz = null;
                            try {
                                if(message.key.indexOf(":")>0)
                                    clazz = Class.forName(message.key.split(":")[1]);//Complete key should look like Arrow:class-path
                            } catch (Exception ignored) {
                            }
                            if(clazz!=null && EntityArrow.class.isAssignableFrom(clazz)){//The arrow entity should use EntityArrow, at least as a superclass
                                QuiverArrowRegistry.addArrowToRegistry(stack, (Class<? extends EntityArrow>) clazz);
                                success = true;
                            }else{//Register with no default handling
                                QuiverArrowRegistry.addArrowToRegistry(stack);
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
                    } catch (Exception ignored) {
                    }
                }else if(message.isNBTMessage() && Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION)&& !Loader.instance().hasReachedState(LoaderState.INITIALIZATION) && BattlegearConfig.initItemFromNBT(message.getNBTValue())){
                    success = true;
                }
                if(success){
                    logger.trace("Mine&Blade:Battlegear2 successfully managed message from "+ message.getSender());
                }else{
                    logger.warn(message.getSender()+" tried to communicate with Mine&Blade:Battlegear2, but message was not supported!");
                }
            }
    	}
    }

    @Mod.EventHandler
    public void onRemapId(FMLMissingMappingsEvent event){
        for(FMLMissingMappingsEvent.MissingMapping mapping:event.get()){
            if(BattlegearConfig.remap(mapping))
                logger.warn("ReMapped: " + mapping.name);
        }
    }

    /**
     * Basic version checker, support having different build number on each side
     * @param mods the data sent from FML handshake packet
     * @param remoteParty the side that sent this data
     * @return true if we allow this to run
     */
    @NetworkCheckHandler
    public boolean checkRemote(Map<String,String> mods, Side remoteParty){
        if(mods.containsKey(MODID)){
            String remoteVersion = mods.get(MODID);
            if(remoteVersion!=null) {
                String internalVersion = FMLCommonHandler.instance().findContainerFor(this).getVersion();
                if(remoteVersion.equals(internalVersion))
                    return true;
                else{
                    internalVersion = internalVersion.substring(0, internalVersion.lastIndexOf("."));
                    remoteVersion = remoteVersion.substring(0, remoteVersion.lastIndexOf("."));
                    return remoteVersion.equals(internalVersion);
                }
            }
        }
        return false;
    }
}
