package mods.battlegear2.items;

import mods.battlegear2.api.IBackStabbable;
import mods.battlegear2.api.ISpecialEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemMace extends OneHandedWeapon implements ISpecialEffect {

	public ItemMace(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
	}

	@Override
	public PotionEffect[] getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		return new PotionEffect[]{
				new PotionEffect(2,100),new PotionEffect(9,100),
				new PotionEffect(15,100),new PotionEffect(18,100)};
	}

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);


        par3List.add(EnumChatFormatting.GOLD+StatCollector.translateToLocal("attribute.name.weapon.daze"));


    }
}
