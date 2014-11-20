package mods.battlegear2.items.arrows;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
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
    public static String PARTICLES = "portal", SOUND = "mob.endermen.portal";
	public EntityEnderArrow(World par1World) {
		super(par1World);
	}
	
    public EntityEnderArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityEnderArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

    @Override
    public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
        this.setDead();
    	if(entityHit instanceof EntityLivingBase){
            if (!this.worldObj.isRemote){
                if(shootingEntity == null){
                    tryTeleport((EntityLivingBase)entityHit);
                }
                else if(shootingEntity instanceof EntityLivingBase){
                    if (shootingEntity instanceof EntityPlayerMP && !(((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.func_147362_b().isChannelOpen() && shootingEntity.worldObj == this.worldObj))
                        return false;
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
            int fall = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, entityHit.getEquipmentInSlot(1));
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
        double x = entity.posX + (this.rand.nextDouble() - 0.5D) * tpRange * 2;
        double y = entity.posY + (double)(this.rand.nextInt(4) - 2);
        double z = entity.posZ + (this.rand.nextDouble() - 0.5D) * tpRange * 2;
        EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0);
        if (MinecraftForge.EVENT_BUS.post(event)){
            return;
        }
        x = entity.posX;
        y = entity.posY;
        z = entity.posZ;
        entity.posX = event.targetX;
        entity.posY = event.targetY;
        entity.posZ = event.targetZ;
        boolean success = false;
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);

        if (this.worldObj.blockExists(i, j, k))
        {
            while (j > 0)
            {
                Block block = this.worldObj.getBlock(i, j - 1, k);
                if (block.getMaterial().blocksMovement())
                {
                    entity.setPosition(entity.posX, entity.posY, entity.posZ);
                    if (this.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty())
                    {
                        success = true;
                        for (int l = 0; l < 128; ++l)
                        {
                            double d6 = (double)l / 127.0D;
                            float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                            double d7 = x + (entity.posX - x) * d6 + (this.rand.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                            double d8 = y + (entity.posY - y) * d6 + this.rand.nextDouble() * (double)entity.height;
                            double d9 = z + (entity.posZ - z) * d6 + (this.rand.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                            this.worldObj.spawnParticle(PARTICLES, d7, d8, d9, (double)f, (double)f1, (double)f2);
                        }
                        this.worldObj.playSoundEffect(x, y, z, SOUND, 1.0F, 1.0F);
                        entity.playSound(SOUND, 1.0F, 1.0F);
                    }
                    break;
                }
                else
                {
                    --entity.posY;
                    --j;
                }
            }
        }

        if (!success)
        {
            entity.setPosition(x, y, z);
        }
    }

    @Override
    public void onHitGround(int x, int y, int z) {
        this.setDead();
        if(shootingEntity instanceof EntityPlayer && shootingEntity.isSneaking()){
            Block id = worldObj.getBlock(x, y, z);
            if(id != Blocks.bedrock && this.worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops")){
                int meta = worldObj.getBlockMetadata(x, y, z);
                worldObj.setBlockToAir(x, y, z);
                ItemStack item = new ItemStack(id, 1, meta);
                if(!((EntityPlayer) shootingEntity).inventory.addItemStackToInventory(item)){
                    EntityItem entityitem = ForgeHooks.onPlayerTossEvent((EntityPlayer) shootingEntity, item, true);
                    if(entityitem!=null) {
                        entityitem.delayBeforeCanPickup = 0;
                        entityitem.func_145797_a(shootingEntity.getCommandSenderName());
                    }
                }
            }
        }else if(shootingEntity instanceof EntityLivingBase){
            while(y < 255 && !(worldObj.isAirBlock(x,y,z) && worldObj.isAirBlock(x,y+1,z))){
                y++;
                if(worldObj.getBlock(x, y, z)==Blocks.bedrock){
                    break;
                }
            }
            if(!worldObj.isAirBlock(x,y,z)){
                while(y > 0 && !(worldObj.isAirBlock(x,y,z) && worldObj.isAirBlock(x,y-1,z))){
                    y--;
                    if(worldObj.getBlock(x, y, z)==Blocks.bedrock){
                        break;
                    }
                }
            }
            if(!worldObj.isAirBlock(x,y,z)){
                return;
            }
            if (shootingEntity instanceof EntityPlayerMP && !(((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.func_147362_b().isChannelOpen() && shootingEntity.worldObj == this.worldObj))
                return;
            handleTeleportEvent(new EnderTeleportEvent((EntityLivingBase)shootingEntity, x+0.5F, y, z+0.5F, getDamageAgainst((EntityLivingBase)shootingEntity)));
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
                this.worldObj.spawnParticle(PARTICLES, event.targetX, event.targetY + this.rand.nextDouble() * 2.0D, event.targetZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
            }
            if (event.entity.isRiding()){
                event.entity.mountEntity(null);
            }
            event.entityLiving.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
            event.entity.fallDistance = 0.0F;
            event.entity.attackEntityFrom(getEnderDamage(), event.attackDamage);
            return true;
        }
        return false;
    }
}
