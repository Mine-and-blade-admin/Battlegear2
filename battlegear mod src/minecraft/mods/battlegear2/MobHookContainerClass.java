package mods.battlegear2;

import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MobHookContainerClass {

    public static final MobHookContainerClass INSTANCE = new MobHookContainerClass();

    private MobHookContainerClass(){}

    /**
     * The key used when registering/loading the arrow data (as byte) into/from the {@link EntitySkeleton}s {@link net.minecraft.network.datasync.EntityDataManager}
     */
    private static final DataParameter<Byte> SKELETON_ARROW = EntityDataManager.createKey(EntitySkeleton.class, DataSerializers.BYTE);

    /**
     * Listen to {@link EntityJoinWorldEvent} :
     * Adds random special {@link EntityArrow}s data to {@link EntitySkeleton}s {@link net.minecraft.network.datasync.EntityDataManager} (for display)
     * Replace the vanilla fired {@link EntityArrow} with the custom {@link AbstractMBArrow} (for actual action)
     * Note: Fails silently
     *
     * Move arrows position slightly to the left when fired from a bow in left hand
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof AbstractSkeleton){

            AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();
            try{
                skeleton.getDataManager().register(SKELETON_ARROW, (byte) -1);
                for(int i = 0; i < ItemMBArrow.names.length; i++){
                    if(skeleton.getRNG().nextFloat() < BattlegearConfig.skeletonArrowSpawnRate[i]){
                        skeleton.getDataManager().set(SKELETON_ARROW, (byte) i);
                        break;
                    }
                }
            }catch (Exception ignored){}

        }else if(event.getEntity() instanceof EntityArrow){
            EntityArrow arrow = ((EntityArrow)event.getEntity());
            if (arrow.shootingEntity instanceof AbstractSkeleton) {
                if(event.getEntity() instanceof EntityTippedArrow && ((EntityTippedArrow) event.getEntity()).getColor() == 0) {
                    AbstractSkeleton skeleton = (AbstractSkeleton) arrow.shootingEntity;
                    if(skeleton.getAttackTarget() != null) {
                        int type = getArrowType(skeleton);
                        if (type > -1) {
                            AbstractMBArrow mbArrow = AbstractMBArrow.generate(type, arrow, skeleton);
                            if (mbArrow != null) {
                                EntityLivingBase target = skeleton.getAttackTarget();
                                float pow = MathHelper.sqrt(skeleton.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ)) / 15F;
                                pow = MathHelper.clamp(pow, 0.1F, 1.0F);

                                mbArrow.setEnchantmentEffectsFromEntity(skeleton, pow);
                                if (skeleton instanceof EntityWitherSkeleton)
                                    mbArrow.setFire(100);
                                double d0 = target.posX - skeleton.posX;
                                double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - mbArrow.posY;
                                double d2 = target.posZ - skeleton.posZ;
                                double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                                mbArrow.setThrowableHeading(d0, d1 + d3 * 0.2D, d2, 1.6F, (float)(14 - skeleton.world.getDifficulty().getDifficultyId() * 4));
                                mbArrow.setDamage(arrow.getDamage());
                                if (skeleton.world.spawnEntity(mbArrow))
                                    event.setCanceled(true);
                            }
                        }
                    }
                }
            }else if(arrow.shootingEntity instanceof EntityPlayer){
                if(BattlegearUtils.isPlayerInBattlemode((EntityPlayer) arrow.shootingEntity)){
                    ItemStack offhand = ((EntityPlayer) arrow.shootingEntity).getHeldItemOffhand();
                    if(!offhand.isEmpty() && BattlegearUtils.isBow(offhand.getItem())){
                        arrow.setPosition(arrow.posX+2*(double)(MathHelper.cos(arrow.shootingEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.16F), arrow.posY, arrow.posZ+2*(double)(MathHelper.sin(arrow.shootingEntity.rotationYaw / 180.0F * (float)Math.PI) * 0.16F));
                    }
                }
            }
        }
    }

    private int getArrowType(AbstractSkeleton skeleton){
        int type;
        try {
            type = skeleton.getDataManager().get(SKELETON_ARROW);
        }catch (Exception handled){
            type = -1;
        }
        return type;
    }

    public ItemStack getArrowForMob(EntitySkeleton skeleton){
        int type = getArrowType(skeleton);
        if(type>-1)
            return new ItemStack(BattlegearConfig.MbArrows, 1, type);
        return new ItemStack(Items.ARROW);
    }

}
