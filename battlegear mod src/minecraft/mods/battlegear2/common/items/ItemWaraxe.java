package mods.battlegear2.common.items;

import java.util.Random;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemWaraxe extends OneHandedWeapon{
	
	private int ignoreDamageAmount;

	public ItemWaraxe(int par1, EnumToolMaterial material, String name, int ignoreDamageAmount) {
		super(par1,material,name);
		this.ignoreDamageAmount = ignoreDamageAmount;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-ignoreDamageAmount;
	}
	
	@Override
	public boolean canHarvestBlock(Block par1Block)//Waraxe can harvest logs
    {
        return par1Block.blockID == Block.wood.blockID;
    }
	
	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {
		
		if(par1Entity instanceof EntityLiving){
			
			//Record the hurt times
			int hurtTimeTemp = ((EntityLiving) par1Entity).hurtTime;
			int hurtResistanceTimeTemp = par1Entity.hurtResistantTime;
			
			//Attack using the "generic" damage type (ignores armour)
			par1Entity.attackEntityFrom(DamageSource.generic, ignoreDamageAmount);
			
			//Re-apply the saved values
			((EntityLiving) par1Entity).hurtTime = hurtTimeTemp;
			par1Entity.hurtResistantTime = hurtResistanceTimeTemp;
			
			//return the normal damage minus the amount usually ignored
			return super.getDamageVsEntity(par1Entity)-ignoreDamageAmount;
		}else{
			return super.getDamageVsEntity(par1Entity);
		}
		
    }
}
