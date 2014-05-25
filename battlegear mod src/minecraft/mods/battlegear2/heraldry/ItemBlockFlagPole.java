package mods.battlegear2.heraldry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
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
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean valid = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, field_150941_b.damageDropped(metadata));
        if(valid){
            ((TileEntityFlagPole)world.getTileEntity(x, y, z)).side = (side / 2);
        }
        return valid;
    }
}
