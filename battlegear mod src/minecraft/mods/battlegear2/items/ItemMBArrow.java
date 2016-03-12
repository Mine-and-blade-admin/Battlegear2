package mods.battlegear2.items;

import mods.battlegear2.api.quiver.DispenseArrow;
import mods.battlegear2.items.arrows.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemMBArrow extends Item {
    public static final String[] names = {"explosive", "ender", "flame", "piercing", "poison", "mystery", "leech"};
    public static final Class<? extends AbstractMBArrow> arrows[] = new Class[]{EntityExplossiveArrow.class, EntityEnderArrow.class, EntityFlameArrow.class, EntityPiercingArrow.class, EntityPoisonArrow.class, EntityLoveArrow.class, EntityLeechArrow.class};
    public static final Item[] component = {Items.gunpowder, Items.ender_pearl, Items.flint, Items.diamond, Items.nether_star, Items.cookie, Items.ghast_tear};
    public static final DispenseArrow dispensable = new DispenseArrow() {
        @Override
        protected EntityArrow getArrowEntity(World world, ItemStack itemStack) {
            if(itemStack.getItemDamage()<arrows.length){
                try{
                    return arrows[itemStack.getMetadata()].getConstructor(World.class).newInstance(world);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    };

    public ItemMBArrow() {
        super();
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + names[par1ItemStack.getMetadata()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List){
        for (int j = 0; j < names.length; ++j){
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        int dmg = par1ItemStack.getMetadata();
        if(dmg<names.length){
            par3List.add(StatCollector.translateToLocal("lore.base.arrow."+names[dmg]));
            if(par4){
                par3List.add(StatCollector.translateToLocal("lore.advanced.arrow."+names[dmg]));
            }
        }
    }
}
