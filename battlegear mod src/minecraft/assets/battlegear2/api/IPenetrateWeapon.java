package assets.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IPenetrateWeapon {
	/**
	 * The amount of damage bypassing armor
	 * @param stack The {@link ItemStack} representative of the item dealing the hit.
	 * @param entityHit
	 * @param entityHitting
	 * @return
	 */
	public float getPenetratingPower(ItemStack stack, EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
