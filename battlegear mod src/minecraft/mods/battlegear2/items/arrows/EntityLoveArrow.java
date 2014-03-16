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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * An arrow which deals weird effects on living entities
 * @author GotoLink
 *
 */
public class EntityLoveArrow extends AbstractMBArrow{

	public EntityLoveArrow(World world) {
		super(world);
	}
	
	public EntityLoveArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityLoveArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        if(entityHit!=shootingEntity){
            if(entityHit instanceof EntityAgeable){
                EntityAgeable child = ((EntityAgeable) entityHit).createChild((EntityAgeable) entityHit);
                if (child != null && !this.worldObj.isRemote){
                    child.setGrowingAge(-24000);
                    child.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                    this.worldObj.spawnEntityInWorld(child);
                }
                ((EntityAgeable) entityHit).setGrowingAge(-24000);
                setDead();
                return true;
            }else if(entityHit instanceof EntityCreature){
                ((EntityCreature) entityHit).setTarget(null);
                if(((EntityCreature) entityHit).getHeldItem()==null){
                    entityHit.setCurrentItemOrArmor(0, new ItemStack(ItemMBArrow.component[5]));
                }
                setDead();
                return true;
            }else if(entityHit instanceof EntityPlayer){
                EntityItem entityitem = ForgeHooks.onPlayerTossEvent((EntityPlayer) entityHit, ((EntityPlayer) entityHit).getCurrentEquippedItem(), true);
                if(entityitem!=null){
                    entityitem.delayBeforeCanPickup = 0;
                    entityitem.func_145797_a(entityHit.getCommandSenderName());
                }
                if(!((IBattlePlayer)entityHit).isBattlemode())
                    ((EntityPlayer) entityHit).inventory.setInventorySlotContents(((EntityPlayer) entityHit).inventory.currentItem, new ItemStack(ItemMBArrow.component[5]));
                setDead();
                return true;
            }
        }
		return false;
	}

	@Override
	public void onHitGround(int x, int y, int z) {
		
	}

}
