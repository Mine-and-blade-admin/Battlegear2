package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.Cancelable;

public class RenderPlayerEventChild extends RenderPlayerEvent{
	public static enum PlayerElementType{
		Offhand,
		ItemOffhand,
		ItemOffhandSheathed,
		ItemMainhandSheathed,
	}
	
	public final PlayerElementType type;
	public final boolean isFirstPerson;
	public final ItemStack element;
	/**
	 * @param type Describe what element is rendered, either the player arm or the item hold/sheathed
	 * @param firstPerson True in first person rendering, false in third person rendering
	 * @param item The element to be rendered, null if a player arm
	 */
	public RenderPlayerEventChild(RenderPlayerEvent parent, PlayerElementType type, boolean firstPerson, ItemStack item) {
		super(parent.entityPlayer, parent.renderer, parent.partialRenderTick);
		this.type = type;
		this.isFirstPerson = firstPerson;
		this.element = item;
	}
	
	@Cancelable
	public static class PreRenderPlayerElement extends RenderPlayerEventChild{
		public PreRenderPlayerElement(RenderPlayerEvent parent, boolean isFirstPerson, PlayerElementType type, ItemStack item) {
			super(parent, type, isFirstPerson, item);
		}
	}
	
	public static class PostRenderPlayerElement extends RenderPlayerEventChild{
		public PostRenderPlayerElement(RenderPlayerEvent parent, boolean isFirstPerson, PlayerElementType type, ItemStack item) {
			super(parent, type, isFirstPerson, item);
		}
	}
	
	@Cancelable
	public static class PreRenderSheathed extends PreRenderPlayerElement{
		/*
		 * True if the sheathed item is supposed to be on the player back
		 */
		public final boolean isOnBack;
		/*
		 * The number of items supposed to be laying on the player back,
		 * including chest armor
		 */
		public final int backCount;
		public PreRenderSheathed(RenderPlayerEvent parent, boolean isOnBack, int count, boolean isMainHand, ItemStack item) {
			super(parent, false, isMainHand?PlayerElementType.ItemMainhandSheathed:PlayerElementType.ItemOffhandSheathed, item);
			this.isOnBack = isOnBack;
			this.backCount = count;
		}
	}

	public static class PostRenderSheathed extends PostRenderPlayerElement{
		/*
		 * True if the sheathed item is supposed to be on the player back
		 */
		public final boolean isOnBack;
		/*
		 * The number of items supposed to be laying on the player back,
		 * including chest armor
		 */
		public final int backCount;
		public PostRenderSheathed(RenderPlayerEvent parent, boolean isOnBack, int count, boolean isMainHand, ItemStack item) {
			super(parent, false, isMainHand?PlayerElementType.ItemMainhandSheathed:PlayerElementType.ItemOffhandSheathed, item);
			this.isOnBack = isOnBack;
			this.backCount = count;
		}
	}
	
}
