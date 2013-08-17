package mods.battlegear2.api;


import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface IShield {

    public float getDecayRate(ItemStack shield);

    public boolean canBlock(ItemStack shield, DamageSource source);

    public float getDamageDecayRate(ItemStack shield, float amount);

}
