package mods.battlegear2.packet;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
    public void process(DataInputStream inputStream, EntityPlayer fake) {
        try {
            user = Packet.readString(inputStream, 30);
            EntityPlayer player = fake.worldObj.getPlayerEntityByName(user);
            if(player!=null && !((IBattlePlayer)player).isBattlemode()){
                slot = inputStream.readInt();
            if(slot>=0 && slot<9){
                player.inventory.currentItem = slot;
                stack = Packet.readItemStack(inputStream);
                if(player.capabilities.isCreativeMode && !ItemStack.areItemStacksEqual(stack, player.getCurrentEquippedItem())){
                    BattlegearUtils.setPlayerCurrentItem(player, stack);
                }
                if(player instanceof EntityPlayerMP){
                    PacketDispatcher.sendPacketToPlayer(this.generatePacket(), (Player)player);
                }
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }
}
