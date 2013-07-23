package assets.battlegear2.common.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import assets.battlegear2.api.ISpecialEffect;

public class ItemMace extends OneHandedWeapon implements ISpecialEffect{

	public ItemMace(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
	}

	@Override
	public PotionEffect[] getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		return new PotionEffect[]{
				new PotionEffect(2,100),new PotionEffect(9,100),
				new PotionEffect(15,100),new PotionEffect(18,100)};
	}
}
