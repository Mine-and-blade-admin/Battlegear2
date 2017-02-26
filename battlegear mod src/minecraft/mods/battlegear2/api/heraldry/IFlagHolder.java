package mods.battlegear2.api.heraldry;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Defines a {@link #TileEntity} holding flags {@link #ItemStack}
 */
public interface IFlagHolder {

    /**
     * Remove all contained flags
     */
    void clearFlags();

    /**
     * Add given flag to the TileEntity
     * @param flag The flag to add
     * return true if flag could be added
     */
    boolean addFlag(ItemStack flag);

    /**
     *
     * @return All flags currently contained, empty list if none
     */
    List<ItemStack> getFlags();

    /**
     * Called by the default {@link FlagPoleTileRenderer}
     * @param metadata
     * @param section
     * @return the value to interpolate on the flagpole icon, to render the flagpole
     */
    float getTextureDimensions(int metadata, int section);

    /**
     * Called by the default {@link FlagPoleTileRenderer}
     * @return the rendered orientation value for the flagpole and thus the flags
     */
    int getOrientation();
}
