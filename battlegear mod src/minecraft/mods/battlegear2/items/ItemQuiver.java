package mods.battlegear2.items;

import mods.battlegear2.api.IArrowContainer;
import mods.battlegear2.api.QuiverArrowEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemQuiver extends Item implements IArrowContainer{

	public ItemQuiver(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(128);
		//This serves as the arrow count, more than 64 to provide an advantage over a simple arrow stack
	}

	@Override
	public boolean hasArrow(ItemStack stack) {
		return stack.getItemDamage()<this.getMaxDamage();
	}

	@Override
	public EntityArrow getArrowType(ItemStack stack, World world, EntityPlayer player, float charge) {
		return new EntityArrow(world, player, charge);
	}

	@Override
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow, EntityArrow arrow) {
		stack.setItemDamage(stack.getItemDamage()+1);
	}

	@Override
	public void onPreArrowFired(QuiverArrowEvent arrowEvent) {	
	}

}
