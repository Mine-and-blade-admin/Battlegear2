package mods.battlegear2.enchantments;

import java.util.ArrayList;
import java.util.List;

import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class BaseEnchantment extends Enchantment {

	private static List<Enchantment> enchants = new ArrayList<Enchantment>();
	private int max;
	private int enchantCoeff;
	private int range;
	public static Enchantment bashWeight, bashPower, bashDamage, shieldUsage, shieldRecover, bowLoot, bowCharge;
	
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
		if(type != EnumEnchantmentType.all){
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

    public static void initBase(){
        if(BattlegearConfig.enchantsId[0]>=0 && BattlegearConfig.enchantsId[0]<Enchantment.enchantmentsList.length)
            bashWeight = new BaseEnchantment(BattlegearConfig.enchantsId[0], 5, 3, 15, 30).setName("bash.weightless");
        if(BattlegearConfig.enchantsId[1]>=0 && BattlegearConfig.enchantsId[1]<Enchantment.enchantmentsList.length)
            bashPower = new BaseEnchantment(BattlegearConfig.enchantsId[1], 10, 5, 10, 40).setName("bash.power");
        if(BattlegearConfig.enchantsId[2]>=0 && BattlegearConfig.enchantsId[2]<Enchantment.enchantmentsList.length)
            bashDamage = new BaseEnchantment(BattlegearConfig.enchantsId[2], 1, 3, 15, 50).setName("bash.damage");
        if(BattlegearConfig.enchantsId[3]>=0 && BattlegearConfig.enchantsId[3]<Enchantment.enchantmentsList.length)
            shieldUsage = new BaseEnchantment(BattlegearConfig.enchantsId[3], 2, 5, 5, 30).setName("shield.usage");
        if(BattlegearConfig.enchantsId[4]>=0 && BattlegearConfig.enchantsId[4]<Enchantment.enchantmentsList.length)
            shieldRecover = new BaseEnchantment(BattlegearConfig.enchantsId[4], 3, 4, 20, 20).setName("shield.recover");
        if(BattlegearConfig.enchantsId[5]>=0 && BattlegearConfig.enchantsId[5]<Enchantment.enchantmentsList.length)
            bowLoot = new BaseEnchantment(BattlegearConfig.enchantsId[5], 2, EnumEnchantmentType.bow, 10, 50).setName("bow.loot");
        if(BattlegearConfig.enchantsId[6]>=0 && BattlegearConfig.enchantsId[6]<Enchantment.enchantmentsList.length)
            bowCharge = new BaseEnchantment(BattlegearConfig.enchantsId[6], 1, EnumEnchantmentType.bow, 20, 20).setName("bow.charge");
    }

    @Override
    public Enchantment setName(String name){
        super.setName(name);
        enchants.add(this);
        addToBookList(this);
        return this;
    }
}
