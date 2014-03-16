package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PickBlockPacket extends AbstractMBPacket{
    public final static String packetName = "MB2|CreaPick";
    private String user;
    private ItemStack stack;
    private int slot;
    public PickBlockPacket(){}
    public PickBlockPacket(EntityPlayer user,ItemStack stack, int slot){
        this.user = user.getCommandSenderName();
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, user);
        out.writeInt(slot);
        ByteBufUtils.writeItemStack(out, stack);
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer fake) {
        user = ByteBufUtils.readUTF8String(inputStream);
        EntityPlayer player = fake.worldObj.getPlayerEntityByName(user);
        if(player!=null && !((IBattlePlayer)player).isBattlemode()){
            slot = inputStream.readInt();
            if(slot>=0 && slot<9){
                player.inventory.currentItem = slot;
                stack = ByteBufUtils.readItemStack(inputStream);
                if(player.capabilities.isCreativeMode && !ItemStack.areItemStacksEqual(stack, player.getCurrentEquippedItem())){
                    BattlegearUtils.setPlayerCurrentItem(player, stack);
                }
                if(player instanceof EntityPlayerMP)
                    Battlegear.packetHandler.sendPacketToPlayer(this.generatePacket(),(EntityPlayerMP) player);
            }
        }
    }
}
