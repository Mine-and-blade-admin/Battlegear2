package mods.battlegear2.common.items;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class ItemMace extends OneHandedWeapon{

	public ItemMace(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
	}

	//TODO: implement the stunning effects
	// Can be done either by applying potions (much easier) or keeping a synced list
	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLiving entityHit, EntityLiving entityHitting)
    {
		//Chance of stun depending on material: from 10% for wood to 50% for gold
		if(new Random().nextFloat() * 10 + this.getMaterial().ordinal() > 9)
		{
			//Going for potion effect, each one having same duration and base level (probably needs testing)
			//First checking for already existing potion effect
			if(!entityHit.isPotionActive(2))//slowdown
			{
				 entityHit.addPotionEffect(new PotionEffect(2,100));//then apply effect
			}
			if(!entityHit.isPotionActive(9))//confusion
			{
				 entityHit.addPotionEffect(new PotionEffect(9,100));
			}
			if(!entityHit.isPotionActive(15))//blindness
			{
				 entityHit.addPotionEffect(new PotionEffect(15,100));
			}
			if(!entityHit.isPotionActive(18))//weakness
			{
				 entityHit.addPotionEffect(new PotionEffect(18,100));
			}
		}
        return super.hitEntity(itemStack, entityHit, entityHitting);//adds damage to itemstack
    }
}
