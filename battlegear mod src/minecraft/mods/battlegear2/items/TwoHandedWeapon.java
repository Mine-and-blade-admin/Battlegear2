package mods.battlegear2.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TwoHandedWeapon extends ItemWeapon{

	public TwoHandedWeapon(ToolMaterial material, String named) {
		super(material, named);
	}

	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand, EntityPlayer player) {
		return offhand == null;
	}

	@Override
	public boolean isOffhandWieldable(ItemStack off, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean sheatheOnBack(ItemStack item) {
		return true;
	}
}
