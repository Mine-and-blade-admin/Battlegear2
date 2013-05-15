package mods.battlegear2.common.items;

import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public abstract class TwoHandedWeapon extends ItemWeapon{

	public TwoHandedWeapon(int par1, EnumToolMaterial material, String named) {
		super(par1, material, named);
	}
	
	@Override
	public boolean willAllowOffhandWeapon() {
		return false;
	}
	@Override
	public boolean willAllowShield() {
		return false;
	}

	@Override
	public boolean isOffhandHandDualWeapon() {
		return false;
	}

	@Override
	public boolean sheatheOnBack() {
		return true;
	}
	
	@Override
	public boolean offhandAttackEntity(OffhandAttackEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		return true;
	}

	@Override
	public boolean offhandClickAir(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		return true;
	}

	@Override
	public boolean offhandClickBlock(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		return true;
	}

	@Override
	public void performPassiveEffects(Side effectiveSide,
			ItemStack mainhandItem, ItemStack offhandItem) {		
	}

}
