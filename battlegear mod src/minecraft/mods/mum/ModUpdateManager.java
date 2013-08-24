package mods.mum;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.mum.exceptions.UnknownVersionFormatException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModUpdateManager {
    
    private static boolean hasInitialised = false;
    private static Map<String, UpdateEntry> updateMap;

    /*
     * The time between update checks in minutes.
     * A value <=0 will only run the updater when a player joins the world.
     */
    private static int Timer = 15*20;
    private static MinecraftServer server;


    public static void registerMod(ModContainer mc, URL updateXML, URL changelog){
        if(!hasInitialised){
            initialise();
            hasInitialised = true;
        }

        updateMap.put(mc.getModId(), new UpdateEntry(mc, updateXML, changelog));
    }

    public static void runUpdateChecker(){

        if(server == null || server != FMLCommonHandler.instance().getMinecraftServerInstance()){
            server = FMLCommonHandler.instance().getMinecraftServerInstance();
            CommandHandler ch = (CommandHandler) server.getCommandManager();
            ch.registerCommand(new BattlegearCommands());

        }


        ICommandSender sender = getSender();
        sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(
                EnumChatFormatting.YELLOW + StatCollector.translateToLocal("mum.name") +
                EnumChatFormatting.WHITE + ": "+StatCollector.translateToLocal("message.checking")
        ));

        Thread t = new Thread(new UpdateChecker(updateMap.values()));
        t.run();

    }

    public static Collection<UpdateEntry> getAllUpdateEntries(){
        return updateMap.values();
    }

    private static void initialise() {
        updateMap = new HashMap<String, UpdateEntry>();
        TickRegistry.registerTickHandler(new ModUpdateManagerTickHandeler(Timer), Side.CLIENT);

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
        sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(
                EnumChatFormatting.YELLOW + StatCollector.translateToLocal("mum.name") +
                        EnumChatFormatting.WHITE + ": "+StatCollector.translateToLocal("message.check.done")
        ));

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
            sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(
                    String.format("%s%s %s %s",
                    EnumChatFormatting.RED, StatCollector.translateToLocal("message.you.have"),
                            outOfDateCount, StatCollector.translateToLocal("message.outdated"))
            ));
            sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(
                    String.format("%s%s %s %s",
                            EnumChatFormatting.RED, StatCollector.translateToLocal("message.type"),
                            "/mum", StatCollector.translateToLocal("message.to.view"))
            ));
        }else{
            sender.sendChatToPlayer(ChatMessageComponent.func_111066_d(
                    String.format("%s%s",
                            EnumChatFormatting.DARK_GREEN, StatCollector.translateToLocal("message.up.to.date"))
            ));
        }


    }
}
