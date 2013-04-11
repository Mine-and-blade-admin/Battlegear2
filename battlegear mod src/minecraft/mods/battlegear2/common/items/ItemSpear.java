package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;

public class ItemSpear extends ItemWeapon{

	public ItemSpear(int par1, int i) {
		super(par1,i);
		this.name="battlegear2:Spear-"+i;
	}

	@Override
	public boolean willAllowOffhandWeapon() {
		return false;
	}

	@Override
	public boolean isOffhandHandDualWeapon() {
		return true;
	}

	@Override
	public boolean sheatheOnBack() {
		return true;
	}

	@Override
	public boolean offhandAttackEntity(OffhandAttackEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offhandClickAir(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offhandClickBlock(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void performPassiveEffects(Side effectiveSide,
			ItemStack mainhandItem, ItemStack offhandItem) {
		// TODO Auto-generated method stub
		
	}

}
