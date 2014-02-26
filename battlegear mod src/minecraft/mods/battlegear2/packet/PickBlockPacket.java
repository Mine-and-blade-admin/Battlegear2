package mods.battlegear2.packet;

import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public class PickBlockPacket extends AbstractMBPacket{
    public final static String packetName = "MB2|CreaPick";
    private String user;
    private ItemStack stack;
    private int slot;
    private EntityPlayer player;
    public PickBlockPacket(){}
    public PickBlockPacket(String user,ItemStack stack, int slot){
        this.user = user;
        this.stack = stack;
        this.slot = slot;
    }
    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Packet.writeString(user, out);
        out.writeInt(slot);
        Packet.writeItemStack(stack, out);
    }

    @Override
    public void process(DataInputStream inputStream, EntityPlayer player) {
        try {
            this.player = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            if(this.player!=null && this.player.capabilities.isCreativeMode && !((IBattlePlayer)this.player).isBattlemode()){
                slot = inputStream.readInt();
                this.player.inventory.currentItem = slot;
                stack = Packet.readItemStack(inputStream);
                if(!stack.isItemEqual(this.player.getCurrentEquippedItem())){
                    BattlegearUtils.setPlayerCurrentItem(this.player, stack);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }
}
