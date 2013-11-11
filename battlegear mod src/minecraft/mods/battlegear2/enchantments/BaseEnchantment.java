package mods.battlegear2.enchantments;

import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class BaseEnchantment extends Enchantment{

	private int max;
	private int enchantCoeff;
	private int range;
	public static Enchantment shieldBash = new BaseEnchantment(BattlegearConfig.shieldBashEnchantId, 1, 3, 20, 30);

	public BaseEnchantment(int id, int weight, int limit, int progress, int range) {
		super(id, weight, EnumEnchantmentType.all);
		if(limit>1)
			this.max = limit;
		else
			this.max = 1;
		if(progress>1)
			this.enchantCoeff = progress;
		else
			this.enchantCoeff = 1;
		if(range>0)
			this.range = range;
		else
			this.range = 0;
	}

	@Override
	public boolean canApply(ItemStack stack)
    {
		if(stack.getItem() instanceof IEnchantable){
			return ((IEnchantable) stack.getItem()).isEnchantable(this, stack);
		}
        return false;
    }
	
	@Override
	public int getMaxLevel()
    {
        return max;
    }
	
	@Override
	public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return super.canApplyTogether(par1Enchantment) && par1Enchantment.effectId != this.effectId;
    }
	
	@Override
	public int getMinEnchantability(int par1)
    {
        return 1 + par1 * enchantCoeff;
    }

    @Override
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + range;
    }
}
