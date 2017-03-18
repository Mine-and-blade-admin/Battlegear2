package mods.battlegear2.items;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.quiver.DispenseArrow;
import mods.battlegear2.items.arrows.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMBArrow extends ItemArrow {
    public static final String[] names = {"explosive", "ender", "flame", "piercing", "poison", "mystery", "leech"};
    public static final Class<? extends AbstractMBArrow> arrows[] = new Class[]{EntityExplossiveArrow.class, EntityEnderArrow.class, EntityFlameArrow.class, EntityPiercingArrow.class, EntityPoisonArrow.class, EntityLoveArrow.class, EntityLeechArrow.class};
    public static final Item[] component = {Items.GUNPOWDER, Items.ENDER_PEARL, Items.FLINT, Items.DIAMOND, Items.NETHER_STAR, Items.COOKIE, Items.GHAST_TEAR};
    public static final DispenseArrow dispensable = new DispenseArrow() {
        @Override
        protected EntityArrow getArrowEntity(World world, ItemStack itemStack) {
            try{
                return arrows[itemStack.getMetadata()].getConstructor(World.class).newInstance(world);
            }catch (Throwable e){
                e.printStackTrace();
            }
            return null;
        }
    };
    static{
        for(int i = 0; i < names.length; i++) {
            EntityRegistry.registerModEntity(new ResourceLocation(Battlegear.MODID, "arrow_" + names[i]), arrows[i], names[i], i, Battlegear.INSTANCE, 64, 20, true);
        }
    }

    public ItemMBArrow() {
        super();
        this.setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + names[par1ItemStack.getMetadata()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List){
        for (int j = 0; j < names.length; ++j){
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        int dmg = par1ItemStack.getMetadata();
        if(dmg < names.length){
            par3List.add(I18n.format("lore.base.arrow."+names[dmg]));
            if(par4){
                par3List.add(I18n.format("lore.advanced.arrow."+names[dmg]));
            }
        }
    }

    @Override
    @Nonnull
    public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter) {
        try {
            return arrows[stack.getMetadata()].getConstructor(World.class, EntityLivingBase.class).newInstance(worldIn, shooter);
        }catch (Throwable e){
            return super.createArrow(worldIn, stack, shooter);
        }
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
        return EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.INFINITY, bow) > 0;
    }
}
