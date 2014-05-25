package mods.battlegear2.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.items.arrows.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemMBArrow extends Item {

    public IIcon[] icons;

    public static final String[] names = {"explosive", "ender", "flame", "piercing", "poison", "mystery", "leech"};
    public static final Class<? extends AbstractMBArrow> arrows[] = new Class[]{EntityExplossiveArrow.class, EntityEnderArrow.class, EntityFlameArrow.class, EntityPiercingArrow.class, EntityPoisonArrow.class, EntityLoveArrow.class, EntityLeechArrow.class};
    public static final Item[] component = {Items.gunpowder, Items.ender_pearl, Items.flint, Items.diamond, Items.nether_star, Items.cookie, Items.ghast_tear};
    
    public ItemMBArrow() {
        super();
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        icons = new IIcon[names.length];
        for(int i = 0; i < names.length; i++){
            icons[i] = par1IconRegister.registerIcon(this.getIconString()+"."+names[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return icons[par1];
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack)+"."+names[par1ItemStack.getItemDamage()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        for (int j = 0; j < names.length; ++j){
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
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
