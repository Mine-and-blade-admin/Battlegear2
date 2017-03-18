package mods.battlegear2.items.arrows;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class AbstractMBArrow extends EntityArrow {

	public AbstractMBArrow(World par1World){
		super(par1World);
	}
	
    public AbstractMBArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public AbstractMBArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

    public abstract boolean onHitEntity(Entity entityHit, DamageSource source, float ammount);

    public abstract void onHitGround(BlockPos pos);

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(ticksInGround == 1 && isEntityAlive()){
            onHitGround(new BlockPos(xTile, yTile, zTile));
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn){
        if(raytraceResultIn.entityHit==null){
            IBlockState iblockstate = this.world.getBlockState(raytraceResultIn.getBlockPos());
            if(iblockstate.getMaterial()!= Material.AIR){
                onHitGround(raytraceResultIn.getBlockPos());
            }
        }
        super.onHit(raytraceResultIn);
    }
    
    @Override//Fixes picking up arrows
    public void onCollideWithPlayer(@Nonnull EntityPlayer par1EntityPlayer){
        if (!this.world.isRemote && this.ticksInGround>0 && this.arrowShake <= 0){
            if (this.pickupStatus == PickupStatus.ALLOWED && !tryPickArrow(par1EntityPlayer)){
            	return;
            }
            boolean flag = this.pickupStatus == PickupStatus.ALLOWED || this.pickupStatus == PickupStatus.CREATIVE_ONLY && par1EntityPlayer.capabilities.isCreativeMode;
            if (flag){
                this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);//That second parameter is unused
                this.setDead();
            }
        }
    }

    /**
     * The actual act of picking up an arrow, taken out of the colliding event, just in case
     * @param player trying to pick up the arrow
     * @return false if the arrow couldn't be added to the player inventory
     */
    public boolean tryPickArrow(EntityPlayer player){
        ItemStack arrow = getArrowStack();
        if(!arrow.isEmpty()){
            boolean hasPickUp = false;
            ItemStack temp = addInQuiver(player.getHeldItemMainhand(), arrow);
            if(temp.isEmpty()){
                return true;
            }else if(temp != arrow){
                hasPickUp = true;
                arrow = temp;
            }
            temp = addInQuiver(player.getHeldItemOffhand(), arrow);
            if(temp.isEmpty()){
                return true;
            }else if(temp != arrow){
                hasPickUp = true;
                arrow = temp;
            }
            if(hasPickUp){
                if(arrow.getCount()>0)
                    world.spawnEntity(new EntityItem(world, posX, posY, posZ, arrow));
                return true;
            }
        }
        return player.inventory.addItemStackToInventory(arrow);
    }

    private ItemStack addInQuiver(ItemStack quiver, ItemStack arrow){
        if(quiver.getItem() instanceof IArrowContainer2){
            final int size = arrow.getCount();
            ItemStack arrowLeft = ((IArrowContainer2) quiver.getItem()).addArrows(quiver, arrow);
            if(arrowLeft.isEmpty()){
                return ItemStack.EMPTY;
            }else if(arrowLeft.getCount() < size){
                return arrowLeft.copy();
            }
        }
        return arrow;
    }

    /**
     * Could be abstracted, but using the registry is easier
     * @return the stack to be picked up, if any
     */
    @Override
    @Nonnull
    protected ItemStack getArrowStack(){
		return QuiverArrowRegistry.getItem(this.getClass());
	}


    public boolean canBreakBlocks(){
        return !(this.shootingEntity instanceof EntityMob) || this.world.getGameRules().getBoolean("mobGriefing");
    }
}
