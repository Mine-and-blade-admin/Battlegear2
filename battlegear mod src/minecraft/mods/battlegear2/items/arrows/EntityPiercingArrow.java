package mods.battlegear2.items.arrows;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
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
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
		entityHit.attackEntityFrom(DamageSource.generic, ammount);
		if(!worldObj.isRemote && entityHit instanceof IShearable){
			if(((IShearable)entityHit).isShearable(null, worldObj, (int) entityHit.posX, (int) entityHit.posY, (int) entityHit.posZ)){
				ArrayList<ItemStack> drops = ((IShearable)entityHit).onSheared(null, worldObj, (int) entityHit.posX, (int) entityHit.posY, (int) entityHit.posZ, 1);
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

	@Override
	public void onHitGround(int x, int y, int z) {
		if(worldObj.getBlockMaterial(x, y, z) == Material.glass){
			Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
			if(block != null){
				worldObj.playAuxSFX(2001, x, y, z, block.blockID + (worldObj.getBlockMetadata(x, y, z) << 12));
				worldObj.setBlockToAir(x, y, z);
			}
		}else if (!worldObj.isRemote){
			int id = worldObj.getBlockId(x, y, z);
			if(Block.blocksList[id] instanceof IShearable){
				IShearable target = (IShearable)Block.blocksList[id];
				if (target.isShearable(null, worldObj, x, y, z)){
					ArrayList<ItemStack> drops = target.onSheared(null, worldObj, x, y, z, 1);
					Random rand = new Random();
					for(ItemStack stack : drops){
	                    float f = 0.7F;
	                    double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	                    double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	                    double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
	                    EntityItem entityitem = new EntityItem(worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
	                    entityitem.delayBeforeCanPickup = 10;
	                    worldObj.spawnEntityInWorld(entityitem);
					}
                }
			}
		}
	}
}
