package assets.battlegear2.common.items;

import assets.battlegear2.api.IBackStabbable;
import assets.battlegear2.api.ILowHitTime;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemDagger extends OneHandedWeapon implements IBackStabbable,ILowHitTime{

	public ItemDagger(int par1, EnumToolMaterial material, String name) {
		super(par1, material, name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage - 2;
	}
	
	@Override
	public boolean canHarvestBlock(Block par1Block)//Daggers can harvest tallgrass and wool
    {
        return par1Block.blockID == Block.tallGrass.blockID||par1Block.blockID == Block.cloth.blockID;
    }

	@Override
	public int getHitTime(ItemStack stack,EntityLivingBase target) {
		return 0;
	}
	
	@Override//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
	public void onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting) 
	{
		if(entityHitting instanceof EntityPlayer)
			entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityHitting), this.baseDamage);
		else
			entityHit.attackEntityFrom(DamageSource.causeMobDamage(entityHitting), this.baseDamage);
	}
}
