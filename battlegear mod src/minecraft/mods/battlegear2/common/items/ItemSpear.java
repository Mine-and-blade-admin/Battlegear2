package mods.battlegear2.common.items;

import java.util.List;

import mods.battlegear2.api.IExtendedReachWeapon;
import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon{
	
	private float maxDist = 50F;

	public ItemSpear(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
	}
	
	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {	//Add damage when entity is riding or sprinting (Love this by the way)
        return (par1Entity.isRiding()||par1Entity.isSprinting())?this.baseDamage+3:this.baseDamage;
    }
	
	@Override
	public float getreachInBlocks(ItemStack stack) {
		return 5.0F;
	}

	@Override
	public boolean willAllowShield() {
		return true;
	}
	
	
}
