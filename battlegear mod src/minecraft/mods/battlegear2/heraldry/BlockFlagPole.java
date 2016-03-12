package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.IFlagHolder;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 11:53 AM
 *
 * Block class for a flag pole
 */
public class BlockFlagPole extends BlockRotatedPillar {

    public static final PropertyEnum<Variants> VARIANT = PropertyEnum.create("variant", Variants.class);
    private static final float[] woodTexDims = {0F, 4F, 8F, 12F, 16F};
    private static final float[] ironTexDims = {1F, 4.5F, 8F, 11.5F, 15F};

    public BlockFlagPole() {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(VARIANT, Variants.OAK));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        for (int i = 0; i < Variants.values().length; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof IFlagHolder) {
            int side = ((IFlagHolder) te).getOrientation();
            if (side == 1) {
                return state.withProperty(AXIS, EnumFacing.Axis.Z);
            } else if (side == 2) {
                return state.withProperty(AXIS, EnumFacing.Axis.X);
            }
        }
        return state.withProperty(AXIS, EnumFacing.Axis.Y);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, Variants.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, AXIS, VARIANT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return getCollisionBoundingBox(world, pos, null);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof IFlagHolder) {
            switch (((IFlagHolder) te).getOrientation()) {
                case 0:
                    return new AxisAlignedBB(pos.getX() + 6F / 16F, pos.getY(), pos.getZ() + 6F / 16F, pos.getX() + 10F / 16F, pos.getY() + 1, pos.getZ() + 10F / 16F);
                case 1:
                    return new AxisAlignedBB(pos.getX() + 6F / 16F, pos.getY() + 13F / 16F, pos.getZ(), pos.getX() + 10F / 16F, pos.getY() + 1, pos.getZ() + 1);
                case 2:
                    return new AxisAlignedBB(pos.getX(), pos.getY() + 13F / 16F, pos.getZ() + 6F / 16F, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 10F / 16F);
            }
        }
        return super.getCollisionBoundingBox(world, pos, state);
    }

    public float getTextDim(int metadata, int section){
        if(metadata % 7 == 4){
            return ironTexDims[section];
        }else{
            return woodTexDims[section];
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState i) {
        return new TileEntityFlagPole();
    }

    @Override
    public void breakBlock(World par1World, BlockPos pos, IBlockState par5) {
        if(!par1World.isRemote){
            TileEntity te = par1World.getTileEntity(pos);
            if(te instanceof IFlagHolder){
                List<ItemStack> flags = ((IFlagHolder)te).getFlags();

                for(ItemStack f : flags){
                    par1World.spawnEntityInWorld(new EntityItem(par1World, pos.getX(), pos.getY(), pos.getZ(), f));
                }
                ((IFlagHolder) te).clearFlags();
            }
        }
        super.breakBlock(par1World, pos, par5);
    }

    public enum Variants implements IStringSerializable {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        IRON("iron"),
        ACACIA("acacia"),
        DARK_OAK("dark_oak");
        private final String name;

        Variants(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public static Variants byMetadata(int meta) {
            Variants[] vals = values();
            return vals[meta % vals.length];
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
