package mods.battlegear2.heraldry;

import mods.battlegear2.api.heraldry.IFlagHolder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 11:53 AM
 *
 * Block class for a flag pole
 */
public class BlockFlagPole extends Block {

    private static final float[] woodTexDims = new float[5];
    private static final float[] ironTexDims = new float[5];
    static{
        woodTexDims[0] = 0F;
        woodTexDims[1] = 4F;
        woodTexDims[2] = 8F;
        woodTexDims[3] = 12F;
        woodTexDims[4] = 16F;

        ironTexDims[0] = 1F;
        ironTexDims[1] = 4.5F;
        ironTexDims[2] = 8F;
        ironTexDims[3] = 11.5F;
        ironTexDims[4] = 15;
    }

    public BlockFlagPole() {
        super(Material.wood);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 7; i++){
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public int damageDropped(int par1){
        return par1 % 7;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int par2, int par3, int par4) {
        TileEntity te = world.getTileEntity(par2, par3, par4);
        if(te instanceof IFlagHolder) {
            switch (((IFlagHolder) te).getOrientation(world.getBlockMetadata(par2, par3, par4))) {
                case 0:
                    return AxisAlignedBB.getAABBPool().getAABB((double) par2 + 6F / 16F, (double) par3 + 0, (double) par4 + 6F / 16F, (double) par2 + 10F / 16F, (double) par3 + 1, (double) par4 + 10F / 16F);
                case 1:
                    return AxisAlignedBB.getAABBPool().getAABB((double) par2 + 6F / 16F, (double) par3 + 13F / 16F, (double) par4 + 0, (double) par2 + 10F / 16F, (double) par3 + 1, (double) par4 + 1);
                case 2:
                    return AxisAlignedBB.getAABBPool().getAABB((double) par2 + 0, (double) par3 + 13F / 16F, (double) par4 + 6F / 16F, (double) par2 + 1, (double) par3 + 1, (double) par4 + 10F / 16F);
            }
        }
        return super.getSelectedBoundingBoxFromPool(world, par2, par3, par4);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    public float getTextDim(int metadata, int section){
        if(metadata % 7 == 4){
            return ironTexDims[section];
        }else{
            return woodTexDims[section];
        }
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int i){
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int i) {
        return new TileEntityFlagPole();
    }

    @Override
    public IIcon getIcon(int par1, int meta) {
        if(meta == 4)
            return Blocks.iron_block.getIcon(par1, 0);
        else if(meta < 4)
            return Blocks.log.getIcon(par1, meta);
        else
            return Blocks.log2.getIcon(par1, meta - 5);
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
        if(!par1World.isRemote){
            TileEntity te = par1World.getTileEntity(par2, par3, par4);
            if(te instanceof IFlagHolder){
                List<ItemStack> flags = ((IFlagHolder)te).getFlags();

                for(ItemStack f : flags){
                    par1World.spawnEntityInWorld(new EntityItem(par1World, par2, par3, par4, f));
                }
                ((IFlagHolder) te).clearFlags();
            }
        }
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
}
