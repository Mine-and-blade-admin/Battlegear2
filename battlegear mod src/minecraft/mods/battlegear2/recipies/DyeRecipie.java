package mods.battlegear2.recipies;

import mods.battlegear2.api.IDyable;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public final class DyeRecipie implements IRecipe
{
    @Override
    public boolean matches(@Nonnull InventoryCrafting par1InventoryCrafting,@Nonnull World par2World)
    {
        ItemStack dyableStack = ItemStack.EMPTY;
        ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();
        boolean waterFound = false;

        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i)
        {
            ItemStack stack = par1InventoryCrafting.getStackInSlot(i);

            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IDyable)
                {
                    if (!dyableStack.isEmpty())
                    {
                        return false;
                    }
                    dyableStack = stack;
                }
                else
                {
                    if(stack.getItem() == Items.WATER_BUCKET &&!waterFound){
                        waterFound = true;
                    }else if (stack.getItem() != Items.DYE)
                    {
                        return false;
                    }else{
                        arraylist.add(stack);
                    }
                }
            }
        }

        return !dyableStack.isEmpty() && (!arraylist.isEmpty() ^ waterFound);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting par1InventoryCrafting)
    {
        ItemStack dyableStack = ItemStack.EMPTY;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        IDyable dyeable = null;
        int k;
        int l;
        float f;
        float f1;
        int i1;
        boolean removeColour = false;

        for (k = 0; k < par1InventoryCrafting.getSizeInventory(); ++k)
        {
            ItemStack stack = par1InventoryCrafting.getStackInSlot(k);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IDyable)
                {
                    dyeable = (IDyable)stack.getItem();

                    if (!dyableStack.isEmpty())
                    {
                        return ItemStack.EMPTY;
                    }

                    dyableStack = stack.copy();
                    dyableStack.setCount(1);

                    if (dyeable.hasColor(stack))
                    {
                        l = dyeable.getColor(dyableStack);
                        f = (float)(l >> 16 & 255) / 255.0F;
                        f1 = (float)(l >> 8 & 255) / 255.0F;
                        float f2 = (float)(l & 255) / 255.0F;
                        i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int)((float)aint[0] + f * 255.0F);
                        aint[1] = (int)((float)aint[1] + f1 * 255.0F);
                        aint[2] = (int)((float)aint[2] + f2 * 255.0F);
                        ++j;
                    }
                }
                else
                {
                    if(stack.getItem() == Items.WATER_BUCKET){
                        removeColour = true;
                    }else if (stack.getItem() != Items.DYE)
                    {
                        return ItemStack.EMPTY;
                    }

                    float[] afloat = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stack.getItemDamage()));
                    int j1 = (int)(afloat[0] * 255.0F);
                    int k1 = (int)(afloat[1] * 255.0F);
                    i1 = (int)(afloat[2] * 255.0F);
                    i += Math.max(j1, Math.max(k1, i1));
                    aint[0] += j1;
                    aint[1] += k1;
                    aint[2] += i1;
                    ++j;
                }
            }
        }

        if (dyeable == null)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            if(removeColour){
                dyeable.removeColor(dyableStack);
            }else{
                k = aint[0] / j;
                int l1 = aint[1] / j;
                l = aint[2] / j;
                f = (float)i / (float)j;
                f1 = (float)Math.max(k, Math.max(l1, l));
                k = (int)((float)k * f / f1);
                l1 = (int)((float)l1 * f / f1);
                l = (int)((float)l * f / f1);
                i1 = (k << 8) + l1;
                i1 = (i1 << 8) + l;
                dyeable.setColor(dyableStack, i1);
            }
            return dyableStack;
        }
    }

    @Override
    public int getRecipeSize()
    {
        return 10;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
