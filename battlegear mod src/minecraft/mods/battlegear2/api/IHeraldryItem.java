package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public interface IHeraldryItem {
	
	public enum HeraldryRenderPassess{
		PrimaryColourBase,
		SecondaryColourPatter,
		Sigil,
		SecondaryColourTrim
	}
	
	public Icon getBaseIcon();
	public Icon getTrimIcon();
	public Icon getPostRenderIcon();
	
	public boolean hasHeraldry(ItemStack stack);
	public int getHeraldryCode(ItemStack stack);
	
	public void setHeraldryCode(ItemStack stack, int code);
	
	public boolean shouldDoPass(HeraldryRenderPassess pass);

}
