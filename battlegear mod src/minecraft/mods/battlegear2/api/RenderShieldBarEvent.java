package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderShieldBarEvent {
	public static class PreRender extends RenderGameOverlayEvent.Pre{
		public ItemStack shield;

		public PreRender(RenderGameOverlayEvent parent, ItemStack shield) {
			super(parent, null);
			this.shield = shield;
		}
		
	}
	
	public static class PostRender extends RenderGameOverlayEvent.Post{
		public final ItemStack shield;

		public PostRender(RenderGameOverlayEvent parent, ItemStack shield) {
			super(parent, null);
			this.shield = shield;
		}
		
	}
}
