package mods.battlegear2.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import java.util.List;


public class ItemMBArrow extends Item {

    public Icon[] icons;

    public static final String[] names = {"explosive", "ender", "flame"};

    public ItemMBArrow(int id) {
        super(id);
        this.setHasSubtypes(true);

    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        icons = new Icon[names.length];
        for(int i = 0; i < names.length; i++){
            icons[i] = par1IconRegister.registerIcon(this.getIconString()+"."+names[i]);
        }
    }

    @Override
    public net.minecraft.util.Icon getIconFromDamage(int par1) {
        return icons[par1];
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack)+"."+names[par1ItemStack.getItemDamage()];
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < names.length; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }
}
