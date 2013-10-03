package mods.battlegear2.items.arrows;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

public abstract class AbstractMBArrow extends EntityArrow {

    public AbstractMBArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public AbstractMBArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    public abstract boolean onHitEntity(Entity entityHit);

    public abstract void onHitGround(int x, int y, int z);

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(ticksInGround == 1){
            onHitGround(xTile, yTile, zTile);
        }
    }

    public static AbstractMBArrow generate(int type, EntityArrow arrow, EntitySkeleton skeleton) {
        AbstractMBArrow mbArrow = null;
        if(arrow != null && skeleton != null && skeleton.getAttackTarget() != null){

            switch(type){
                case 0:
                    mbArrow = new EntityExplossiveArrow(arrow.worldObj, skeleton, skeleton.getAttackTarget(), 1.6F, (float)(14 - skeleton.worldObj.difficultySetting * 4));
                    break;
                case 1:
                    mbArrow = new EntityEnderArrow(arrow.worldObj, skeleton, skeleton.getAttackTarget(), 1.6F, (float)(14 - skeleton.worldObj.difficultySetting * 4));
                    break;
                case 2:
                    mbArrow = new EntityFlameArrow(arrow.worldObj, skeleton, skeleton.getAttackTarget(), 1.6F, (float)(14 - skeleton.worldObj.difficultySetting * 4));
                    break;
            }

        }
        return mbArrow;
    }
}
