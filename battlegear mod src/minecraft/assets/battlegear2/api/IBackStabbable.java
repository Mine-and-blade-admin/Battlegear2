package assets.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;

public interface IBackStabbable {

	/**
	 * Action to perform on back stabbing
	 * @param entityHit
	 * @param entityHitting
	 */
	public void onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting);
}
