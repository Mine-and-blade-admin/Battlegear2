package mods.battlegear2.enchantments;

import com.google.common.base.Optional;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nonnull;

public class BaseEnchantment extends Enchantment {

	private int max;
	private int enchantCoeff;
	private int range;
	public static Optional<Enchantment> bashWeight, bashPower, bashDamage, shieldUsage, shieldRecover, bowLoot, bowCharge;
    public static EnchantmentHelper helper = new EnchantmentHelper();
	private static final EntityEquipmentSlot[] HANDS =  {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND};
	
	public BaseEnchantment(Rarity weight, int limit, int progress, int range) {
		super(weight, EnumEnchantmentType.ALL, HANDS);
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
	
	public BaseEnchantment(Rarity weight, int coeff, int rng){
		super(weight, EnumEnchantmentType.BOW, HANDS);
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
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		if (stack.getItem() instanceof IEnchantable) {
            return ((IEnchantable) stack.getItem()).isEnchantable(this, stack);
        }
        if(type == EnumEnchantmentType.BOW && BattlegearUtils.isBow(stack.getItem())){
            return true;
        }
		return type != EnumEnchantmentType.ALL && super.canApplyAtEnchantingTable(stack);
	}

	@Override
	public int getMaxLevel() {
		return max;
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
		Rarity ALMOST_RARE = EnumHelper.addEnum(Rarity.class, "NEAR_RARE", new Class<?>[]{int.class}, 3);
		bashWeight = EnchantmentHelper.build(props[0], "bash.weightless", BaseEnchantment.class, Battlegear.MODID+":bash_weightless", Rarity.UNCOMMON, 3, 15, 30);
        bashPower = EnchantmentHelper.build(props[1], "bash.power", BaseEnchantment.class, Battlegear.MODID+":bash_power", Rarity.COMMON, 5, 10, 40);
        bashDamage = EnchantmentHelper.build(props[2], "bash.damage", BaseEnchantment.class, Battlegear.MODID+":bash_damage", Rarity.VERY_RARE, 3, 15, 50);
        shieldUsage = EnchantmentHelper.build(props[3], "shield.usage", BaseEnchantment.class, Battlegear.MODID+":shield_efficiency", Rarity.RARE, 5, 5, 30);
        shieldRecover = EnchantmentHelper.build(props[4], "shield.recover", BaseEnchantment.class, Battlegear.MODID+":shield_recovery", ALMOST_RARE, 4, 20, 20);
        bowLoot = EnchantmentHelper.build(props[5], "bow.loot", BaseEnchantment.class, Battlegear.MODID+":bow_luck", Rarity.RARE, 10, 50);
        bowCharge = EnchantmentHelper.build(props[6], "bow.charge", BaseEnchantment.class, Battlegear.MODID+":bow_drawnback", Rarity.VERY_RARE, 20, 20);
    }

    @Override
	@Nonnull
    public Enchantment setName(@Nonnull String name){
        super.setName(name);
        helper.addEnchantment(this);
        return this;
    }
}
