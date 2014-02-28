package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:47 PM
 */
public class BattlegearAnimationPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|Animation";
	private EnumBGAnimations animation;
	private String username;

    public BattlegearAnimationPacket(EnumBGAnimations animation, EntityPlayer user) {
    	this.animation = animation;
    	this.username = user.getCommandSenderName();
    }

    public BattlegearAnimationPacket() {
	}
    
	@Override
    public void process(ByteBuf in,EntityPlayer player) {
        animation = EnumBGAnimations.values()[in.readInt()];
        username = ByteBufUtils.readUTF8String(in);
        if (username != null && animation != null) {
            EntityPlayer entity = player.worldObj.getPlayerEntityByName(username);
            if(entity!=null){
                if (!player.worldObj.isRemote) {
                    ((WorldServer) player.worldObj).getEntityTracker().func_151247_a(entity, this.generatePacket());
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
