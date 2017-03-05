package mods.battlegear2.items.arrows;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
/**
 * An arrow which teleports living entities or blocks on contact
 * @author GotoLink
 *
 */
public class EntityEnderArrow extends AbstractMBArrow{
    public static float tpRange = 32.0F;

    public EntityEnderArrow(World par1World) {
		super(par1World);
	}
	
    public EntityEnderArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityEnderArrow(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        this.setDead();
    	if(entityHit instanceof EntityLivingBase){
            if (!this.world.isRemote){
                if(shootingEntity == null){
                    tryTeleport((EntityLivingBase)entityHit);
                }
                else if(shootingEntity instanceof EntityLivingBase){
                    if (shootingEntity instanceof EntityPlayerMP){
                        if(!((EntityPlayerMP) this.shootingEntity).connection.getNetworkManager().isChannelOpen() || shootingEntity.world != this.world || ((EntityPlayerMP) this.shootingEntity).isPlayerSleeping())
                        return false;
                    }
                    double x = shootingEntity.posX;
                    double y = shootingEntity.posY;
                    double z = shootingEntity.posZ;
                    EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase)shootingEntity, entityHit.posX+0.5F, entityHit.posY, entityHit.posZ+0.5F, getDamageAgainst((EntityLivingBase)shootingEntity));
                    if(handleTeleportEvent(event)) {
                        event = new EnderTeleportEvent((EntityLivingBase) entityHit, x + 0.5F, y, z + 0.5F, getDamageAgainst((EntityLivingBase) entityHit));
                        handleTeleportEvent(event);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Handle calculating teleport damage.
     * Takes into account the feather falling enchanted boots on a player, no matter the type of damage done
     *
     * @param entityHit that will be damaged
     * @return the value of the damage posted into EnderTeleportEvent
     */
    public float getDamageAgainst(EntityLivingBase entityHit) {
        if(entityHit instanceof EntityPlayer){
            int fall = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, entityHit.getItemStackFromSlot(EntityEquipmentSlot.FEET));
            return (float) getDamage() * 2.5F - 0.5F * fall;
        }
        return entityHit.getMaxHealth()/10;
    }

    /**
     * Teleport at random, for automated systems
     *
     * @param entity the entity hit by the arrow
     */
    protected void tryTeleport(EntityLivingBase entity)
    {
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x+ (this.rand.nextDouble() - 0.5D) * tpRange * 2, y+ (double)(this.rand.nextInt(4) - 2), z+ (this.rand.nextDouble() - 0.5D) * tpRange * 2, 0);
        if (MinecraftForge.EVENT_BUS.post(event)){
            return;
        }
        if(entity.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())){
            this.world.playSound(null, x, y, z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }
    }

    @Override
    public void onHitGround(BlockPos pos) {
        this.setDead();
        if(shootingEntity instanceof EntityPlayer && shootingEntity.isSneaking()){
            IBlockState id = world.getBlockState(pos);
            if (id.getBlock().getBlockHardness(id, world, pos) != -1F && this.world.getGameRules().getBoolean("doTileDrops")) {
                Item item = Item.getItemFromBlock(id.getBlock());
                int i = 0;
                if(item.getHasSubtypes())
                    i = id.getBlock().getMetaFromState(id);
                ItemStack stack = new ItemStack(item, 1, i);
                if(world.setBlockToAir(pos) && !((EntityPlayer) shootingEntity).inventory.addItemStackToInventory(stack)){
                    EntityItem entityitem = ForgeHooks.onPlayerTossEvent((EntityPlayer) shootingEntity, stack, true);
                    if(entityitem!=null) {
                        entityitem.setNoPickupDelay();
                        entityitem.setOwner(shootingEntity.getName());
                    }
                }
            }
        }else if(shootingEntity instanceof EntityLivingBase){
            while (pos.getY() < 255 && !(world.isAirBlock(pos) && world.isAirBlock(pos.up()))) {
                pos = pos.up();
                if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                    break;
                }
            }
            if (!world.isAirBlock(pos)) {
                while (pos.getY() > 0 && !(world.isAirBlock(pos) && world.isAirBlock(pos.down()))) {
                    pos = pos.down();
                    if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                        break;
                    }
                }
            }
            if (!world.isAirBlock(pos)) {
                return;
            }
            if (shootingEntity instanceof EntityPlayerMP){
                if(!((EntityPlayerMP) this.shootingEntity).connection.getNetworkManager().isChannelOpen() || shootingEntity.world != this.world || ((EntityPlayerMP) this.shootingEntity).isPlayerSleeping())
                return;
            }
            handleTeleportEvent(new EnderTeleportEvent((EntityLivingBase) shootingEntity, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, getDamageAgainst((EntityLivingBase) shootingEntity)));
        }
    }

    /**
     * The type of damage done when teleporting entities, almost identical to EnderPearl damage
     */
    public DamageSource getEnderDamage(){
        return new DamageSource("fall").setDamageBypassesArmor().setProjectile();
    }

    /**
     * Most generic handling of EnderTeleportEvent
     *
     * @param event to handle
     * @return true only if the event is not cancelled
     */
    private boolean handleTeleportEvent(EnderTeleportEvent event){
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            for (int i = 0; i < 32; ++i){
                this.world.spawnParticle(EnumParticleTypes.PORTAL, event.getTargetX(), event.getTargetY() + this.rand.nextDouble() * 2.0D, event.getTargetZ(), this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
            }
            event.getEntity().dismountRidingEntity();
            event.getEntity().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            event.getEntity().fallDistance = 0.0F;
            event.getEntity().attackEntityFrom(getEnderDamage(), event.getAttackDamage());
            return true;
        }
        return false;
    }
}
