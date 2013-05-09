package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemDagger extends OneHandedWeapon{

	public ItemDagger(int par1, int i) {
		super(par1, i, "Dagger-");
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
	public boolean offhandClickAir(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		return false;
	}

	@Override
	public boolean offhandClickBlock(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		return false;
	}

	@Override
	public void performPassiveEffects(Side effectiveSide,
			ItemStack mainhandItem, ItemStack offhandItem) {
		if(mainhandItem.getItem() instanceof ItemDagger && this.baseDamage<5)//If two daggers are equipped, they dealt more damage
		{
			this.addDamagePower(1);
		}
	}

}
