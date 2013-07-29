package assets.battlegear2.api;

import net.minecraft.entity.EntityLivingBase;

public interface ISpecialEffect {

	/**
	 * Action to perform on entityHitting attacking entityHit
	 * @param entityHit
	 * @param entityHitting
	 * @return 
	 * @return true if it adds an hitting action
	 */
	public boolean performEffects(EntityLivingBase entityHit, EntityLivingBase entityHitting);

}
