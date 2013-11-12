package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public class EntityExplossiveArrow extends AbstractMBArrow{

    public EntityExplossiveArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityExplossiveArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public boolean onHitEntity(Entity entityHit) {
        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, this.getIsCritical()?2:1F, true);
        this.setDead();
        return false;
    }

    @Override
    public void onHitGround(int x, int y, int z) {
        this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, this.getIsCritical()?2:1F, true);
        this.setDead();
    }
}
