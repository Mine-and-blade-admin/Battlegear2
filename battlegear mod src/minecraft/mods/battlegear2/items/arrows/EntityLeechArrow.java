package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
/**
 * An arrow which sucks living entities life force to give it to the shooter
 * @author GotoLink
 *
 */
public class EntityLeechArrow extends AbstractMBArrow{

    public EntityLeechArrow(World par1World){
        super(par1World);
    }

    public EntityLeechArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityLeechArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        if(shootingEntity instanceof EntityLivingBase && entityHit instanceof EntityLivingBase){
            float value = ((EntityLivingBase) entityHit).getHealth()* 0.2F;//20% of opponent life
            if(entityHit.attackEntityFrom(getLeechDamage(), value)){//Try leech
                ((EntityLivingBase) shootingEntity).heal(value);
                ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 40));//Weaken the opponent
            }
            ((EntityLivingBase) entityHit).setArrowCountInEntity(((EntityLivingBase) entityHit).getArrowCountInEntity()+1);
            this.setDead();
            return true;
        }
        return false;
    }

    @Override
    public void onHitGround(int x, int y, int z) {
        if (!worldObj.isRemote){
            worldObj.spawnEntityInWorld(new EntityPotion(worldObj, x, y, z, new ItemStack(Items.potionitem, 1, 16392)));//Splash weakness
        }
        this.setDead();
    }

    public DamageSource getLeechDamage(){
        return DamageSource.causeThornsDamage(shootingEntity).setProjectile();
    }
}
