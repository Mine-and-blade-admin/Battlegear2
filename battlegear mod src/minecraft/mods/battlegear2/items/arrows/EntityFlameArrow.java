package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
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
    
    public EntityFlameArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        isImmuneToFire = true;
    }

    public EntityFlameArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
        isImmuneToFire = true;
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        source.setFireDamage();
        return false;
    }

    @Override
    public void onHitGround(BlockPos posGround) {
        EnumFacing face = EnumFacing.UP;
        if(this.posX - posGround.getX() > 1){
            face = EnumFacing.EAST;
        }
        else if(this.posX - posGround.getX() < 0){
            face = EnumFacing.WEST;
        }
        else if(this.posY - posGround.getY() < 0){
            face = EnumFacing.DOWN;
        }
        else if(this.posZ - posGround.getZ() > 1){
            face = EnumFacing.SOUTH;
        }
        else if(this.posZ - posGround.getZ() < 0){
            face = EnumFacing.NORTH;
        }
        BlockPos pos = posGround.offset(face);
        if (world.isAirBlock(pos) && Blocks.FIRE.canPlaceBlockAt(world, pos)) {
            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public boolean isBurning(){
        return true;
    }
}
