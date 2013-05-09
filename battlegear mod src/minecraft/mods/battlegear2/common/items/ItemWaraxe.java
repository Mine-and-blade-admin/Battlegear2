package mods.battlegear2.common.items;

import java.util.Random;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemWaraxe extends TwoHandedWeapon{

	public ItemWaraxe(int par1, int i) {
		super(par1,i,"Waraxe-");
	}
	@Override
	public boolean canHarvestBlock(Block par1Block)//Waraxe can harvest logs
    {
        return par1Block.blockID == Block.wood.blockID;
    }
	
	@Override
	public boolean offhandAttackEntity(OffhandAttackEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		if(event.getTarget() instanceof EntityPlayer)//We do additional armor damage
			((EntityPlayer)event.getTarget()).inventory.damageArmor(1);
		else if (event.getTarget() instanceof EntityLiving)
			for (int index=0;index<4;index++)
				((EntityLiving)event.getTarget()).getCurrentArmor(index).damageItem(1, (EntityLiving) event.getTarget());
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
	}
	
	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {
		Random rand=new Random();
		//Chance of critical damage depending on material: from 10% for wood to 50% for gold
		if(rand.nextFloat() * 10 + this.getMaterial().ordinal() + 1 > 10)
			this.addDamagePower((int) (this.baseDamage * 0.5));
		//Add damage if entity is sneaking
		return par1Entity.isSneaking()?this.baseDamage+1:this.baseDamage;
    }
}
