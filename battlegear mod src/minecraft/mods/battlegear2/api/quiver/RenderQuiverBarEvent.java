package mods.battlegear2.api.quiver;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderQuiverBarEvent {

	public static class PreRender extends RenderGameOverlayEvent.Pre{
		public ItemStack main;
		public ItemStack quiver;

		public PreRender(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack quiver) {
			super(parent, null);
			this.main = mainhand;
			this.quiver = quiver;
		}
		
	}
	
	public static class PostRender extends RenderGameOverlayEvent.Post{
		public final ItemStack main;
		public final ItemStack quiver;

		public PostRender(RenderGameOverlayEvent parent, ItemStack mainhand, ItemStack quiver) {
			super(parent, null);
			this.main = mainhand;
			this.quiver = quiver;
		}
		
	}
}
