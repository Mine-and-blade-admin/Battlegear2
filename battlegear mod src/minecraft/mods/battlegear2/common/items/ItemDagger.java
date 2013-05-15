package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
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
	public boolean offhandAttackEntity(OffhandAttackEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		if(event.getTarget() instanceof EntityLiving)
		{
			((EntityLiving)event.getTarget()).hurtResistantTime=5;//Default is 10
			((EntityLiving)event.getTarget()).hurtTime=5;
		}
		return false;
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

	
}
