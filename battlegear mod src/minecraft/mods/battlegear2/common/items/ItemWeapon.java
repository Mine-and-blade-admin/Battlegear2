package mods.battlegear2.common.items;

import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemWeapon extends Item implements IBattlegearWeapon{

	private final EnumToolMaterial material;
	protected String name;
	public ItemWeapon(int par1, int i) {
		super(par1);
		switch(i)
		{
		case 0:this.material=EnumToolMaterial.WOOD;break;
		case 1:this.material=EnumToolMaterial.STONE;break;
		case 2:this.material=EnumToolMaterial.IRON;break;
		case 3:this.material=EnumToolMaterial.EMERALD;break;
		case 4:this.material=EnumToolMaterial.GOLD;break;
		default:this.material=EnumToolMaterial.WOOD;break;
		}
		this.setCreativeTab(BattlegearConfig.customTab);
	}
	@SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister)
    {
        this.iconIndex = par1IconRegister.registerIcon(this.name);
    }
}
