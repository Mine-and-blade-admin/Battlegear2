package mods.battlegear2.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

import java.util.List;

public class CreativeTabMB_B_2 extends CreativeTabs{

	public CreativeTabMB_B_2(String label) {
		super(label);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return StatCollector.translateToLocal("tab.battle.title");
    }
	
	@Override
    @SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return BattlegearConfig.findNonNullItemIcon();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List list){
        super.displayAllReleventItems(list);
        list.addAll(BaseEnchantment.helper.getEnchantmentBooks());
    }
}
