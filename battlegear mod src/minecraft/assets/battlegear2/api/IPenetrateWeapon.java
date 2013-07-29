package assets.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPenetrateWeapon {
	/**
	 * @param stack The {@link ItemStack} representative of the item dealing the hit.
	 * @param entityHit
	 * @param entityHitting
	 * @return The amount of damage bypassing armor
	 */
	public float getPenetratingPower(ItemStack stack, EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
