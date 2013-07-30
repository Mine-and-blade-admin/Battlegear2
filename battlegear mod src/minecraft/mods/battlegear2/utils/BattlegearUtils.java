package mods.battlegear2.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.IOException;

public class BattlegearUtils {

    private static String[] itemBlackListMethodNames;

    private static Class[][] itemBlackListMethodParams;

    static {

        if (World.class.getName().equals("net.minecraft.world.World")) {
            itemBlackListMethodNames = new String[]{
                    "onItemUse",
                    "onItemRightClick"
            };
        } else {
            itemBlackListMethodNames = new String[]{
                    BattlegearTranslator.getMapedMethodName("Item", "func_77648_a"),
                    BattlegearTranslator.getMapedMethodName("Item", "func_77659_a")
            };
        }

        itemBlackListMethodParams = new Class[][]{
                new Class[]{ItemStack.class, EntityPlayer.class, World.class, int.class, int.class, int.class, int.class, float.class, float.class, float.class},
                new Class[]{ItemStack.class, World.class, EntityPlayer.class}
        };
    }

    private static boolean[] weapons;
    private static boolean[] mainHandDualWeapons;
    private static boolean[] offhandDualWeapons;


    public static boolean isPlayerInBattlemode(EntityPlayer player) {
        return player.inventory instanceof InventoryPlayerBattle && ((InventoryPlayerBattle) player.inventory).isBattlemode();
    }


    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack, int offset) {
        ((InventoryPlayerBattle) (player.inventory)).setInventorySlotContents(player.inventory.currentItem + offset, stack, false);
    }

    public static void setPlayerCurrentItem(EntityPlayer player, ItemStack stack) {
        setPlayerCurrentItem(player, stack, 0);
    }

    public static boolean isWeapon(int id) {

        if (Item.itemsList[id] instanceof IBattlegearWeapon)
            return true;
        else
            return weapons[id];
    }

    public static boolean isMainHand(int id) {
        if (Item.itemsList[id] instanceof IBattlegearWeapon)
            return ((IBattlegearWeapon) Item.itemsList[id]).willAllowOffhandWeapon();
        else
            return mainHandDualWeapons[id];
    }

    public static boolean isOffHand(int id) {
        if (Item.itemsList[id] instanceof IBattlegearWeapon)
            return ((IBattlegearWeapon) Item.itemsList[id]).isOffhandHandDualWeapon();
        else
            return offhandDualWeapons[id];
    }

    public static void scanAndProcessItems() {

        weapons = new boolean[Item.itemsList.length];
        mainHandDualWeapons = new boolean[Item.itemsList.length];
        offhandDualWeapons = new boolean[Item.itemsList.length];

        for (int i = 0; i < Item.itemsList.length; i++) {
            Item item = Item.itemsList[i];
            weapons[i] = false;
            mainHandDualWeapons[i] = false;
            offhandDualWeapons[i] = false;
            if (item != null) {

                boolean valid = item.getItemStackLimit() == 1 && item.isDamageable();
                if (valid) {
                    weapons[i] = item instanceof ItemSword ||
                            item instanceof ItemBow ||
                            item instanceof ItemTool;


                    if (weapons[i]) {
                        //make sure there are no special functions for offhand/mainhand weapons
                        boolean rightClickFunction = checkForRightClickFunction(item);
                        //only weapons can be placed in offhand
                        offhandDualWeapons[i] = !(item instanceof ItemTool || item instanceof ItemBow) && !rightClickFunction;
                        mainHandDualWeapons[i] = !(item instanceof ItemBow) && !rightClickFunction;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean checkForRightClickFunction(Item item) {
        try {
            if (item.getItemUseAction(null) == EnumAction.block || item.getItemUseAction(null) == EnumAction.none) {

                Class c = item.getClass();
                while (!(c.equals(Item.class) || c.equals(ItemTool.class) || c.equals(ItemSword.class))) {
                    try {
                        try {
                            c.getDeclaredMethod(itemBlackListMethodNames[0], itemBlackListMethodParams[0]);
                            return true;
                        } catch (NoSuchMethodException ignored) {
                        }

                        try {
                            c.getDeclaredMethod(itemBlackListMethodNames[1], itemBlackListMethodParams[1]);
                            return true;
                        } catch (NoSuchMethodException ignored) {
                        }
                    } catch (NoClassDefFoundError ignored) {

                    }

                    c = c.getSuperclass();
                }

                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            return true;
        }
    }


    public static ItemStack readItemStack(ByteArrayDataInput par0DataInputStream) throws IOException {
        ItemStack itemstack = null;
        short short1 = par0DataInputStream.readShort();

        if (short1 >= 0) {
            byte b0 = par0DataInputStream.readByte();
            short short2 = par0DataInputStream.readShort();
            itemstack = new ItemStack(short1, b0, short2);
            itemstack.stackTagCompound = readNBTTagCompound(par0DataInputStream);
        }

        return itemstack;
    }

    /**
     * Reads a compressed NBTTagCompound from the InputStream
     */
    public static NBTTagCompound readNBTTagCompound(ByteArrayDataInput par0DataInputStream) throws IOException {
        short short1 = par0DataInputStream.readShort();

        if (short1 < 0) {
            return null;
        } else {
            byte[] abyte = new byte[short1];
            par0DataInputStream.readFully(abyte);

            return CompressedStreamTools.decompress(abyte);
        }
    }


    public static void writeItemStack(ByteArrayDataOutput par1DataOutputStream, ItemStack par0ItemStack) throws IOException {

        if (par0ItemStack == null) {
            par1DataOutputStream.writeShort(-1);
        } else {
            par1DataOutputStream.writeShort(par0ItemStack.itemID);
            par1DataOutputStream.writeByte(par0ItemStack.stackSize);
            par1DataOutputStream.writeShort(par0ItemStack.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (par0ItemStack.getItem().isDamageable() || par0ItemStack.getItem().getShareTag()) {
                nbttagcompound = par0ItemStack.stackTagCompound;
            }

            writeNBTTagCompound(nbttagcompound, par1DataOutputStream);
        }

    }

    protected static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, ByteArrayDataOutput par1DataOutputStream) throws IOException {
        if (par0NBTTagCompound == null) {
            par1DataOutputStream.writeShort(-1);
        } else {
            byte[] abyte = CompressedStreamTools.compress(par0NBTTagCompound);
            par1DataOutputStream.writeShort((short) abyte.length);
            par1DataOutputStream.write(abyte);
        }
    }

    public static InventoryPlayer replaceInventory(EntityPlayer entityPlayer) {
        return new InventoryPlayerBattle(entityPlayer);
    }
}
