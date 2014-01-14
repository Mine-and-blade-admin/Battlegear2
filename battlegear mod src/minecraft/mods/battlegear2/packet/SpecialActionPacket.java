package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

import mods.battlegear2.api.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class SpecialActionPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|Special";
	private EntityPlayer player;
	private Entity entityHit;

    @Override
    public void process(DataInputStream inputStream, EntityPlayer player) {
        try {
            this.player = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            if(this.player!=null){
	            if(inputStream.readBoolean()){
	            	entityHit = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
	            }else{
	                try{
	                	entityHit = player.worldObj.getEntityByID(inputStream.readInt());
	                }catch (EOFException e){}
	            }
	
	            ItemStack mainhand = this.player.getCurrentEquippedItem();
	            ItemStack offhand = ((InventoryPlayerBattle)this.player.inventory).getCurrentOffhandWeapon();
	
	            ItemStack quiver = QuiverArrowRegistry.getArrowContainer(mainhand, player);
	
	            if(quiver != null){
	                ((IArrowContainer2)quiver.getItem()).setSelectedSlot(quiver,
	                        (((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver)+1) %
	                                ((IArrowContainer2)quiver.getItem()).getSlotCount(quiver));
	            } else if(entityHit != null && entityHit instanceof EntityLivingBase){
	
	                if(offhand != null && offhand.getItem() instanceof IShield){
	                	if(entityHit.canBePushed()){
		                    double d0 = entityHit.posX - this.player.posX;
		                    double d1;
		
		                    for (d1 = entityHit.posZ - this.player.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D){
		                        d0 = (Math.random() - Math.random()) * 0.01D;
		                    }
		                    double pow = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashPower.effectId, offhand)*0.1D;
		
		                    ((EntityLivingBase) entityHit).knockBack(player, 0, -d0*(1+pow), -d1*(1+pow));
	                	}
	                	if(entityHit.getDistanceToEntity(player)<2){
	                		float dam = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashDamage.effectId, offhand)*2F;
	                		if(dam>0) {
	                			entityHit.attackEntityFrom(DamageSource.causeThornsDamage(player), dam);
	                			entityHit.playSound("damage.thorns", 0.5F, 1.0F);
	                		}
	                	}
	                    if(FMLCommonHandler.instance().getEffectiveSide().isServer() &&
	                    		entityHit instanceof EntityPlayer){
	                        PacketDispatcher.sendPacketToPlayer(this.generatePacket(), (Player)entityHit);
	                    }
	
	
	
	                }else if(mainhand != null && offhand != null){
	                    //This will be handeled elsewhere
	                }else if (mainhand != null && mainhand.getItem() instanceof IBattlegearWeapon){
	                	//What's the plan here ?
	                }
	                else if(mainhand != null){
	
	                }
	
	            }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }

    public SpecialActionPacket(EntityPlayer player, ItemStack mainhand, ItemStack offhand, Entity entityHit) {
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
	public void write(DataOutput out) throws IOException {
		boolean isPlayer = entityHit instanceof EntityPlayer;

        Packet.writeString(player.username, out);

        out.writeBoolean(isPlayer);
        if(isPlayer){
            Packet.writeString(((EntityPlayer) entityHit).username, out);
        }else{
            if(entityHit != null)
                out.writeInt(entityHit.entityId);
        }
	}
}
