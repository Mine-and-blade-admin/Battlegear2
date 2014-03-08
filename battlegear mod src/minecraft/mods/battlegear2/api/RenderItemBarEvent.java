package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class RenderItemBarEvent extends RenderGameOverlayEvent{
    public int xOffset = 0;
    public int yOffset = 0;

    public RenderItemBarEvent(RenderGameOverlayEvent parent) {
        super(parent.partialTicks, parent.resolution, parent.mouseX, parent.mouseY);
    }

	@Cancelable
	public static class ShieldBar extends RenderItemBarEvent {
        public final ItemStack shield;
		public ShieldBar(RenderGameOverlayEvent parent, ItemStack item) {
			super(parent);
            this.shield = item;
		}
	}
	
	@Cancelable
	public static class QuiverSlots extends RenderItemBarEvent{
		public final ItemStack mainhand;
        public final ItemStack quiver;
		public QuiverSlots(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack item) {
			super(parent);
			this.mainhand = mainhand;
            this.quiver = item;
		}
	}

    @Cancelable
    public static class BattleSlots extends RenderItemBarEvent{
        public final boolean isMainHand;
        public BattleSlots(RenderGameOverlayEvent parent, boolean isMainHand){
            super(parent);
            this.isMainHand = isMainHand;
        }
    }
}
