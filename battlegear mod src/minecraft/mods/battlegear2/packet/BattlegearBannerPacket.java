package mods.battlegear2.packet;

import mods.battlegear2.api.heraldry.IFlagHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BattlegearBannerPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|Banner";
    private int posX, posY, posZ;
    private List<ItemStack> parts = new ArrayList<ItemStack>();

    public BattlegearBannerPacket(){}

    public BattlegearBannerPacket(int x, int y, int z, List<ItemStack> flags){
        posX = x;
        posY = y;
        posZ = z;
        parts = flags;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(posX);
        out.writeInt(posY);
        out.writeInt(posZ);
        out.writeByte(((byte) parts.size()));
        for(ItemStack f:parts){
            Packet.writeItemStack(f, out);
        }
    }

    @Override
    public void process(DataInputStream in, EntityPlayer player) {
        int size = 0;
        try{
            posX = in.readInt();
            posY = in.readInt();
            posZ = in.readInt();
            size = in.readByte();
            for(int i = 0; i < size; i++){
                parts.add(Packet.readItemStack(in));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TileEntity te = player.worldObj.getBlockTileEntity(posX,posY,posZ);
        if(te != null && te instanceof IFlagHolder){
            ((IFlagHolder) te).clearFlags();
            for(ItemStack flag:parts){
                ((IFlagHolder)te).addFlag(flag);
            }
        }
    }
}
