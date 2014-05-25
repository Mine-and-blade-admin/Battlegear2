package mods.battlegear2.heraldry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.items.HeraldryCrest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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
    public boolean receiveUpdates = false;
    public int side;

    public TileEntityFlagPole(){
        flags = new ArrayList<ItemStack>(MAX_FLAGS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
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
        super.readFromNBT(par1NBTTagCompound);;
        side = par1NBTTagCompound.getInteger("orientation");
        flags = new ArrayList<ItemStack>(MAX_FLAGS);
        for(int i = 0; i < MAX_FLAGS; i++){
            if(par1NBTTagCompound.hasKey("flag"+i)){
                flags.add(ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("flag"+i)));
            }
        }
        receiveUpdates = par1NBTTagCompound.getBoolean("hasUpdate");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("orientation", side);
        for(int i = 0; i < flags.size(); i++){
            NBTTagCompound flagCompound = new NBTTagCompound();
            flags.get(i).writeToNBT(flagCompound);
            par1NBTTagCompound.setTag("flag"+i, flagCompound);
        }
        par1NBTTagCompound.setBoolean("hasUpdate", receiveUpdates);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound flagCompound = new NBTTagCompound();
        writeToNBT(flagCompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, flagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void clearFlags() {
        flags.clear();
    }

    @Override
    public boolean addFlag(ItemStack flag) {
        if(flag.getItem() instanceof HeraldryCrest && flags.size() < MAX_FLAGS){
            this.flags.add(flag);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getFlags() {
        return flags;
    }

    @Override
    public float getTextureDimensions(int metadata, int section) {
        return ((BlockFlagPole)this.getBlockType()).getTextDim(metadata, section);
    }

    @Override
    public int getOrientation(int metadata) {
        return side;
    }

    @Override
    public boolean canUpdate(){
        return receiveUpdates;
    }

    @Override
    public void updateEntity() {
        if(!getWorldObj().isRemote && canUpdate() && getWorldObj().rand.nextInt(100) == 0){
            List entities = getWorldObj().getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getAABBPool().getAABB(xCoord-3, yCoord, zCoord-3, xCoord + 3, yCoord + 1, zCoord + 3));
            if(entities.isEmpty())
                spawnUnit();
        }
    }

    public void spawnUnit(){
        //getWorldObj().spawnEntityInWorld(new EntityMBUnit(getWorldObj()));
    }
}
