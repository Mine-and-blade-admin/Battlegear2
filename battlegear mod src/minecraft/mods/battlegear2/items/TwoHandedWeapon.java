package mods.battlegear2.items;

import mods.battlegear2.api.weapons.WeaponRegistry;
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

	/**
	 * Make this item instance right-hand only
	 */
	@Override
	public WeaponRegistry.Wield getWieldStyle(ItemStack itemStack, EntityPlayer player) {
		return WeaponRegistry.Wield.RIGHT;
	}

	@Override
	public boolean sheatheOnBack(ItemStack item) {
		return true;
	}
}
