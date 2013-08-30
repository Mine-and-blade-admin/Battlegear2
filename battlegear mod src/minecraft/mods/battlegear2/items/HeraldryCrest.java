package mods.battlegear2.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import java.util.List;

public class HeraldryCrest extends ItemMap implements IHeraldryItem{
    public HeraldryCrest(int par1) {
        super(par1);
        this.setCreativeTab(BattlegearConfig.customTab);
        this.setMaxStackSize(1);
        setUnlocalizedName("battlegear2:heraldric");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("battlegear2:bg-icon");
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    }

    @Override
    public Icon getBaseIcon(ItemStack stack) {
        return null;
    }

    @Override
    public Icon getTrimIcon(ItemStack stack) {
        return null;
    }

    @Override
    public boolean getShareTag() {
        return true;
    }


    @Override
    public boolean hasContainerItem(){
        return true;
    }

    @Override
    public ItemStack getContainerItemStack(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public Icon getPostRenderIcon(ItemStack stack) {
        return null;
    }

    @Override
    public boolean hasHeraldry(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("hc");
    }

    @Override
    public byte[] getHeraldry(ItemStack stack) {
        if(!stack.hasTagCompound()){
            return HeraldryData.getDefault().getByteArray();
        }
        NBTTagCompound compound = stack.getTagCompound();
        if(compound.hasKey("hc")){
            return compound.getByteArray("hc");
        }else{
            return HeraldryData.getDefault().getByteArray();
        }
    }

    @Override
    public void removeHeraldry(ItemStack item) {
        if(item.hasTagCompound()){
            item.getTagCompound().removeTag("hc");
        }
    }

    @Override
    public void setHeraldry(ItemStack stack, byte[] data) {
        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setByteArray("hc", data);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean useDefaultRenderer() {
        return false;
    }


    @Override
    public boolean shouldDoPass(HeraldyRenderPassess pass) {
        return ! pass.equals(HeraldyRenderPassess.SecondaryColourTrim);
    }




    public MapData getMapData(ItemStack par1ItemStack, World par2World)
    {return null;}

    public void updateMapData(World par1World, Entity par2Entity, MapData par3MapData)
    {}
    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {}

    /**
     * returns null if no update is to be sent
     */
    public Packet createMapDataPacket(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {return null;}

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {}

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {}
}
