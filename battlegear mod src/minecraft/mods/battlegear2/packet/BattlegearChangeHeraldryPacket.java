package mods.battlegear2.packet;

import mods.battlegear2.api.heraldry.IHeraldryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

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
    public void write(DataOutput out) throws IOException {
        out.writeUTF(playerName);
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void process(DataInputStream in, EntityPlayer player) {
        try{
            playerName = in.readUTF();
            data = new byte[in.readInt()];
            int size = in.read(data);
            if(size!=data.length){
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
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
