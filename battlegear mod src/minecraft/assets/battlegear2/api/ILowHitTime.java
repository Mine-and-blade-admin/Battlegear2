package assets.battlegear2.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface ILowHitTime {
	/**
	 * 
	 * @param entityHit 
	 * @return The duration of the hit state. 
	 */
	public int getHitTime(ItemStack stack, EntityLivingBase entityHit);

}
