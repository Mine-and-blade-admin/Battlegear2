package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityExplossiveArrow extends AbstractMBArrow{

	public EntityExplossiveArrow(World par1World) {
        super(par1World);
    }
	
    public EntityExplossiveArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityExplossiveArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        this.onExplode();
        return false;
    }

    @Override
    public void onHitGround(int x, int y, int z) {
    	this.onExplode();
    }
    
    public void onExplode(){
        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, getExplosionStrength(this.getIsCritical()), true);
        this.setDead();
    }

	public float getExplosionStrength(boolean isCritical) {
		return isCritical?2F:1F;
	}
}
