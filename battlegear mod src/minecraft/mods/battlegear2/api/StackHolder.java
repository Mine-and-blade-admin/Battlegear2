package mods.battlegear2.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Wrapper for {@link ItemStack} with valid {@link Object#hashCode()} and {@link Object#equals(Object)} overrides.
 * Hash is lazily initiated and equals compare using the helper {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)}
 * Note: Doesn't allow null values
 */
public class StackHolder{
    public final ItemStack stack;
    private int hash;
    public StackHolder(ItemStack stack){
        if(stack==null || stack.getItem()==null){
            throw new IllegalArgumentException("StackHolder doesn't accept null");
        }
        this.stack = stack;
    }

    @Override
    public int hashCode() {
        if(hash==0) {
            int init = 17, mult = 37;
            hash = new HashCodeBuilder(init, mult).append(Item.getIdFromItem(stack.getItem())).append(stack.getItemDamage()).append(stack.getTagCompound()).toHashCode();
            assert hash!=0:"Hashcode builder failed to follow its contract";
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
