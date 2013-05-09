package mods.battlegear2.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public abstract class OneHandedWeapon extends ItemWeapon{

	public OneHandedWeapon(int par1, int i, String named) {
		super(par1, i, named);
		this.setMaxDamage(this.getMaterial().getMaxUses() * 2);
		this.baseDamage=this.getMaterial().getDamageVsEntity()+1;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.block;
    }
	
	@Override
	public boolean willAllowOffhandWeapon() {
		return true;
	}

	@Override
	public boolean isOffhandHandDualWeapon() {
		return true;
	}

	@Override
	public boolean sheatheOnBack() {
		return false;
	}
	
	@Override
	public int getItemEnchantability()
    {
        return this.getMaterial().getEnchantability();
    }
}
