package mods.battlegear2.heraldry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;

/**
 * User: nerd-boy
 * Date: 6/08/13
 * Time: 12:40 PM
 * TODO: Add discription
 */
public class ItemBlockFlagPole extends ItemMultiTexture {

    public ItemBlockFlagPole(Block block) {
        super(block, block, new String[] {"oak", "spruce", "birch", "jungle", "iron"});
        this.setUnlocalizedName("battlegear2:flagpole");
    }
}
