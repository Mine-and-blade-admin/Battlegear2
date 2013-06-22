package mods.battlegear2.common.items;

import java.util.List;

import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StringTranslate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon{

	protected final EnumToolMaterial material;
	protected String name;
	protected int baseDamage;
	
	public ItemWeapon(int par1, EnumToolMaterial material, String named) {
		super(par1, material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficent)
		this.material = material;
		
		this.setCreativeTab(BattlegearConfig.customTab);
		this.maxStackSize = 1;
		
		if(material == EnumToolMaterial.EMERALD){
			this.name = named+".diamond";
		}else{
			this.name= named+"."+material.name().toLowerCase();
		}
		
		
		this.setUnlocalizedName("battlegear2:"+name);
		
		
		this.baseDamage = 4 + material.getDamageVsEntity();
	}
	
	
	public EnumToolMaterial getMaterial() {
		return this.material;
	}

	@Override
	public int getDamageVsEntity(Entity par1Entity)
    {
        return this.baseDamage;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		//TODO Change/Remove this when 1.6 is released
		par3List.add(String.format("%s +%d %s", EnumChatFormatting.BLUE, (baseDamage-1), 
				StringTranslate.getInstance().translateKey("tooltip.attack")));
	}
	
	
	
}
