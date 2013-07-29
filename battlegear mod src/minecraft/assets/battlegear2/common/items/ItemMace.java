package assets.battlegear2.common.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import assets.battlegear2.api.IPotionEffect;

public class ItemMace extends OneHandedWeapon implements IPotionEffect{

	public ItemMace(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
	}

	@Override
	public Map<PotionEffect, Short> getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting) 
	{
		return effects;
	}
	
	public static Map<PotionEffect,Short> effects= new HashMap<PotionEffect,Short>();
	static{
		effects.put(new PotionEffect(2,100), (short) 10);
		effects.put(new PotionEffect(9,100), (short) 10);
		effects.put(new PotionEffect(15,100), (short) 10);
		effects.put(new PotionEffect(18,100), (short) 10);
	}
}
