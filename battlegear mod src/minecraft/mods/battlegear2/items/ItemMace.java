package mods.battlegear2.items;

import mods.battlegear2.api.IBackStabbable;
import mods.battlegear2.api.IPotionEffect;
import mods.battlegear2.api.ISpecialEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemMace extends OneHandedWeapon implements IPotionEffect{

    private float stunChance;
    private Map<PotionEffect, Float> effects;
    private static NumberFormat percentFormat = NumberFormat.getPercentInstance();
    static{
        percentFormat.setMaximumFractionDigits(0);
    }

	public ItemMace(int par1, EnumToolMaterial material, String name, float stunChance) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage = baseDamage-1;
        effects= new HashMap<PotionEffect,Float>();
        this.stunChance = stunChance;
        stunChance = 1;
        effects.put(new PotionEffect(2,3*20,100), stunChance);
        effects.put(new PotionEffect(9,3*20,100), stunChance);
        effects.put(new PotionEffect(15,3*20,100), stunChance);
        effects.put(new PotionEffect(18,3*20,100), stunChance);

	}

    public Map<PotionEffect, Float> getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting){
        return effects;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        par3List.add(EnumChatFormatting.GOLD+StatCollector.translateToLocal("attribute.weapon.daze")+" "+percentFormat.format(stunChance));
    }
}
