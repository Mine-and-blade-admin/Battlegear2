package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PickBlockPacket extends AbstractMBPacket{
    public final static String packetName = "MB2|CreaPick";
    private String user;
    private ItemStack stack;
    private int slot;
    private EntityPlayer player;
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
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.player = player.worldObj.getPlayerEntityByName(ByteBufUtils.readUTF8String(inputStream));
        if(this.player!=null && this.player.capabilities.isCreativeMode && !((IBattlePlayer)this.player).isBattlemode()){
            slot = inputStream.readInt();
            this.player.inventory.currentItem = slot;
            stack = ByteBufUtils.readItemStack(inputStream);
            if(!stack.isItemEqual(this.player.getCurrentEquippedItem())){
                BattlegearUtils.setPlayerCurrentItem(this.player, stack);
            }
        }
    }
}
