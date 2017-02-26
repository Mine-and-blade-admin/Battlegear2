package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.IFlagHolder;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
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
        super(Material.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(VARIANT, Variants.OAK));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(@Nonnull Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
        for (int i = 0; i < Variants.values().length; i++) {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
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

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, Variants.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, VARIANT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state,@Nonnull World world,@Nonnull BlockPos pos) {
        return getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state,@Nonnull IBlockAccess world,@Nonnull BlockPos pos) {
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
        return super.getCollisionBoundingBox(state, world, pos);
    }

    public float getTextDim(int metadata, int section){
        if(metadata % 7 == 4){
            return ironTexDims[section];
        }else{
            return woodTexDims[section];
        }
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world,@Nonnull IBlockState i) {
        return new TileEntityFlagPole();
    }

    @Override
    public void breakBlock(@Nonnull World par1World,@Nonnull BlockPos pos,@Nonnull IBlockState par5) {
        if(!par1World.isRemote){
            TileEntity te = par1World.getTileEntity(pos);
            if(te instanceof IFlagHolder){
                List<ItemStack> flags = ((IFlagHolder)te).getFlags();

                for(ItemStack f : flags){
                    par1World.spawnEntity(new EntityItem(par1World, pos.getX(), pos.getY(), pos.getZ(), f));
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

        @Nonnull
        @Override
        public String getName() {
            return this.name;
        }
    }
}
