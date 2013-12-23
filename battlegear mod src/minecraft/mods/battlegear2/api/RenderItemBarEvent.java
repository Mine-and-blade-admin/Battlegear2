package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.Cancelable;

public class RenderItemBarEvent {
	@Cancelable
	public static class PreRender extends RenderGameOverlayEvent.Pre{
		public final ItemStack item;
		public int xOffset = 0;
		public int yOffset = 0;

		public PreRender(RenderGameOverlayEvent parent, ItemStack item) {
			super(parent, null);
			this.item = item;
		}
		
	}
	
	@Cancelable
	public static class PreDual extends PreRender{
		public final ItemStack mainhand;
		public PreDual(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack item) {
			super(parent, item);
			this.mainhand = mainhand;
		}
		
	}
	
	public static class PostRender extends RenderGameOverlayEvent.Post{
		public final ItemStack item;
		public PostRender(RenderGameOverlayEvent parent, ItemStack item) {
			super(parent, null);
			this.item = item;
		}
		
	}
	
	public static class PostDual extends PostRender{
		public final ItemStack mainhand;
		public PostDual(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack item) {
			super(parent, item);
			this.mainhand = mainhand;
		}
	}
}
