package mods.battlegear2.items;

import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class OneHandedWeapon extends ItemWeapon{

	public OneHandedWeapon(ToolMaterial material, String named) {
		super(material, named);
	}
	
	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand, EntityPlayer player) {
		return true;
	}

	@Override
	public WeaponRegistry.Wield getWieldStyle(ItemStack itemStack, EntityPlayer player) {
		return WeaponRegistry.Wield.BOTH;
	}

	@Override
	public boolean sheatheOnBack(ItemStack item) {
		return false;
	}

	@Override
	public int getItemEnchantability() {
        return this.getMaterial().getEnchantability();
    }
}
