package mods.battlegear2.items.arrows;

import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.items.ItemMBArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class AbstractMBArrow extends EntityArrow {

	public AbstractMBArrow(World par1World){
		super(par1World);
	}
	
    public AbstractMBArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public AbstractMBArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    public abstract boolean onHitEntity(Entity entityHit, DamageSource source, float ammount);

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
        if(arrow != null && skeleton != null && skeleton.getAttackTarget() != null && type<ItemMBArrow.arrows.length){
            try {
				mbArrow = ItemMBArrow.arrows[type].getConstructor(World.class, EntityLivingBase.class, EntityLivingBase.class, float.class, float.class).newInstance(arrow.worldObj, skeleton, skeleton.getAttackTarget(), 1.6F, (float)(14 - skeleton.worldObj.difficultySetting * 4));
			} catch (Exception e) {
				e.printStackTrace();
			}          		
        }
        return mbArrow;
    }
    
    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer){
        if (!this.worldObj.isRemote && this.ticksInGround>0 && this.arrowShake <= 0){
            boolean flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;
            if (flag && !par1EntityPlayer.inventory.addItemStackToInventory(getPickedUpItem())){
            	flag = false;
            }
            if (flag){
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

	public ItemStack getPickedUpItem(){
		return QuiverArrowRegistry.getItem(this.getClass());
	}
}
