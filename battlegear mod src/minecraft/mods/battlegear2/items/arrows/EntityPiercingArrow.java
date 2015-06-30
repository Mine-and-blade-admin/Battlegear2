package mods.battlegear2.items.arrows;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.List;
import java.util.Random;
/**
 * An arrow which deals damage through armors and shields, shears things, and breaks glass blocks
 * @author GotoLink
 *
 */
public class EntityPiercingArrow extends AbstractMBArrow{

	public EntityPiercingArrow(World par1World) {
		super(par1World);
	}
	
	public EntityPiercingArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityPiercingArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public void onUpdate() {
        Vec3 a = new Vec3(this.posX, this.posY, this.posZ);
        Vec3 b = new Vec3(this.posX + this.motionX * 1.5, this.posY + this.motionY * 1.5, this.posZ + this.motionZ * 1.5);
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(a, b, false, true, true);

        if (ticksInGround == 0 && movingobjectposition != null && movingobjectposition.entityHit == null){
            ticksInGround ++;
            onHitGround(movingobjectposition.getBlockPos().getX(), movingobjectposition.getBlockPos().getY(), movingobjectposition.getBlockPos().getZ());
        }
        super.onUpdate();
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
		entityHit.attackEntityFrom(getPiercingDamage(), ammount);
		if(!worldObj.isRemote && entityHit instanceof IShearable){
            if (((IShearable) entityHit).isShearable(null, worldObj, new BlockPos(entityHit))) {
                List<ItemStack> drops = ((IShearable) entityHit).onSheared(null, worldObj, new BlockPos(entityHit), 1);
                Random rand = new Random();
                for(ItemStack stack : drops){
                    EntityItem ent = entityHit.entityDropItem(stack, 1.0F);
                    ent.motionY += rand.nextFloat() * 0.05F;
                    ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                }
			}
		}
		return true;
	}

    public DamageSource getPiercingDamage(){
        return new EntityDamageSourceIndirect("piercing.arrow", null, shootingEntity).setProjectile().setDamageBypassesArmor();
    }

	@Override
	public void onHitGround(int x, int y, int z) {
        boolean broken = false;
        if(canBreakBlocks()) {
            BlockPos pos = new BlockPos(x, y, z);
            Block block = worldObj.getBlockState(pos).getBlock();
            if (block.getMaterial() == Material.glass) {
                worldObj.destroyBlock(pos, false);
            } else if (!worldObj.isRemote) {
                if (block instanceof IShearable) {
                    IShearable target = (IShearable) block;
                    if (target.isShearable(null, worldObj, pos)) {
                        List<ItemStack> drops = target.onSheared(null, worldObj, pos, 1);
                        Random rand = new Random();
                        for (ItemStack stack : drops) {
                            float f = 0.7F;
                            double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(worldObj, (double) x + d, (double) y + d1, (double) z + d2, stack);
                            entityitem.setDefaultPickupDelay();
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                        broken = worldObj.setBlockToAir(pos);
                    }
                }
            }
        }
        if(broken){
            this.ticksInGround = 0;
            this.yTile = -1;
            this.motionY += 0.05F;
        }
	}

}
