package mods.mud;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MUD is a client side only utility for mods to send automated report for updates and changelog
 */
public class ModUpdateDetector {
    public static Logger logger = LogManager.getLogger("M.U.D");
    private static boolean hasInitialised = false;
    private static Map<String, UpdateEntry> updateMap;
    public static boolean hasChecked = false;
    private static Configuration config;
    private static Property check;
    public static boolean enabled = true, verbose = false;
    private static ICommandSender sender = null;

    /**
     * The main registration method for a mod
     * @param mc The FML wrapper for a mod, you can get it with {@link FMLCommonHandler#findContainerFor(Object)}
     * @param updateXML An expected url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog An expected url for a file containing text to describe any changes, can be null
     */
    public static void registerMod(ModContainer mc, URL updateXML, URL changelog){
        if(!hasInitialised){
            initialise();
            hasInitialised = true;
        }
        updateMap.put(mc.getModId(), new UpdateEntry(mc, updateXML, changelog));
    }

    /**
     * Helper registration method for a mod
     * @param mc The FML wrapper for a mod, you can get it with {@link FMLCommonHandler#findContainerFor(Object)}
     * @param updateXML String that can be converted as an url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog String that can be converted as an url for a file containing text to describe any changes, can be null
     * @throws MalformedURLException If no known protocol is found, or <tt>updateXML</tt> is <tt>null</tt>.
     */
    public static void registerMod(ModContainer mc, String updateXML, String changelog) throws MalformedURLException {
        registerMod(mc, new URL(updateXML), changelog!=null?new URL(changelog):null);
    }

    /**
     * Helper registration method for a mod
     * @param mod A modid or mod instance
     * @param updateXML String that can be converted as an url for an xml file, listing mod versions and download links by Minecraft releases
     * @param changelog String that can be converted as an url for a file containing text to describe any changes, can be null
     * @throws MalformedURLException If no known protocol is found, or <tt>updateXML</tt> is <tt>null</tt>.
     */
    public static void registerMod(Object mod, String updateXML, String changelog) throws MalformedURLException {
        registerMod(FMLCommonHandler.instance().findContainerFor(mod), updateXML, changelog);
    }

    public static void runUpdateChecker(){

        if(enabled){
            if(verbose) {
                ICommandSender sender = getSender();
                if(sender != null)
                sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.YELLOW + StatCollector.translateToLocal("mud.name") +
                                EnumChatFormatting.WHITE + ": " + StatCollector.translateToLocal("message.checking")));
            }

            Thread t = new Thread(new UpdateChecker(updateMap.values()));
            t.run();
        }

    }

    public static Collection<UpdateEntry> getAllUpdateEntries(){
        return updateMap.values();
    }

    private static void initialise() {
        updateMap = new HashMap<String, UpdateEntry>();
        /*
         * The time between update checks in minutes.
         * A value <=0 will only run the updater when a player joins the world.
         */
        int Timer = 60*60*20;
        try{
	        config = new Configuration(new File(Loader.instance().getConfigDir(), "MUD.cfg"));
	        Timer = config.get(Configuration.CATEGORY_GENERAL, "Update Time", 60, "The time in minutes between update checks").getInt() * 60 * 20;
            check = config.get(Configuration.CATEGORY_GENERAL, "Update Check Enabled", true, "Should MUD automatically check for updates");
	        verbose = config.get(Configuration.CATEGORY_GENERAL, "Chat stats", false, "Should MUD print in chat its status").getBoolean();
            enabled = check.getBoolean(true);
	        if(config.hasChanged()){
	            config.save();
	        }
        }catch(Exception handled){
        	handled.printStackTrace();
        }

        FMLCommonHandler.instance().bus().register(new ModUpdateDetectorTickHandeler(Timer));
        ClientCommandHandler.instance.registerCommand(new MudCommands());
    }

    public static void toggleState(){
        enabled = !enabled;
        check.set(enabled);
        config.save();
    }

    public static ICommandSender getSender() {
        if(sender == null){
        	sender = Minecraft.getMinecraft().thePlayer;
        }
        return sender;
    }


    public static void notifyUpdateDone(){
        ICommandSender sender = getSender();
        if(verbose && sender != null){
            sender.addChatMessage(new ChatComponentText(
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
        ChatComponentTranslation chat;
        if(outOfDateCount > 0){
            if(sender != null){
                chat = new ChatComponentTranslation("message.you.have.outdated", outOfDateCount);
                chat.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(chat);
                chat = new ChatComponentTranslation("message.type.to.view");
                chat.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(chat);
            }
        }else if (verbose){
            if(sender != null){
                chat = new ChatComponentTranslation("message.up.to.date");
                chat.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
                sender.addChatMessage(chat);
            }
        }
        hasChecked = true;
    }
}
