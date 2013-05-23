package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemDagger extends OneHandedWeapon{

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
	public int getDamageVsEntity(Entity par1Entity) {
		//Moved this from the offhand Attack method, should work on both off and mainhand.
		// This might even be better implemented as a event
		if(par1Entity instanceof EntityLiving){
			//The usual is less than half the max hurt resistance time
			if(((EntityLiving)par1Entity).hurtResistantTime < 
					((float)((EntityLiving) par1Entity).maxHurtResistantTime) * 0.75F){
				
				((EntityLiving)par1Entity).hurtResistantTime = 0;		
			}	
		}
		
		return super.getDamageVsEntity(par1Entity);
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLiving entityHit, EntityLiving entityHitting)
    {
		//Get victim and murderer vector views at hit time
		double[] victimView = new double[]{entityHit.getLookVec().xCoord,entityHit.getLookVec().zCoord};
		double[] murdererView = new double[]{entityHitting.getLookVec().xCoord,entityHitting.getLookVec().zCoord};
		//back-stab conditions: vectors are closely enough aligned, (fuzzy parameter might need testing)
		//but not in opposite directions (face to face or sideways)
		if(Math.abs(victimView[0]*murdererView[1]-victimView[1]*murdererView[0])<0.01 && Math.signum(victimView[0])==Math.signum(murdererView[0]) && Math.signum(victimView[1])==Math.signum(murdererView[1]))
		//Perform back stab effect
		{//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
			if(entityHitting instanceof EntityPlayer)
				entityHit.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entityHitting), this.getDamageVsEntity(entityHit));
			else //In case a mob gets a dagger and sneak against another entity...?
				entityHit.attackEntityFrom(DamageSource.causeMobDamage(entityHitting), this.getDamageVsEntity(entityHit));
		}
		return super.hitEntity(itemStack, entityHit, entityHitting);//adds damage to itemstack
    }
}
