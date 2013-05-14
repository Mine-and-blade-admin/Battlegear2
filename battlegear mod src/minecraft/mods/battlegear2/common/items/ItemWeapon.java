package mods.battlegear2.common.items;

import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon{

	protected final EnumToolMaterial material;
	protected String name;
	protected int baseDamage;
	
	@SideOnly(Side.CLIENT)
	public static Icon[] backgroundIcon;
	
	public ItemWeapon(int par1, EnumToolMaterial material, String named) {
		super(par1, material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficent)
		this.material = material;
		
		this.setCreativeTab(BattlegearConfig.customTab);
		this.maxStackSize = 1;
		this.setUnlocalizedName("battlegear2:"+named+material.ordinal());
		this.name="battlegear2:"+named+material.ordinal();
		
		this.baseDamage = 4 + material.getDamageVsEntity();
	}
	
	@SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon(this.name);
        
        //Make sure this only does it once
        if(this.backgroundIcon == null){
			this.backgroundIcon=new Icon[2];
	        for (int i=0;i<2;i++){
	        	this.backgroundIcon[i]=
	        			par1IconRegister.registerIcon(
	        					"battlegear2:slots/".concat(i==0?"mainhand":"offhand"));
	        }
        }
    }
	
	public EnumToolMaterial getMaterial() {
		return this.material;
	}
	
	
	
	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return this.material.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {
        return this.baseDamage;
    }
	
	@SideOnly(Side.CLIENT)
    public static Icon getBackground(boolean mainhand)
    {  
		return mainhand?backgroundIcon[0]:backgroundIcon[1];
    }
}
