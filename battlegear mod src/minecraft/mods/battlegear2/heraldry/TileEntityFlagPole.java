package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.items.HeraldryCrest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 2:33 PM
 */
public class TileEntityFlagPole extends TileEntity implements IFlagHolder, IUpdatePlayerListBox {
    private static final int MAX_FLAGS = 4;
    private ArrayList<ItemStack> flags;
    private boolean receiveUpdates = false;
    public int side;

    public TileEntityFlagPole(){
        flags = new ArrayList<ItemStack>(MAX_FLAGS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        if (side == 1 || side == 2) {
            return axisAlignedBB.expand(0, flags.size(), 0);
        }
        return axisAlignedBB.expand(flags.size(), 0, 0);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
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
        return new S35PacketUpdateTileEntity(pos, 0, flagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt){
        if (getWorld().isRemote && pkt.getTileEntityType() == 0)
            readFromNBT(pkt.getNbtCompound());
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
    public int getOrientation() {
        return side;
    }

    public boolean canUpdate(){
        return receiveUpdates;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote && canUpdate()) {
            if (getWorld().rand.nextInt(100) == 0) {
                List entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.up()).expand(3, 0, 3));
                if (entities.isEmpty())
                    spawnUnit();
            }
        } else {
            //getWorld().tickableTileEntities.remove(this);
        }
    }

    public void spawnUnit(){
        //getWorldObj().spawnEntityInWorld(new EntityMBUnit(getWorldObj()));
    }
}
