package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
/**
 * An arrow which causes explosions
 * @author GotoLink
 *
 */
public class EntityExplossiveArrow extends AbstractMBArrow{

	public EntityExplossiveArrow(World par1World) {
        super(par1World);
    }
	
    public EntityExplossiveArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityExplossiveArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        source.setExplosion();
        this.onExplode();
        return false;
    }

    @Override
    public void onHitGround(BlockPos pos) {
    	this.onExplode();
    }
    
    public void onExplode(){
        if (!this.world.isRemote) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, getExplosionStrength(), canBreakBlocks());
            this.setDead();
        }
    }

	public float getExplosionStrength() {
		return getIsCritical()?2F:1F;
	}
}
