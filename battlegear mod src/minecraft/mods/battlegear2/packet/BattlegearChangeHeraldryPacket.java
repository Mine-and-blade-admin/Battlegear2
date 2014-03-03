package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BattlegearChangeHeraldryPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|Heraldry";
    private String playerName;
    private byte[] data;
    public BattlegearChangeHeraldryPacket() {
    }

    public BattlegearChangeHeraldryPacket(String user, byte[] dat){
        playerName = user;
        data = dat;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, playerName);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    public void process(ByteBuf in, EntityPlayer player) {
        try{
            playerName = ByteBufUtils.readUTF8String(in);
            data = new byte[in.readInt()];
            in.readBytes(data);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(playerName != null){
            EntityPlayer target = player.worldObj.getPlayerEntityByName(playerName);
            if(target != null){
                ItemStack targetEquip = target.getCurrentEquippedItem();
                if(targetEquip != null && targetEquip.getItem() instanceof IHeraldryItem){
                    ((IHeraldryItem)targetEquip.getItem()).setHeraldry(targetEquip, data);
                }
            }

        }
    }
}
