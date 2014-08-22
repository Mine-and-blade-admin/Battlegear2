package mods.battlegear2.api.quiver;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Event triggered when about to swap slot in quiver.
 * Cancel to stay on currently selected slot, or change slotStep to step over the next slot
 */
@Cancelable
public class SwapArrowEvent extends PlayerEvent{
    /**
     * {@link ItemStack} holding a {@link IArrowContainer2} item
     */
    public final ItemStack quiverStack;
    /**
     * Current slot value
     */
    public final int selected;
    /**
     * Step when swapping slots
     */
    public int slotStep = 1;
    public SwapArrowEvent(EntityPlayer player, ItemStack quiver) {
        super(player);
        quiverStack = quiver;
        selected = ((IArrowContainer2) quiver.getItem()).getSelectedSlot(quiverStack);
    }

    /**
     * @return the next slot value
     */
    public int getNextSlot(){
        return  (selected+slotStep)%((IArrowContainer2) quiverStack.getItem()).getSlotCount(quiverStack);
    }

    /**
     * @return slot content after swap
     */
    public ItemStack getNextArrow(){
        return ((IArrowContainer2) quiverStack.getItem()).getStackInSlot(quiverStack, getNextSlot());
    }

    /**
     * @return slot content before swap
     */
    public ItemStack getCurrentArrow(){
        return ((IArrowContainer2) quiverStack.getItem()).getStackInSlot(quiverStack, selected);
    }
}
