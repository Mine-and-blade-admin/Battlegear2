package mods.battlegear2.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Wrapper for {@link net.minecraft.item.ItemStack}
 * with valid #hashCode() and #equals(Object) overrides.
 */
public class StackHolder{
    public final ItemStack stack;
    private int hash;
    public StackHolder(ItemStack stack){
        assert stack!=null && stack.getItem()!=null :"Stack or item can't be null";
        this.stack = stack;
    }

    @Override
    public int hashCode() {
        if(hash==0) {
            int init = 17, mult = 37;
            hash = new HashCodeBuilder(init, mult).append(Item.getIdFromItem(stack.getItem())).append(stack.getItemDamage()).append(stack.getTagCompound()).toHashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj!=null && obj instanceof StackHolder && ItemStack.areItemStacksEqual(stack, ((StackHolder) obj).stack);
    }
}
