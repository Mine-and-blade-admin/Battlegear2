package mods.battlegear2.heraldry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTextureTile;

/**
 * User: nerd-boy
 * Date: 6/08/13
 * Time: 12:40 PM
 * TODO: Add discription
 */
public class ItemBlockFlagPole extends ItemMultiTextureTile{

    public ItemBlockFlagPole(int i, Block block) {
        super(i, block, new String[] {"oak", "spruce", "birch", "jungle", "iron"});
        this.setUnlocalizedName("battlegear2:flagpole");
    }
}
