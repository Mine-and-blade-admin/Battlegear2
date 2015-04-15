package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public final class MobHookContainerClass {

    public static final MobHookContainerClass INSTANCE = new MobHookContainerClass();

    private MobHookContainerClass(){}

    /**
     * The key used when registering/loading the arrow data (as byte) into/from the {@link EntitySkeleton}s {@link net.minecraft.entity.DataWatcher}
     */
    public static final int Skell_Arrow_Datawatcher = 25;

    /**
     * Listen to {@link EntityJoinWorldEvent} :
     * Adds random special {@link EntityArrow}s data to {@link EntitySkeleton}s {@link net.minecraft.entity.DataWatcher} (for display)
     * Replace the vanilla fired {@link EntityArrow} with the custom {@link AbstractMBArrow} (for actual action)
     * Note: Fails silently
     *
     * Move arrows position slightly to the left when fired from a bow in left hand
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(event.entity instanceof EntitySkeleton){

            EntitySkeleton skeleton = (EntitySkeleton) event.entity;
            try{
                skeleton.getDataWatcher().addObject(Skell_Arrow_Datawatcher, (byte) -1);
                for(int i = 0; i < ItemMBArrow.names.length; i++){
                    if(skeleton.getRNG().nextFloat() < BattlegearConfig.skeletonArrowSpawnRate[i]){
                        skeleton.getDataWatcher().updateObject(Skell_Arrow_Datawatcher, (byte) i);
                        break;
                    }
                }
            }catch (Exception ignored){}

        }else if(event.entity instanceof EntityArrow){
            EntityArrow arrow = ((EntityArrow)event.entity);
            if (arrow.shootingEntity instanceof EntitySkeleton) {
                if(event.entity.getClass() == EntityArrow.class) {
                    EntitySkeleton skeleton = (EntitySkeleton) arrow.shootingEntity;

                    int type = getArrowType(skeleton);
                    if (type > -1) {

                        AbstractMBArrow mbArrow = AbstractMBArrow.generate(type, arrow, skeleton);
                        if (mbArrow != null) {
                            EntityLivingBase target = skeleton.getAttackTarget();
                            event.setCanceled(true);
                            //Extracted from EntitySkeleton#attackEntityWithRangedAttack
                            double d0 = skeleton.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
                            float pow = MathHelper.sqrt_double(d0) / (15F * 15F);

                            pow = Math.max(0.1F, pow);
                            pow = Math.min(1, pow);

                            int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, skeleton.getHeldItem());
                            int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, skeleton.getHeldItem());
                            mbArrow.setDamage((double) (pow * 2.0F) + skeleton.getRNG().nextGaussian() * 0.25D + (double) ((float) skeleton.worldObj.difficultySetting.getDifficultyId() * 0.11F));

                            if (i > 0)
                                mbArrow.setDamage(mbArrow.getDamage() + (double) i * 0.5D + 0.5D);

                            if (j > 0)
                                mbArrow.setKnockbackStrength(j);

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, skeleton.getHeldItem()) > 0 || skeleton.getSkeletonType() == 1)
                                mbArrow.setFire(100);

                            skeleton.worldObj.spawnEntityInWorld(mbArrow);
                        }

                    }
                }
            }else if(arrow.shootingEntity instanceof EntityPlayer){
                if(BattlegearUtils.isPlayerInBattlemode((EntityPlayer) arrow.shootingEntity)){
                    ItemStack offhand = ((InventoryPlayerBattle)((EntityPlayer) arrow.shootingEntity).inventory).getCurrentOffhandWeapon();
                    if(offhand!=null && BattlegearUtils.isBow(offhand.getItem())){
                        arrow.setPosition(arrow.posX+2*(double)(MathHelper.cos(arrow.shootingEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.16F), arrow.posY, arrow.posZ+2*(double)(MathHelper.sin(arrow.shootingEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.16F));
                    }
                }
            }
        }
    }

    private int getArrowType(EntitySkeleton skeleton){
        int type;
        try {
            type = skeleton.getDataWatcher().getWatchableObjectByte(Skell_Arrow_Datawatcher);
        }catch (Exception handled){
            type = -1;
        }
        return type;
    }

    public ItemStack getArrowForMob(EntitySkeleton skeleton){
        int type = getArrowType(skeleton);
        if(type>-1)
            return new ItemStack(BattlegearConfig.MbArrows, 1, type);
        return new ItemStack(Items.arrow);
    }

}
