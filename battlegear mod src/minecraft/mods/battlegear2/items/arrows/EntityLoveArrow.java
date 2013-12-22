package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityLoveArrow extends AbstractMBArrow{

	public EntityLoveArrow(World world) {
		super(world);
	}
	
	public EntityLoveArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityLoveArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
		if(entityHit instanceof EntityAgeable){
			EntityAgeable child = ((EntityAgeable) entityHit).createChild((EntityAgeable) entityHit);
            if (child != null && !this.worldObj.isRemote){
            	child.setGrowingAge(-24000);
            	child.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(child);
            }
			((EntityAgeable)entityHit).setGrowingAge(-24000);
			setDead();
			return true;
		}else if(entityHit instanceof EntityCreature){
			((EntityCreature)entityHit).setTarget(null);
			setDead();
			return true;
		}else if(entityHit instanceof EntityPlayer){
			((EntityPlayer) entityHit).dropPlayerItem(((EntityPlayer) entityHit).getCurrentEquippedItem());
			setDead();
			return true;
		}
		return false;
	}

	@Override
	public void onHitGround(int x, int y, int z) {
		
	}

}
