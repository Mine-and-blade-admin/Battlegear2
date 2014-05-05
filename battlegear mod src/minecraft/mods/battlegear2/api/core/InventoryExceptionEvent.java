package mods.battlegear2.api.core;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created by GotoLink on 05/05/2014.
 * Event posted on {@link MinecraftForge.EVENT_BUS} when an instance of {@link EntityPlayer} just spawned, but its inventory field
 * isn't an instance of {@link InventoryPlayerBattle}, meaning most of Battlegear features aren't going to work
 * Cancel this event to prevent the exception thrown...and probably allow weird stuff to happen
 */
@Cancelable
public class InventoryExceptionEvent extends PlayerEvent{
    public InventoryExceptionEvent(EntityPlayer player) {
        super(player);
    }
}
