package mods.battlegear2.items;

import mods.battlegear2.api.IShield;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemShield extends Item implements IShield{

    public ItemShield(int id) {
        super(id);
        this.setCreativeTab(BattlegearConfig.customTab);
        this.setUnlocalizedName("battlegear2:shield-test");
        this.func_111206_d("battlegear2:shield-test");
    }

    @Override
    public int getDecayRate(ItemStack shield) {
        return 10;
    }

    @Override
    public boolean canBlockFull(ItemStack shield, DamageSource source) {
        return !source.isUnblockable();
    }

    @Override
    public float reduceDamage(ItemStack shield, DamageSource source, float amount) {
        if(source.isUnblockable())
            return amount;
        else
            return 0;
    }

    @Override
    public int getDamageDecayRate(ItemStack shield, float amount) {
        return (int) (amount * 50);
    }
}
