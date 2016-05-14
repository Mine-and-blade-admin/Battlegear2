package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.lang.reflect.Method;

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
            temp = Class.forName("org.bukkit.craftbukkit.event.CraftEventFactory");
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
            Object result = null;
            if (playerInteracted.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                result = AirInteract.invoke(null, playerInteracted.entityPlayer, AIR, stack);
            } else {
                result = BlockInteract.invoke(null, playerInteracted.entityPlayer, BLOCK, playerInteracted.x, playerInteracted.y, playerInteracted.z, playerInteracted.face, stack);
            }
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
        }catch (Throwable logged){
            Battlegear.logger.error(logged.getMessage());
        }
    }
}
