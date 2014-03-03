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
    public void clearFlags();

    /**
     * Add given flag to the TileEntity
     * @param flag The flag to add
     * return true if flag could be added
     */
    public boolean addFlag(ItemStack flag);

    /**
     *
     * @return All flags currently contained, empty list if none
     */
    public List<ItemStack> getFlags();
}
