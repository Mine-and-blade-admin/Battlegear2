package mods.battlegear2.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public interface IHeraldryItem {
	
	public Icon getBaseIcon();
	public Icon getPostRenderIcon();
	
	public boolean hasHeraldry(ItemStack stack);
	public int getHeraldryCode(ItemStack stack);

}
