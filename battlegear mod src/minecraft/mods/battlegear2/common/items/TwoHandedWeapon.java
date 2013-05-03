package mods.battlegear2.common.items;

import net.minecraft.entity.Entity;

public abstract class TwoHandedWeapon extends ItemWeapon{

	public int baseDamage;

	public TwoHandedWeapon(int par1, int i) {
		super(par1, i);
		this.setMaxDamage(this.getMaterial().getMaxUses());
		this.baseDamage=this.getMaterial().getDamageVsEntity() + 2;
	}
	
	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {
        return this.baseDamage;
    }
	
	@Override
	public boolean willAllowOffhandWeapon() {
		return false;
	}

	@Override
	public boolean isOffhandHandDualWeapon() {
		return false;
	}

	@Override
	public boolean sheatheOnBack() {
		return true;
	}
}
