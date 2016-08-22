package mods.battlegear2;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Help manage "Bukkit" support
 * Fixing possible "Protection" plugins
 */
public class BukkitWrapper {
    private static Object AIR,BLOCK;//The Bukkit Action enum values
    private static Object DENY;//The Bukkit Result.DENY enum value
    private static Method AirInteract, BlockInteract;//The event calling method
    private static Method IsCancelled, ItemUse, BlockUse;//The event instance methods
    static{
        try {
            Class<?> temp = Class.forName("org.bukkit.event.block.Action");
            Object[] objs = temp.getEnumConstants();
            AIR = objs[3];
            BLOCK = objs[1];
            temp = Class.forName("org.bukkit.event.Event$Result");
            DENY = temp.getEnumConstants()[0];
            URL resource = ClassLoader.getSystemClassLoader().getResource("org/bukkit/craftbukkit");
            if(resource == null) {
                throw new RuntimeException("No resource at path org/bukkit/craftbukkit");
            }

            if(resource.getProtocol().equals("jar")){
                Battlegear.logger.info("Loading CraftEventFactory from jar");
                temp = exploreJar(((JarURLConnection) resource.openConnection()).getJarFile(), "org/bukkit/craftbukkit");
            }else{
                Battlegear.logger.info("Loading CraftEventFactory from directories");
                temp = exploreDir(new File(URLDecoder.decode(resource.getPath(), "UTF-8")), "org.bukkit.craftbukkit");
            }
            if(temp == null) {
                throw new RuntimeException("Couldn't find event factory at path org/bukkit/craftbukkit");
            }
            for(Method m : temp.getMethods()){
                if(m.getName().equals("callPlayerInteractEvent")){
                    int i = m.getParameterTypes().length;
                    if(i == 3)
                        AirInteract = m;
                    else if(i == 7)
                        BlockInteract = m;
                }
                if(AirInteract != null && BlockInteract != null)
                    break;
            }
            temp = Class.forName("org.bukkit.event.player.PlayerInteractEvent");
            IsCancelled = temp.getMethod("isCancelled");
            ItemUse = temp.getMethod("useItemInHand");
            BlockUse = temp.getMethod("useInteractedBlock");
        }catch (Throwable logged){
            Battlegear.logger.error(logged.getMessage());
        }
    }

    /**
     * Send the player interaction data to Bukkit, if it exists
     * Then apply the answer states to the interaction data
     * @param playerInteracted the interaction data
     * @param stack data added by Battlegear, hopefully not messed up by Bukkit
     */
    public static void callBukkitInteractEvent(PlayerInteractEvent playerInteracted, ItemStack stack){
        if(AIR == null)//No point if Bukkit isn't here
            return;
        try {
            Object result;
            if (playerInteracted.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                result = AirInteract.invoke(null, playerInteracted.entityPlayer, AIR, stack);
                if(result != null && ItemUse.invoke(result) == DENY)
                    playerInteracted.useItem = Event.Result.DENY;
            } else {
                result = BlockInteract.invoke(null, playerInteracted.entityPlayer, BLOCK, playerInteracted.pos.getX(), playerInteracted.pos.getY(), playerInteracted.pos.getZ(), playerInteracted.face.getIndex(), stack);

                if(result != null){
                    if((Boolean)IsCancelled.invoke(result)){
                        playerInteracted.setCanceled(true);
                    }else {
                        if(ItemUse.invoke(result) == DENY){
                            playerInteracted.useItem = Event.Result.DENY;
                        }
                        if(BlockUse.invoke(result) == DENY){
                            playerInteracted.useBlock = Event.Result.DENY;
                        }
                    }
                }
            }
        }catch (Throwable logged){
            Battlegear.logger.error(logged.getMessage());
        }
    }

    /**
     * Search Bukkit event factory class within jar
     * @throws ClassNotFoundException
     */
    private static Class<?> exploreJar(JarFile jarPath, String relPath) throws ClassNotFoundException, IOException {
        //Get contents of jar file and iterate through them
        Enumeration<JarEntry> entries = jarPath.entries();
        while(entries.hasMoreElements()) {
            String entryName = entries.nextElement().getName();

            //Found the event factory, get the full class name.
            if(entryName.endsWith("CraftEventFactory.class") && entryName.startsWith(relPath)) {
                return Class.forName(entryName.replace('/', '.').replace('\\', '.').replace(".class", ""));
            }
        }
        return null;
    }

    /**
     * Search Bukkit event factory class within directories
     * @throws ClassNotFoundException
     */
    private static Class<?> exploreDir(File directory, String pkgname) throws ClassNotFoundException {
        //Iterate through files in directory
        String[] files = directory.list();
        for(String name : files){
            if(name.endsWith("CraftEventFactory.class")){//Found the event factory
                return Class.forName(pkgname + '.' + name.substring(0, name.length() - 6));
            }
            File file = new File(directory, name);
            if(file.isDirectory()){
                Class<?> temp = exploreDir(file, pkgname + '.' + name);
                if(temp != null)
                    return temp;
            }
        }
        return null;
    }
}
