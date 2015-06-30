package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.BattlemodeHookContainerClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Olivier on 26/06/2015.
 */
public class ReachTargetPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|Reach";
    private int id;
    public ReachTargetPacket(){}
    public ReachTargetPacket(Entity entity){
        id = entity.getEntityId();
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(id);
    }

    @Override
    public void process(ByteBuf in, EntityPlayer player) {
        if(player.worldObj.isRemote || player.isSpectator()){
            return;
        }
        Entity entity = player.worldObj.getEntityByID(in.readInt());
        if (entity instanceof EntityLivingBase && entity != player){
            if (player.getDistanceToEntity(entity) > BattlemodeHookContainerClass.defaultReachDistance(player.capabilities.isCreativeMode)) {
                if (entity.hurtResistantTime != ((EntityLivingBase) entity).maxHurtResistantTime) {
                    player.attackTargetEntityWithCurrentItem(entity);
                }
            }
        }
    }
}
