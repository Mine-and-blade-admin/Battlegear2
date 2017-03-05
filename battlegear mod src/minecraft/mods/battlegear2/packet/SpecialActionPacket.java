package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.quiver.SwapArrowEvent;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.enchantments.BaseEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public final class SpecialActionPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|Special";
	private EntityPlayer player;
	private Entity entityHit;

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        try {
            this.player = player.world.getPlayerEntityByUUID(UUID.fromString(ByteBufUtils.readUTF8String(inputStream)));
            if (inputStream.readBoolean()) {
                entityHit = player.world.getPlayerEntityByUUID(UUID.fromString(ByteBufUtils.readUTF8String(inputStream)));
            } else {
                entityHit = player.world.getEntityByID(inputStream.readInt());
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        if(this.player!=null){
            if (entityHit instanceof EntityLivingBase) {
                ItemStack offhand = this.player.getHeldItemOffhand();
                if (offhand.getItem() instanceof IShield) {
                    if (entityHit.canBePushed()) {
                        double d0 = entityHit.posX - this.player.posX;
                        double d1;
                        for (d1 = entityHit.posZ - this.player.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }
                        float pow = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashPower, offhand) * 0.1F;
                        ((EntityLivingBase) entityHit).knockBack(this.player, pow, -d0, -d1);
                    }
                    if (entityHit.getDistanceToEntity(this.player) < 2) {
                        float dam = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashDamage, offhand) * 2F;
                        if (dam > 0) {
                            entityHit.attackEntityFrom(DamageSource.causeThornsDamage(this.player), dam);
                            entityHit.playSound(SoundEvents.ENCHANT_THORNS_HIT, 0.5F, 1.0F);
                        }
                    }
                    if (!this.player.world.isRemote && entityHit instanceof EntityPlayerMP) {
                        Battlegear.packetHandler.sendPacketToPlayer(this.generatePacket(), (EntityPlayerMP) entityHit);
                    }
                }
            }else{
                ItemStack quiver = QuiverArrowRegistry.getArrowContainer(this.player);
                if(!quiver.isEmpty()){
                    SwapArrowEvent swapEvent = new SwapArrowEvent(this.player, quiver);
                    if(!MinecraftForge.EVENT_BUS.post(swapEvent) && swapEvent.slotStep!=0) {
                        ((IArrowContainer2) quiver.getItem()).setSelectedSlot(quiver, swapEvent.getNextSlot());
                        if (this.player instanceof EntityPlayerMP) {
                            Battlegear.packetHandler.sendPacketToPlayer(this.generatePacket(), (EntityPlayerMP) this.player);
                        }
                    }
                }
            }
        }
    }

    public SpecialActionPacket(EntityPlayer player, Entity entityHit) {
    	this.player = player;
    	this.entityHit = entityHit;
    }

	public SpecialActionPacket() {
	}

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, player.getCachedUniqueIdString());

        boolean isPlayer = entityHit instanceof EntityPlayer;
        out.writeBoolean(isPlayer);
        if(isPlayer){
            ByteBufUtils.writeUTF8String(out, entityHit.getCachedUniqueIdString());
        }else{
            out.writeInt(entityHit != null?entityHit.getEntityId():-1);
        }
	}
}
