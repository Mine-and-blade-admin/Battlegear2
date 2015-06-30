package mods.battlegear2;

import mods.battlegear2.packet.LoginPacket;
import mods.battlegear2.recipies.CraftingHandeler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class BgPlayerTracker {

    public static final BgPlayerTracker INSTANCE = new BgPlayerTracker();

    private BgPlayerTracker(){}

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP){
            Battlegear.packetHandler.sendPacketToPlayer(new LoginPacket().generatePacket(), (EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            Battlegear.battlegearEnabled = false;
    }

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event){
        CraftingHandeler.onCrafting(event.player, event.crafting, event.craftMatrix);
    }
}
