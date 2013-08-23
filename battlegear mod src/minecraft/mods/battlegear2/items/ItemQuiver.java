package mods.battlegear2.items;

import java.util.ArrayList;
import java.util.List;

import mods.battlegear2.api.IArrowContainer;
import mods.battlegear2.api.QuiverArrowEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

@Deprecated
public class ItemQuiver extends Item implements IArrowContainer{

	public static final String TAG = "Arrows";
	public static int maxArrow = 256;
	//This serves as the arrow limit, more than 64 to provide an advantage over a simple arrow stack
	public ItemQuiver(int par1) {
		super(par1);
		this.setMaxStackSize(1);
	}

    public void dyeQuiver(ItemStack quiver, ArrayList<ItemStack> dyes){



    }


	@Override
	public boolean hasArrowFor(ItemStack stack, ItemStack bow, EntityPlayer player) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger(TAG)>0;
		else
			return false;
	}

	@Override
	public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge) {
		return new EntityArrow(world, player, charge);
	}

	@Override
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {
		stack.getTagCompound().setInteger(TAG, stack.getTagCompound().getInteger(TAG)-1);
	}

	@Override
	public void onPreArrowFired(QuiverArrowEvent arrowEvent) {	
	}

	@Override
	public boolean isCraftableWithArrows(ItemStack stack) {
		return true;
	}
	
	@Override
	public int addArrows(ItemStack quiver, int arrows) {
		int drop;
		if(quiver.hasTagCompound())
		{
			drop = quiver.getTagCompound().getInteger(TAG)+arrows-maxArrow; 
			if(drop>=0)
				quiver.getTagCompound().setInteger(TAG,maxArrow);
			else
				quiver.getTagCompound().setInteger(TAG,drop+maxArrow);
		}
		else
		{
			drop = arrows-maxArrow;
			NBTTagCompound tag = new NBTTagCompound();
			if(drop>=0)
				tag.setInteger(TAG, maxArrow);
			else
				tag.setInteger(TAG, arrows);
			quiver.setTagCompound(tag);
		}
		return drop>0?drop:0;
	}

	@Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(stack, par2EntityPlayer, list, par4);
        if(stack.hasTagCompound())
        {
        	int arrow = stack.getTagCompound().getInteger(TAG);
        	if(arrow>0)
        	{
        		list.add(String.format("%s%s %s", EnumChatFormatting.DARK_GREEN,arrow,StatCollector.translateToLocal("attribute.quiver.arrow.count")));
        		return;
        	}
        }
        list.add(String.format("%s %s", EnumChatFormatting.RED, StatCollector.translateToLocal("attribute.quiver.arrow.empty")));
	}
}
