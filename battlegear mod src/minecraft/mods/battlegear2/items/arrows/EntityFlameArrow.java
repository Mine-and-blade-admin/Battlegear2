package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
/**
 * An arrow which sets living entities or blocks on flame
 * @author GotoLink
 *
 */
public class EntityFlameArrow extends AbstractMBArrow{

    public EntityFlameArrow(World par1World) {
        super(par1World);
        isImmuneToFire = true;
    }
    
    public EntityFlameArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
        isImmuneToFire = true;
    }

    public EntityFlameArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
        isImmuneToFire = true;
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        source.setFireDamage();
        entityHit.setFire(3);
        return false;
    }

    @Override
    public void onHitGround(int x, int y, int z) {
        if (worldObj.isAirBlock(x, y+1, z)){
            worldObj.playSoundEffect((double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            worldObj.setBlock(x, y+1, z, Blocks.fire);
        }
    }

    @Override
    public boolean isBurning(){
        return true;
    }
}
