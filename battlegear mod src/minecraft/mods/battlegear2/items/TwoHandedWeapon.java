package mods.battlegear2.items;

import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.api.weapons.OffhandAttackEvent;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public abstract class TwoHandedWeapon extends ItemWeapon{

	public TwoHandedWeapon(int par1, EnumToolMaterial material, String named) {
		super(par1, material, named);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.none;
    }
	
	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
		return false;
	}

	@Override
	public boolean isOffhandHandDual(ItemStack off) {
		return false;
	}

	@Override
	public boolean sheatheOnBack(ItemStack item) {
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
