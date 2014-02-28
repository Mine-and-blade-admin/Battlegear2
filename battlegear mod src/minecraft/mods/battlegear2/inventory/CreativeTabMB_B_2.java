package mods.battlegear2.inventory;

import java.util.List;

import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabMB_B_2 extends CreativeTabs{

	public CreativeTabMB_B_2(String label) {
		super(label);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return StatCollector.translateToLocal("name.title");
    }
	
	@Override
	public Item getTabIconItem() {
		return BattlegearConfig.heradricItem;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List list)
    {
        super.displayAllReleventItems(list);
        this.addEnchantmentBooksToList(list, BaseEnchantment.getEnchants());
    }
	
	private void addEnchantmentBooksToList(List list, List<Enchantment> enchants) {
		for(Enchantment enchantment:enchants){
			list.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
		}
	}
}
