package mods.battlegear2.inventory;

import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTabMB_B_2 extends CreativeTabs{

	public CreativeTabMB_B_2(String label) {
		super(label);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@Nonnull
    public String getTranslatedTabLabel()
    {
        return "tab.battle.title";
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	@Nonnull
	public ItemStack getTabIconItem() {
		return new ItemStack(BattlegearConfig.findNonNullItemIcon());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> list){
        super.displayAllRelevantItems(list);
        list.addAll(BaseEnchantment.helper.getEnchantmentBooks());
    }
}
