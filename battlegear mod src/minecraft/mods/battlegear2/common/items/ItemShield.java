package mods.battlegear2.common.items;

import mods.battlegear2.api.OffhandAttackEvent;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShield extends ItemWeapon implements IHeraldryItem{
	
	private Icon baseIcon;
	private Icon postRenderIcon;
	private Icon shadeIcon;
	private Icon backIcon;

	public ItemShield(int par1, int i) {
		super(par1,i);
		this.name="battlegear2:shields/Shield-"+i;
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		super.updateIcons(par1IconRegister);
		
		baseIcon = par1IconRegister.registerIcon(this.name+"-base");
		postRenderIcon = par1IconRegister.registerIcon(this.name+"-trim");
		shadeIcon = par1IconRegister.registerIcon(this.name+"-shade");
		backIcon = par1IconRegister.registerIcon(this.name+"-back");
	}
	
	/**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 72000;
    }

	@Override
	public boolean willAllowOffhandWeapon() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isOffhandHandDualWeapon() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean sheatheOnBack() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean offhandAttackEntity(OffhandAttackEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		event.entityPlayer.setItemInUse(offhandItem, 300000);
		return false;
	}
	
	@Override
	public boolean offhandClickAir(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		event.entityPlayer.setItemInUse(offhandItem, 300000);
		return false;
	}
	
	
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.none;
	}



	@Override
	public boolean offhandClickBlock(PlayerInteractEvent event,
			ItemStack mainhandItem, ItemStack offhandItem) {
		event.entityPlayer.setItemInUse(offhandItem, 0);
		return false;
	}
	
	@Override
	public void performPassiveEffects(Side effectiveSide,
			ItemStack mainhandItem, ItemStack offhandItem) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Icon getBaseIcon() {
		return baseIcon;
	}
	
	@Override
	public Icon getPostRenderIcon() {
		return postRenderIcon;
	}
	
	

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		return true;
	}

	@Override
	public int getHeraldryCode(ItemStack stack) {
		return -464322478;
	}
	
	
}
