package mods.battlegear2.api.weapons;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
/**
 * Registry for stacks which will be allowed in battle inventory,
 * accessible through {@link #FMLInterModComms} messages.
 * Use only if your item is not recognized by default.
 * Use of {@link #IBattlegearWeapon} is preferred over this method.
 * {@link #NBTTagCompound} are supported.
 * @author GotoLink
 *
 */
public class WeaponRegistry {
	private static Set<StackHolder> weapons = new HashSet<StackHolder>();
	private static Set<StackHolder> mainHand = new HashSet<StackHolder>();
	private static Set<StackHolder> offHand = new HashSet<StackHolder>();
	/**
	 * Called by a {@link #IMCMessage} with "Dual" as key, and the {@link #ItemStack} as value
	 * @param stack registered as dual wieldable
	 */
	public static void addDualWeapon(ItemStack stack) {
        weapons.add(new StackHolder(stack));
        mainHand.add(new StackHolder(stack));
        offHand.add(new StackHolder(stack));
	}
	
	/**
	 * Called by a {@link #IMCMessage} with "MainHand" as key, and the {@link #ItemStack} as value
	 * @param stack registered as wieldable only in main hand
	 */
	public static void addTwoHanded(ItemStack stack) {
        weapons.add(new StackHolder(stack));
        mainHand.add(new StackHolder(stack));
	}

	/**
	 * Called by a {@link #IMCMessage} with "OffHand" as key, and the {@link #ItemStack} as value
	 * @param stack registered as wieldable only in offhand
	 */
	public static void addOffhandWeapon(ItemStack stack) {
        weapons.add(new StackHolder(stack));
        offHand.add(new StackHolder(stack));
	}
	
	public static boolean isWeapon(ItemStack stack) {
		return weapons.contains(new StackHolder(stack));
	}
	
	public static boolean isMainHand(ItemStack stack) {
		return mainHand.contains(new StackHolder(stack));
	}
	
	public static boolean isOffHand(ItemStack stack) {
		return offHand.contains(new StackHolder(stack));
	}
	
	static class StackHolder{
		private final ItemStack stack;

		public StackHolder(ItemStack stack){
			this.stack = stack;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = prime + (stack == null ? 0 : stack.itemID ^ stack.stackSize + (stack.hasTagCompound() ? prime*prime ^ stack.getTagCompound().hashCode():0));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof StackHolder)) {
				return false;
			}
			if (!ItemStack.areItemStacksEqual(stack, ((StackHolder) obj).stack)) {
				return false;
			}
			return true;
		}
	}
}
