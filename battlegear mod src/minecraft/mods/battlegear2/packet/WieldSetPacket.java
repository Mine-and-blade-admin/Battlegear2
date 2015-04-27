package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Olivier on 25/04/2015.
 */
public class WieldSetPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|WieldSet";
    public WieldSetPacket(){}

    private ItemStack stack;
    private String type;
    public WieldSetPacket(ItemStack itemStack, String text){
        stack = itemStack;
        type = text;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, type);
        ByteBufUtils.writeItemStack(out, stack);
    }

    @Override
    public void process(ByteBuf in, EntityPlayer player) {
        type = ByteBufUtils.readUTF8String(in);
        stack = ByteBufUtils.readItemStack(in);
        if(stack!=null && player.worldObj.isRemote) {
            WeaponRegistry.Wield.valueOf(type).setUsable(stack);
        }
    }
}
