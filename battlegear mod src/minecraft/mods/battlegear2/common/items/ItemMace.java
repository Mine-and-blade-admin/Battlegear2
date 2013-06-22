package mods.battlegear2.common.items;

import java.util.Random;

import mods.battlegear2.api.ISpecialEffect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemMace extends OneHandedWeapon implements ISpecialEffect{

	public ItemMace(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
	}

	@Override
	public PotionEffect[] getEffectsOnHit(ItemStack item, EntityLiving entityHit) {
		return new PotionEffect[]{
				new PotionEffect(2,100),new PotionEffect(9,100),
				new PotionEffect(15,100),new PotionEffect(18,100)};
	}
}
