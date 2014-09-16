package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Events from {@link BattlegearInGameGUI} to {@link MinecraftForge.EVENT_BUS}
 * helping display HUD elements added to the in-game screen
 */
public abstract class RenderItemBarEvent extends RenderGameOverlayEvent{
    /**
     * Horizontal offset from the default position
     */
    public int xOffset = 0;
    /**
     * Vertical offset from the default position
     */
    public int yOffset = 0;

    public RenderItemBarEvent(RenderGameOverlayEvent parent) {
        super(parent.partialTicks, parent.resolution, parent.mouseX, parent.mouseY);
    }

    /**
     * Event posted when the player uses the shield in the offhand, display a sort of "stamina bar"
     */
	@Cancelable
	public static class ShieldBar extends RenderItemBarEvent {
        /**
         * The stack containing the {@link IShield} item held by the player in his offhand
         */
        public final ItemStack shield;
		public ShieldBar(RenderGameOverlayEvent parent, ItemStack item) {
			super(parent);
            this.shield = item;
		}
	}

    /**
     * Event posted when the player uses an item compatible with {@link QuiverArrowRegistry}
     * that is, a bow and its valid {@link IArrowContainer2}, displaying all slots in it
     */
	@Cancelable
	public static class QuiverSlots extends RenderItemBarEvent{
        /**
         * The stack containing the compatible bow held by the player (can be in either hand)
         */
		public final ItemStack mainhand;
        /**
         * The stack containing the valid {@link IArrowContainer2} item
         */
        public final ItemStack quiver;
		public QuiverSlots(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack item) {
			super(parent);
			this.mainhand = mainhand;
            this.quiver = item;
		}
	}

    /**
     * Event corresponding to the display of each additional hand slots
     */
    @Cancelable
    public static class BattleSlots extends RenderItemBarEvent{
        /**
         * True if the slots are for the mainhand (on the "right"), false for the offhand (on the "left")
         */
        public final boolean isMainHand;
        public BattleSlots(RenderGameOverlayEvent parent, boolean isMainHand){
            super(parent);
            this.isMainHand = isMainHand;
        }
    }
}
