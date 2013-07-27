package assets.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;

public interface IBackStabbable {

	/**
	 * Action to perform on back stabbing
	 * @param entityHit
	 * @param entityHitting
	 * @return true if it was an hitting action
	 */
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
