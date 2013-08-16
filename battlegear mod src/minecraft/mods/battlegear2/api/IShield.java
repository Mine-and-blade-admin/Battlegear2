package mods.battlegear2.api;


import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface IShield {

    public int getDecayRate(ItemStack shield);

    public boolean canBlockFull(ItemStack shield, DamageSource source);

    public float reduceDamage(ItemStack shield, DamageSource source, float amaount);

    public int getDamageDecayRate(ItemStack shield, float amount);

}
