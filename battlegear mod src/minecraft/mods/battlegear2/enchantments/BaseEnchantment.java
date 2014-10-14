package mods.battlegear2.enchantments;

import com.google.common.base.Optional;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;

public class BaseEnchantment extends Enchantment {

	private int max;
	private int enchantCoeff;
	private int range;
	public static Optional<Enchantment> bashWeight, bashPower, bashDamage, shieldUsage, shieldRecover, bowLoot, bowCharge;
    public static EnchantmentHelper helper = new EnchantmentHelper();
	
	public BaseEnchantment(int id, int weight, int limit, int progress,	int range) {
		super(id, weight, EnumEnchantmentType.all);
		if (limit > 1)
			this.max = limit;
		else
			this.max = 1;
		if (progress > 1)
			this.enchantCoeff = progress;
		else
			this.enchantCoeff = 1;
		if (range > 0)
			this.range = range;
		else
			this.range = 0;
	}
	
	public BaseEnchantment(int id, int weight, EnumEnchantmentType type, int coeff, int rng){
		super(id, weight, type);
		this.max = 1;
		if (coeff > 1)
			this.enchantCoeff = coeff;
		else
			this.enchantCoeff = 1;
		if (range > 0)
			this.range = rng;
		else
			this.range = 0;
	}

	@Override
	public boolean canApply(ItemStack stack) {
        if (stack.getItem() instanceof IEnchantable) {
            return ((IEnchantable) stack.getItem()).isEnchantable(this, stack);
        }
        if(type == EnumEnchantmentType.bow && BattlegearUtils.isBow(stack.getItem())){
            return true;
        }
        return type != EnumEnchantmentType.all && super.canApply(stack);
    }

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return canApply(stack);// Redundancy for MCPC+ fix
	}

	@Override
	public int getMaxLevel() {
		return max;
	}

	@Override
	public boolean canApplyTogether(Enchantment par1Enchantment) {
		return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != this.effectId;
	}

	@Override
	public int getMinEnchantability(int par1) {
		return 1 + par1 * enchantCoeff;
	}

	@Override
	public int getMaxEnchantability(int par1) {
		return this.getMinEnchantability(par1) + range;
	}

    public static void initBase(Property...props){
        bashWeight = EnchantmentHelper.build(props[0], "bash.weightless", BaseEnchantment.class, 5, 3, 15, 30);
        bashPower = EnchantmentHelper.build(props[1], "bash.power", BaseEnchantment.class, 10, 5, 10, 40);
        bashDamage = EnchantmentHelper.build(props[2], "bash.damage", BaseEnchantment.class, 1, 3, 15, 50);
        shieldUsage = EnchantmentHelper.build(props[3], "shield.usage", BaseEnchantment.class, 2, 5, 5, 30);
        shieldRecover = EnchantmentHelper.build(props[4], "shield.recover", BaseEnchantment.class, 3, 4, 20, 20);
        bowLoot = EnchantmentHelper.build(props[5], "bow.loot", BaseEnchantment.class, 2, EnumEnchantmentType.bow, 10, 50);
        bowCharge = EnchantmentHelper.build(props[6], "bow.charge", BaseEnchantment.class, 1, EnumEnchantmentType.bow, 20, 20);
    }

    @Override
    public Enchantment setName(String name){
        super.setName(name);
        if(helper.addEnchantment(this))
            addToBookList(this);
        return this;
    }
}
