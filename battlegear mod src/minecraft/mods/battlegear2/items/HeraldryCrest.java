package mods.battlegear2.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import java.util.List;

public class HeraldryCrest extends ItemMap implements IHeraldryItem{
    public HeraldryCrest() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public IIcon getBaseIcon(ItemStack stack) {
        return null;
    }

    @Override
    public IIcon getTrimIcon(ItemStack stack) {
        return null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World world, EntityPlayer player){
        player.openGui(Battlegear.INSTANCE, BattlegearGUIHandeler.flagEditor, world, 0, 0, 0);
        return par1ItemStack;
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack){
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public IIcon getPostRenderIcon(ItemStack stack) {
        return null;
    }

    @Override
    public boolean hasHeraldry(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(heraldryTag);
    }

    @Override
    public byte[] getHeraldry(ItemStack stack) {
        if(!stack.hasTagCompound()){
            return HeraldryData.getDefault().getByteArray();
        }
        NBTTagCompound compound = stack.getTagCompound();
        if(compound.hasKey(heraldryTag)){
            return compound.getByteArray(heraldryTag);
        }else{
            return HeraldryData.getDefault().getByteArray();
        }
    }

    @Override
    public void removeHeraldry(ItemStack item) {
        if(item.hasTagCompound()){
            item.getTagCompound().removeTag(heraldryTag);
        }
    }

    @Override
    public void setHeraldry(ItemStack stack, byte[] data) {
        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setByteArray(heraldryTag, data);
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
        return !pass.equals(HeraldyRenderPassess.SecondaryColourTrim);
    }


    public MapData getMapData(ItemStack par1ItemStack, World par2World)
    {return null;}

    public void updateMapData(World par1World, Entity par2Entity, MapData par3MapData)
    {}

    @Override
    public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {}

    @Override
    public Packet func_150911_c(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {return null;}

    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {}
}
