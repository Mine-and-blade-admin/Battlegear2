package mods.battlegear2.items;

import mods.battlegear2.api.IArrowContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemQuiver extends Item implements IArrowContainer{

	public ItemQuiver(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(64);
	}

	@Override
	public boolean hasArrow(ItemStack stack) {
		return stack.getItemDamage()<64;
	}

	@Override
	public Class<? extends EntityArrow> getArrowType(ItemStack stack) {
		return EntityArrow.class;
	}

	@Override
	public void onArrowFired(World world, EntityPlayer player, ItemStack stack, ItemStack bow) {
		stack.setItemDamage(stack.getItemDamage()+1);
	}

}
