package mods.battlegear2.packet;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.BowHookContainerClass2;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

import java.io.*;

public class SpecialActionPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|Special";
	private EntityPlayer player;
	private Entity entityHit;

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            if(targetPlayer!=null){
	            Entity targetHit = null;
	            if(inputStream.readBoolean()){
	                targetHit = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
	            }else{
	                try{
	                    targetHit = player.worldObj.getEntityByID(inputStream.readInt());
	                }catch (EOFException e){}
	            }
	
	
	
	            ItemStack mainhand = targetPlayer.getCurrentEquippedItem();
	            ItemStack offhand = ((InventoryPlayerBattle)targetPlayer.inventory).getCurrentOffhandWeapon();
	
	            ItemStack quiver = BowHookContainerClass2.getArrowContainer(mainhand, player);
	
	            if(quiver != null){
	                ((IArrowContainer2)quiver.getItem()).setSelectedSlot(quiver,
	                        (((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver)+1) %
	                                ((IArrowContainer2)quiver.getItem()).getSlotCount(quiver));
	            } else if(targetHit != null && targetHit instanceof EntityLivingBase){
	
	                if(offhand != null && offhand.getItem() instanceof IShield){
	                	if(((EntityLivingBase) targetHit).canBePushed()){
		                    double d0 = targetHit.posX - targetPlayer.posX;
		                    double d1;
		
		                    for (d1 = targetHit.posZ - targetPlayer.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D){
		                        d0 = (Math.random() - Math.random()) * 0.01D;
		                    }
		                    double pow = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashPower.effectId, offhand)*0.1D;
		
		                    ((EntityLivingBase) targetHit).knockBack(player, 0, -d0*(1+pow), -d1*(1+pow));
	                	}
	                	if(((EntityLivingBase) targetHit).getDistanceToEntity(player)<2){
	                		float dam = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashDamage.effectId, offhand)*2F;
	                		if(dam>0) {
	                			targetHit.attackEntityFrom(DamageSource.causeThornsDamage(player), dam);
	                			targetHit.playSound("damage.thorns", 0.5F, 1.0F);
	                		}
	                	}
	                    if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER &&
	                            targetHit instanceof EntityPlayer){
	                        PacketDispatcher.sendPacketToPlayer(packet, (Player)targetHit);
	                    }
	
	
	
	                }else if(mainhand != null && offhand != null){
	                    //This will be handeled elsewhere
	                }else if (mainhand != null && mainhand.getItem() instanceof IBattlegearWeapon){
	
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
