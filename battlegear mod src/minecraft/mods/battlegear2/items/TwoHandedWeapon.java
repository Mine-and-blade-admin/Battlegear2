package mods.battlegear2.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
		return par1ItemStack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack){
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack){
		return 0;
	}
}
