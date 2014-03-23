package mods.battlegear2.items.arrows;

import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.items.ItemMBArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
            onHitGround(field_145791_d, field_145792_e, field_145789_f);
        }
    }

    /**
     * Helper generation method for skeletons
     * @param type the new type of the arrow
     * @param arrow the original arrow fired by the skeleton
     * @param skeleton the shooter
     * @return
     */
    public static AbstractMBArrow generate(int type, EntityArrow arrow, EntitySkeleton skeleton) {
        AbstractMBArrow mbArrow = null;
        if(arrow != null && skeleton != null && skeleton.getAttackTarget() != null && type<ItemMBArrow.arrows.length){
            try {
				mbArrow = ItemMBArrow.arrows[type].getConstructor(World.class, EntityLivingBase.class, EntityLivingBase.class, float.class, float.class).newInstance(arrow.worldObj, skeleton, skeleton.getAttackTarget(), 1.6F, (float)(14 - skeleton.worldObj.difficultySetting.getDifficultyId() * 4));
			} catch (Exception e) {
				e.printStackTrace();
			}          		
        }
        return mbArrow;
    }
    
    @Override//Fixes picking up arrows
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer){
        if (!this.worldObj.isRemote && this.ticksInGround>0 && this.arrowShake <= 0){
            boolean flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;
            if (flag && !tryPickArrow(par1EntityPlayer)){
            	flag = false;
            }
            if (flag){
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
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
        ItemStack arrow = getPickedUpItem();
        if(arrow!=null){
            ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
            if(offhand!=null && offhand.getItem() instanceof IArrowContainer2){
                final int size = arrow.stackSize;
                ItemStack arrowLeft = ((IArrowContainer2) offhand.getItem()).addArrows(offhand, arrow);
                if(arrowLeft==null||arrowLeft.stackSize<size){
                    if(arrowLeft!=null && arrowLeft.stackSize>0)
                        worldObj.spawnEntityInWorld(new EntityItem(worldObj, posX, posY, posZ, arrowLeft));
                    return true;
                }
            }
        }
        return player.inventory.addItemStackToInventory(arrow);
    }

    /**
     * Could be abstracted, but using the registry is easier
     * @return the stack to be picked up, if any
     */
	public ItemStack getPickedUpItem(){
		return QuiverArrowRegistry.getItem(this.getClass());
	}
}
