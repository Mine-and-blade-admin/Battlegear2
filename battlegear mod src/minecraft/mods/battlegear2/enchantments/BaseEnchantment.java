package mods.battlegear2.enchantments;

import java.util.ArrayList;
import java.util.List;

import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class BaseEnchantment extends Enchantment {

	private static List<Enchantment> enchants = new ArrayList<Enchantment>();
	private int max;
	private int enchantCoeff;
	private int range;
	public static final Enchantment bashWeight = new BaseEnchantment(BattlegearConfig.enchantsId[0], 5, 3, 15, 30).setName("bash.weightless");
	public static final Enchantment bashPower = new BaseEnchantment(BattlegearConfig.enchantsId[1], 10, 5, 10, 40).setName("bash.power");
	public static final Enchantment bashDamage = new BaseEnchantment(BattlegearConfig.enchantsId[2], 1, 3, 15, 50).setName("bash.damage");
	public static final Enchantment shieldUsage = new BaseEnchantment(BattlegearConfig.enchantsId[3], 2, 5, 5, 30).setName("shield.usage");
	public static final Enchantment shieldRecover = new BaseEnchantment(BattlegearConfig.enchantsId[4], 3, 4, 20, 20).setName("shield.recover");
	public static final Enchantment bowLoot = new BaseEnchantment(BattlegearConfig.enchantsId[5], 2, EnumEnchantmentType.bow, 10, 50).setName("bow.loot");
	public static final Enchantment bowCharge = new BaseEnchantment(BattlegearConfig.enchantsId[6], 1, EnumEnchantmentType.bow, 20, 20).setName("bow.charge");
	
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
		enchants.add(this);
		addToBookList(this);
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
		enchants.add(this);
		addToBookList(this);
	}

	@Override
	public boolean canApply(ItemStack stack) {
		if(type == EnumEnchantmentType.all){
			if (stack.getItem() instanceof IEnchantable) {
				return ((IEnchantable) stack.getItem()).isEnchantable(this, stack);
			}
		}else{
			return super.canApply(stack);
		}
		return false;
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
		return super.canApplyTogether(par1Enchantment)
				&& par1Enchantment.effectId != this.effectId;
	}

	@Override
	public int getMinEnchantability(int par1) {
		return 1 + par1 * enchantCoeff;
	}

	@Override
	public int getMaxEnchantability(int par1) {
		return this.getMinEnchantability(par1) + range;
	}

	public static List<Enchantment> getEnchants() {
		return enchants;
	}
}
