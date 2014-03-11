package mods.battlegear2.heraldry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.packet.BattlegearBannerPacket;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 2:33 PM
 */
public class TileEntityFlagPole extends TileEntity implements IFlagHolder{
    private static final int MAX_FLAGS = 4;
    private ArrayList<ItemStack> flags;

    public TileEntityFlagPole(){
        flags = new ArrayList<ItemStack>(MAX_FLAGS);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        int side = BattlegearConfig.banner.getOrient(getBlockMetadata());
        switch (side){
            case 0:
                return AxisAlignedBB.getAABBPool().getAABB(
                        xCoord - flags.size(),
                        yCoord,
                        zCoord,
                        xCoord + flags.size()+1,
                        yCoord + 1, zCoord + 1);
            case 1:
            case 2:
                return AxisAlignedBB.getAABBPool().getAABB(
                        xCoord,
                        yCoord - flags.size(),
                        zCoord,
                        xCoord+1,
                        yCoord+ flags.size()+1, zCoord + 1);
        }

        return AxisAlignedBB.getAABBPool().getAABB(
                xCoord - flags.size(),
                yCoord,
                zCoord,
                xCoord + flags.size()+1,
                yCoord + 1, zCoord + 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        flags = new ArrayList<ItemStack>(MAX_FLAGS);
        for(int i = 0; i < MAX_FLAGS; i++){
            if(par1NBTTagCompound.hasKey("flag"+i)){
                flags.add(ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("flag"+i)));
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        for(int i = 0; i < flags.size(); i++){
            NBTTagCompound flagCompound = new NBTTagCompound();
            flags.get(i).writeToNBT(flagCompound);
            par1NBTTagCompound.setTag("flag" + i, flagCompound);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        return new BattlegearBannerPacket(xCoord, yCoord, zCoord, flags).generatePacket();
    }

    public boolean hasFlag(){
        return flags.size() != 0;
    }

    @Override
    public void clearFlags() {
        flags.clear();
    }

    @Override
    public boolean addFlag(ItemStack flag) {
        if(flags.size() < MAX_FLAGS){
            this.flags.add(flag);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getFlags() {
        return flags;
    }
}
