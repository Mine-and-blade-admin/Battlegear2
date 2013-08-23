package mods.battlegear2.recipies;


import mods.battlegear2.api.IDyable;
import mods.battlegear2.items.ItemQuiver2;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuiverDyeRecipie implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting par1InventoryCrafting, World par2World)
    {
        ItemStack dyableStack = null;
        ArrayList arraylist = new ArrayList();
        boolean waterFound = false;

        for (int i = 0; i < par1InventoryCrafting.getSizeInventory(); ++i)
        {
            ItemStack stack = par1InventoryCrafting.getStackInSlot(i);

            if (stack != null)
            {
                if (stack.getItem() instanceof IDyable)
                {

                    if (dyableStack != null)
                    {
                        return false;
                    }

                    dyableStack = stack;
                }
                else
                {
                    if(stack.itemID == Item.bucketWater.itemID &&!waterFound){
                        waterFound = true;
                    }else if (stack.itemID != Item.dyePowder.itemID)
                    {
                        return false;
                    }else{
                        arraylist.add(stack);
                    }
                }
            }
        }

        return dyableStack != null && (!arraylist.isEmpty() ^ waterFound);
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting)
    {
        ItemStack dyableStack = null;
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

            if (stack != null)
            {
                if (stack.getItem() instanceof IDyable)
                {
                    dyeable = (IDyable)stack.getItem();

                    if (dyableStack != null)
                    {
                        return null;
                    }

                    dyableStack = stack.copy();
                    dyableStack.stackSize = 1;

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
                    if(stack.getItem().itemID == Item.bucketWater.itemID){
                        removeColour = true;
                    }else if (stack.itemID != Item.dyePowder.itemID)
                    {
                        return null;
                    }

                    float[] afloat = EntitySheep.fleeceColorTable[BlockColored.getBlockFromDye(stack.getItemDamage())];
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
            return null;
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

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 10;
    }

    public ItemStack getRecipeOutput()
    {
        return null;
    }
}
