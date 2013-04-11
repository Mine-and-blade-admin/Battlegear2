package mods.battlegear2.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.StringTranslate;

public class CreativeTabMB_B_2 extends CreativeTabs{

	public CreativeTabMB_B_2( String label) {
		super(label);
	}
	@SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return StringTranslate.getInstance().translateKey(this.getTabLabel());
    }
}
