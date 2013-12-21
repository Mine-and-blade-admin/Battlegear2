package mods.battlegear2.items.arrows;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFlameArrow extends AbstractMBArrow{

    public EntityFlameArrow(World par1World) {
        super(par1World);
    }
    
    public EntityFlameArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityFlameArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        entityHit.setFire(3);
        return false;
    }

    @Override
    public void onHitGround(int x, int y, int z) {
        if (worldObj.isAirBlock(x, y+1, z)){
            worldObj.playSoundEffect((double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D, "fire.ignite", 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            worldObj.setBlock(x, y+1, z, Block.fire.blockID);
        }
    }
}
