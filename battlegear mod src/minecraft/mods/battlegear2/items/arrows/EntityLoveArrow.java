package mods.battlegear2.items.arrows;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.items.ItemMBArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * An arrow which deals weird effects on living entities
 * @author GotoLink
 *
 */
public class EntityLoveArrow extends AbstractMBArrow{
    public static int AGE_TIMER = -24000, PICKUP_TIME = 10;
	public EntityLoveArrow(World world) {
		super(world);
	}
	
	public EntityLoveArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityLoveArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        if(entityHit!=shootingEntity){
            if(entityHit instanceof EntityAgeable){
                if(!((EntityAgeable) entityHit).isChild()) {
                    EntityAgeable child = ((EntityAgeable) entityHit).createChild((EntityAgeable) entityHit);
                    if (child != null && !this.world.isRemote) {
                        child.setGrowingAge(AGE_TIMER);
                        child.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                        this.world.spawnEntity(child);
                    }
                }
                ((EntityAgeable) entityHit).setGrowingAge(AGE_TIMER);//Fountain of youth
                setDead();
                return true;
            }else if(entityHit instanceof EntityCreature){
                ((EntityCreature) entityHit).setAttackTarget(null);//Try peacefulness
                if(((EntityCreature) entityHit).getHeldItemMainhand().isEmpty()){
                    ((EntityCreature) entityHit).setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ItemMBArrow.component[5]));
                }else if(((EntityCreature) entityHit).getHeldItemOffhand().isEmpty()){
                    ((EntityCreature) entityHit).setHeldItem(EnumHand.OFF_HAND, new ItemStack(ItemMBArrow.component[5]));
                }
                setDead();
                return true;
            }else if(entityHit instanceof EntityPlayer){
                EntityItem entityitem = ForgeHooks.onPlayerTossEvent((EntityPlayer) entityHit, ((EntityPlayer) entityHit).getHeldItemMainhand(), true);
                if(entityitem!=null){
                    entityitem.setPickupDelay(PICKUP_TIME);
                    entityitem.setOwner(entityHit.getName());
                }
                if(!((IBattlePlayer)entityHit).isBattlemode())
                    ((EntityPlayer) entityHit).setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ItemMBArrow.component[5]));//Get a cookie
                setDead();
                return true;
            }
        }
		return false;
	}

	@Override
	public void onHitGround(BlockPos pos) {
		
	}
}
