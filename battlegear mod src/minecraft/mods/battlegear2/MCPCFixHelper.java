package mods.battlegear2;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

/**
 * Help fix MCPC+ derp with inventory
 */
public class MCPCFixHelper {
    public MCPCFixHelper(){
        if(isMCPCLoaded()){
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void playerDropOnDeath(PlayerDropsEvent event) {
        EntityPlayer player = event.entityPlayer;
        InventoryPlayerBattle inventory = ((InventoryPlayerBattle) player.inventory);
        for (int i = 0; i < inventory.extraItems.length; ++i) {
            if (inventory.extraItems[i] != null) {
                event.drops.add(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, inventory.extraItems[i]));
                inventory.extraItems[i] = null;
            }
        }
    }

    public static boolean isMCPCLoaded(){
        return FMLCommonHandler.instance().getModName().contains("mcpc");
    }
}
