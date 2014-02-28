package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class MobHookContainerClass {

    public static final int Skell_Arrow_Datawatcher = 25;

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(event.entity instanceof EntitySkeleton){

            EntitySkeleton skeleton = (EntitySkeleton) event.entity;

            try{
                skeleton.getDataWatcher().addObject(Skell_Arrow_Datawatcher, Byte.valueOf((byte)-1));

                for(int i = 0; i < ItemMBArrow.names.length; i++){
                    if(skeleton.getRNG().nextFloat() < BattlegearConfig.skeletonArrowSpawnRate[i]){
                        skeleton.getDataWatcher().updateObject(Skell_Arrow_Datawatcher, Byte.valueOf((byte) i));
                        break;
                    }
                }

            }catch (Exception e){}


        }else if(event.entity.getClass() == EntityArrow.class){
            EntityArrow arrow = ((EntityArrow)event.entity);
            if(arrow.shootingEntity instanceof EntitySkeleton){
                EntitySkeleton skeleton = (EntitySkeleton) arrow.shootingEntity;

                int type = skeleton.getDataWatcher().getWatchableObjectByte(Skell_Arrow_Datawatcher);
                if(type > -1){

                    AbstractMBArrow mbArrow = AbstractMBArrow.generate(type, arrow, skeleton);
                    if(mbArrow != null){
                        EntityLivingBase target = skeleton.getAttackTarget();
                        event.setCanceled(true);

                        double d0 = skeleton.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
                        float pow = MathHelper.sqrt_double(d0) / (15F * 15F);

                        pow = Math.max(0.1F, pow);
                        pow = Math.min(1,pow);

                        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, skeleton.getHeldItem());
                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, skeleton.getHeldItem());
                        mbArrow.setDamage((double)(pow * 2.0F) + skeleton.getRNG().nextGaussian() * 0.25D + (double)((float)skeleton.worldObj.difficultySetting.getDifficultyId() * 0.11F));

                        if (i > 0)
                            mbArrow.setDamage(mbArrow.getDamage() + (double)i * 0.5D + 0.5D);

                        if (j > 0)
                            mbArrow.setKnockbackStrength(j);

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, skeleton.getHeldItem()) > 0 || skeleton.getSkeletonType() == 1)
                            mbArrow.setFire(100);

                        skeleton.worldObj.spawnEntityInWorld(mbArrow);
                    }

                }
            }
        }
    }

}
