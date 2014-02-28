package mods.battlegear2.items;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.weapons.IBackStabbable;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.api.weapons.IHitTimeModifier;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;

public class ItemDagger extends OneHandedWeapon implements IBackStabbable,IHitTimeModifier,IExtendedReachWeapon{

	public ItemDagger(ToolMaterial material, String name) {
		super(material, name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
	}
	
	@Override
	public boolean func_150897_b(Block par1Block)//Daggers can harvest tallgrass and wool
    {
        return par1Block == Blocks.tallgrass||par1Block == Blocks.wool;
    }

	@Override
	public int getHitTime(ItemStack stack,EntityLivingBase target) {
		return -5;
	}
	
	@Override//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting)
	{
        entityHit.attackEntityFrom(new EntityDamageSource(Battlegear.CUSTOM_DAMAGE_SOURCE, entityHitting), this.baseDamage/2);
        return true;
	}

    @Override
    public float getReachModifierInBlocks(ItemStack stack) {
        return -2;
    }
}
