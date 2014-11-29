package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class BattlegearChangeHeraldryPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|Heraldry";
    private byte[] data;
    public BattlegearChangeHeraldryPacket() {
    }

    public BattlegearChangeHeraldryPacket(byte[] dat){
        data = dat;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    public void process(ByteBuf in, EntityPlayer player) {
        try{
            data = new byte[in.readInt()];
            in.readBytes(data);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        if(player != null){
            ItemStack targetEquip = player.getCurrentEquippedItem();
            if(targetEquip != null && targetEquip.getItem() instanceof IHeraldryItem){
                ((IHeraldryItem)targetEquip.getItem()).setHeraldry(targetEquip, data);
            }
        }
    }
}
