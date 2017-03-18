package mods.battlegear2.items.arrows;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.List;
/**
 * An arrow which deals damage through armors and shields, shears things, and breaks glass blocks
 * @author GotoLink
 *
 */
public class EntityPiercingArrow extends AbstractMBArrow{

	public EntityPiercingArrow(World par1World) {
		super(par1World);
	}
	
	public EntityPiercingArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityPiercingArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

    @Override
    public void onUpdate() {
        Vec3d a = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d b = new Vec3d(this.posX + this.motionX * 1.5, this.posY + this.motionY * 1.5, this.posZ + this.motionZ * 1.5);
        RayTraceResult movingobjectposition = this.world.rayTraceBlocks(a, b, false, true, true);

        if (ticksInGround == 0 && movingobjectposition != null && movingobjectposition.entityHit == null){
            ticksInGround ++;
            onHitGround(movingobjectposition.getBlockPos());
        }
        super.onUpdate();
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
	    if(!source.getDamageType().equals("piercing.arrow")) {
            if(entityHit != this.shootingEntity && entityHit.attackEntityFrom(getPiercingDamage(), ammount)) {
                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            }
            if (entityHit instanceof IShearable) {
                if (((IShearable) entityHit).isShearable(ItemStack.EMPTY, world, new BlockPos(entityHit))) {
                    List<ItemStack> drops = ((IShearable) entityHit).onSheared(ItemStack.EMPTY, world, new BlockPos(entityHit), 1);
                    if(!world.isRemote) {
                        for (ItemStack stack : drops) {
                            EntityItem ent = entityHit.entityDropItem(stack, 1.0F);
                            if (ent != null) {
                                ent.motionY += rand.nextFloat() * 0.05F;
                                ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                                ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                            }
                        }
                    }
                }
            }
            return true;
        }
		return false;
	}

    public DamageSource getPiercingDamage(){
        return new EntityDamageSourceIndirect("piercing.arrow", this, shootingEntity).setProjectile().setDamageBypassesArmor();
    }

	@Override
	public void onHitGround(BlockPos pos) {
        boolean broken = false;
        if(canBreakBlocks()) {
            IBlockState block = world.getBlockState(pos);
            if (block.getMaterial() == Material.GLASS) {
                broken = world.destroyBlock(pos, false);
            } else if (block.getBlock() instanceof IShearable) {
                IShearable target = (IShearable) block.getBlock();
                if (target.isShearable(ItemStack.EMPTY, world, pos)) {
                    List<ItemStack> drops = target.onSheared(ItemStack.EMPTY, world, pos, 1);
                    if (!world.isRemote) {
                        for (ItemStack stack : drops) {
                            float f = 0.7F;
                            double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(world, (double) pos.getY() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
                            entityitem.setDefaultPickupDelay();
                            world.spawnEntity(entityitem);
                        }
                    }
                    broken = world.setBlockToAir(pos);
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
