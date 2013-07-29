package assets.battlegear2.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import assets.battlegear2.api.IExtendedReachWeapon;
import assets.battlegear2.api.ISpecialEffect;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon,ISpecialEffect{
	
	private float maxDist = 50F;

	public ItemSpear(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
	}
	
	@Override
	public float getreachInBlocks(ItemStack stack) {
		return 5.0F;
	}

	@Override
	public boolean willAllowShield() {
		return true;
	}
	
	@Override
	public boolean performEffects(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		if(entityHit.isRiding() || entityHit.isSprinting() || entityHitting.isSneaking())
		{
			if(entityHitting instanceof EntityPlayer)
				entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityHitting), this.baseDamage - 2);
			else
				entityHit.attackEntityFrom(DamageSource.causeMobDamage(entityHitting), this.baseDamage - 2);
			return true;
		}
		return false;
	}
}
