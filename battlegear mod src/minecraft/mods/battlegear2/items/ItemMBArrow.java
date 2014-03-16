package mods.battlegear2.items;

import java.util.List;

import mods.battlegear2.items.arrows.*;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemMBArrow extends Item {

    public Icon[] icons;

    public static final String[] names = {"explosive", "ender", "flame", "piercing", "poison", "mystery", "leech"};
    public static final Class<? extends AbstractMBArrow> arrows[] = new Class[]{EntityExplossiveArrow.class, EntityEnderArrow.class, EntityFlameArrow.class, EntityPiercingArrow.class, EntityPoisonArrow.class, EntityLoveArrow.class, EntityLeechArrow.class};
    public static final Item[] component = {Item.gunpowder, Item.enderPearl, Item.flint, Item.diamond, Item.netherStar, Item.cookie, Item.ghastTear};
    
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
    public Icon getIconFromDamage(int par1) {
        return icons[par1];
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack)+"."+names[par1ItemStack.getItemDamage()];
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List){
        for (int j = 0; j < names.length; ++j){
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        int dmg = par1ItemStack.getItemDamage();
        if(dmg<names.length){
            par3List.add(StatCollector.translateToLocal("lore.base.arrow."+names[dmg]));
            if(par4){
                par3List.add(StatCollector.translateToLocal("lore.advanced.arrow."+names[dmg]));
            }
        }
    }
}
