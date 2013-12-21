package mods.battlegear2.items;

import java.util.List;

import mods.battlegear2.items.arrows.*;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemMBArrow extends Item {

    public Icon[] icons;

    public static final String[] names = {"explosive", "ender", "flame", "piercing", "poison"};
    public static final Class<? extends EntityArrow> arrows[] = new Class[]{EntityExplossiveArrow.class, EntityEnderArrow.class, EntityFlameArrow.class, EntityPiercingArrow.class, EntityPoisonArrow.class};
    public static final Item[] component = {Item.gunpowder, Item.enderPearl, Item.flint, Item.diamond, Item.netherStar};
    
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

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < names.length; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }
}
