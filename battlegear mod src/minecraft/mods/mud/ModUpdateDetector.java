package mods.mud;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.WeaponHookContainerClass;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModUpdateDetector {
    
    private static boolean hasInitialised = false;
    private static Map<String, UpdateEntry> updateMap;
    public static boolean hasChecked = false;
    public static boolean enabled = true;

    /*
     * The time between update checks in minutes.
     * A value <=0 will only run the updater when a player joins the world.
     */
    private static int Timer = 60*60*20;
    private static MinecraftServer server;


    public static void registerMod(ModContainer mc, URL updateXML, URL changelog){
        if(!hasInitialised){
            initialise(mc);
            hasInitialised = true;
        }

        updateMap.put(mc.getModId(), new UpdateEntry(mc, updateXML, changelog));
    }

    public static void runUpdateChecker(){


        if(server == null || server != FMLCommonHandler.instance().getMinecraftServerInstance()){
            server = FMLCommonHandler.instance().getMinecraftServerInstance();
            CommandHandler ch = (CommandHandler) server.getCommandManager();
            ch.registerCommand(new MudCommands());

        }

        if(enabled){
            ICommandSender sender = getSender();
            sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                    EnumChatFormatting.YELLOW + StatCollector.translateToLocal("mud.name") +
                    EnumChatFormatting.WHITE + ": "+StatCollector.translateToLocal("message.checking")
            ));

            Thread t = new Thread(new UpdateChecker(updateMap.values()));
            t.run();
        }

    }

    public static Collection<UpdateEntry> getAllUpdateEntries(){
        return updateMap.values();
    }

    private static void initialise(ModContainer mc) {
        updateMap = new HashMap<String, UpdateEntry>();

        Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "MUD.cfg"));

        config.load();

        Timer = config.get(Configuration.CATEGORY_GENERAL, "Update Time", 60, "The time in minutes between update checks").getInt() * 60 * 20;
        enabled = config.get(Configuration.CATEGORY_GENERAL, "Update Check Enabled", true, "Should MUD automatically check for updates").getBoolean(true);

        if(config.hasChanged()){
            config.save();
        }

        TickRegistry.registerTickHandler(new ModUpdateDetectorTickHandeler(Timer), Side.CLIENT);
    }

    public static ICommandSender getSender() {
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
            return Minecraft.getMinecraft().thePlayer;
        }else{
            return MinecraftServer.getServer();
        }
    }


    public static void notifyUpdateDone(){
        ICommandSender sender = getSender();
        if(sender != null){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                    EnumChatFormatting.YELLOW + StatCollector.translateToLocal("mud.name") +
                            EnumChatFormatting.WHITE + ": "+StatCollector.translateToLocal("message.check.done")
            ));
        }

        int outOfDateCount = 0;
        int failedCount = 0;
        for(UpdateEntry e : updateMap.values()){
            try {
                if(!e.isUpToDate()){
                    outOfDateCount ++;
                }
            } catch (Exception e1) {
                failedCount++;
            }
        }
        if(outOfDateCount > 0){
            if(sender != null){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                        String.format("%s%s %s %s",
                        EnumChatFormatting.RED, StatCollector.translateToLocal("message.you.have"),
                                outOfDateCount, StatCollector.translateToLocal("message.outdated"))
                ));
                sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                        String.format("%s%s %s %s",
                                EnumChatFormatting.RED, StatCollector.translateToLocal("message.type"),
                                "/mud", StatCollector.translateToLocal("message.to.view"))
                ));
            }
        }else{
            if(sender != null){
                sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                        String.format("%s%s",
                                EnumChatFormatting.DARK_GREEN, StatCollector.translateToLocal("message.up.to.date"))
                ));
            }
        }
        hasChecked = true;
    }
}
