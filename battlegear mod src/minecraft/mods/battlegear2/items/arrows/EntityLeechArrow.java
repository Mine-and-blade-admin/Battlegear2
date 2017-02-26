package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * An arrow which sucks living entities life force to give it to the shooter
 * @author GotoLink
 *
 */
public class EntityLeechArrow extends AbstractMBArrow{
    @GameRegistry.ObjectHolder("minecraft:weakness")
    public static Potion WEAKNESS;
    @GameRegistry.ObjectHolder("minecraft:weakness")
    public static PotionType WEAKNESS_TYPE;
    public static float LEECH_FACTOR = 0.2F;
    public EntityLeechArrow(World par1World){
        super(par1World);
    }

    public EntityLeechArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityLeechArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        if(entityHit instanceof EntityLivingBase){
            float value = ((EntityLivingBase) entityHit).getHealth()* LEECH_FACTOR;//20% of opponent life
            if(entityHit.attackEntityFrom(getLeechDamage(), value)){//Try leech
                if(shootingEntity instanceof EntityLivingBase)
                    ((EntityLivingBase) shootingEntity).heal(value);
                ((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(WEAKNESS, 40));//Weaken the opponent
            }
            ((EntityLivingBase) entityHit).setArrowCountInEntity(((EntityLivingBase) entityHit).getArrowCountInEntity()+1);
            this.setDead();
            return true;
        }
        return false;
    }

    @Override
    public void onHitGround(BlockPos pos) {
        if (!world.isRemote){
            world.spawnEntity(new EntityPotion(world, pos.getX(), pos.getY(), pos.getZ(), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), WEAKNESS_TYPE)));//Splash weakness
        }
        this.setDead();
    }

    public DamageSource getLeechDamage(){
        return DamageSource.causeThornsDamage(shootingEntity).setProjectile();
    }
}
