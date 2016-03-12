package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:47 PM
 */
public final class BattlegearAnimationPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|Animation";
	private EnumBGAnimations animation;
	private String username;

    public BattlegearAnimationPacket(EnumBGAnimations animation, EntityPlayer user) {
    	this.animation = animation;
    	this.username = user.getName();
    }

    public BattlegearAnimationPacket() {
	}
    
	@Override
    public void process(ByteBuf in,EntityPlayer player) {
        try {
            animation = EnumBGAnimations.values()[in.readInt()];
            username = ByteBufUtils.readUTF8String(in);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if (username != null && animation != null) {
            EntityPlayer entity = player.worldObj.getPlayerEntityByName(username);
            if(entity!=null){
                if (entity.worldObj instanceof WorldServer) {
                    ((WorldServer) entity.worldObj).getEntityTracker().sendToAllTrackingEntity(entity, this.generatePacket());
                }
                animation.processAnimation((IBattlePlayer)entity);
            }
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(animation.ordinal());
        ByteBufUtils.writeUTF8String(out, username);
	}
}
