package mods.battlegear2.api.weapons;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Registry for stacks which will be allowed in battle inventory,
 * accessible through {@link FMLInterModComms} messages.
 * Use only if your item is not recognized by default.
 * Use of {@link IBattlegearWeapon} is preferred over this method.
 * {@link NBTTagCompound} are supported.
 * @author GotoLink
 *
 */
public class WeaponRegistry {
	private static Set<StackHolder> weapons = new HashSet<StackHolder>();
	private static Set<StackHolder> mainHand = new HashSet<StackHolder>();
	private static Set<StackHolder> offHand = new HashSet<StackHolder>();
	/**
	 * Called by a {@link IMCMessage} with "Dual" as key, and the {@link ItemStack} as value
	 * @param stack registered as dual wieldable
	 */
	public static void addDualWeapon(ItemStack stack) {
        weapons.add(new StackHolder(stack));
        mainHand.add(new StackHolder(stack));
        offHand.add(new StackHolder(stack));
	}
	
	/**
	 * Called by a {@link IMCMessage} with "MainHand" as key, and the {@link ItemStack} as value
	 * @param stack registered as wieldable only in main hand
	 */
	public static void addTwoHanded(ItemStack stack) {
        weapons.add(new StackHolder(stack));
        mainHand.add(new StackHolder(stack));
	}

	/**
	 * Called by a {@link IMCMessage} with "OffHand" as key, and the {@link ItemStack} as value
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
        private int hash;

		public StackHolder(ItemStack stack){
			this.stack = stack;
		}
		
		@Override
		public int hashCode() {
            int init = 17, mult = 37;
            if(hash==0) {
                if(stack==null)
                    hash = new HashCodeBuilder(init, mult).toHashCode();
                else
                    hash = new HashCodeBuilder(init, mult).append(Item.getIdFromItem(stack.getItem())).append(stack.getItemDamage()).append(stack.getTagCompound()).toHashCode();
            }
            return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			return obj instanceof StackHolder && ItemStack.areItemStacksEqual(stack, ((StackHolder) obj).stack);
		}
	}
}
