package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemSpear extends TwoHandedWeapon{

	public ItemSpear(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
	}
	
	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {	//Add damage when entity is riding or sprinting (Love this by the way)
        return (par1Entity.isRiding()||par1Entity.isSprinting())?this.baseDamage+3:this.baseDamage;
    }
}
