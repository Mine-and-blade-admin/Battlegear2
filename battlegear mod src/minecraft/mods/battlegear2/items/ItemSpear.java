package mods.battlegear2.items;

import mods.battlegear2.api.IBackStabbable;
import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.api.ISpecialEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon,ISpecialEffect {

    int mounted_extra_damage = 2;

	public ItemSpear(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
	}
	
	@Override
	public float getReachModifierInBlocks(ItemStack stack) {
		return 2.0F;
	}

	@Override
	public boolean willAllowShield() {
		return true;
	}

	@Override
	public PotionEffect[] getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		return null;
	}
	
	@Override
	protected void performEffects(ISpecialEffect item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		if(entityHit.isRiding() || entityHit.isSprinting() || entityHitting.isSneaking())
		{
			if(entityHitting instanceof EntityPlayer)
				entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityHitting), mounted_extra_damage);
			else
				entityHit.attackEntityFrom(DamageSource.causeMobDamage(entityHitting), mounted_extra_damage);
		}
	}

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        par3List.add(EnumChatFormatting.DARK_GREEN+
                StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                        new Object[] {field_111284_a.format(mounted_extra_damage),
                                StatCollector.translateToLocal("attribute.name.weapon.mountedBonus")}));
    }
}
