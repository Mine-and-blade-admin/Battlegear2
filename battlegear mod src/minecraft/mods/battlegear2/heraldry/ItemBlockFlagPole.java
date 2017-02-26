package mods.battlegear2.heraldry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * User: nerd-boy
 * Date: 6/08/13
 * Time: 12:40 PM
 * TODO: Add discription
 */
public class ItemBlockFlagPole extends ItemMultiTexture {

    public ItemBlockFlagPole(Block block) {
        super(block, block, new String[] {"oak", "spruce", "birch", "jungle", "iron", "acacia", "big_oak"});
        this.setUnlocalizedName("battlegear2:flagpole");
    }

    @Override
    public int getMetadata(int damage) {
        return damage % 7;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState metadata) {
        boolean valid = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, metadata);
        if(valid){
            ((TileEntityFlagPole) world.getTileEntity(pos)).side = (side.getIndex() / 2);
        }
        return valid;
    }
}
