package mods.battlegear2.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.weapons.IPotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

	public ItemMace(ToolMaterial material, String name, float stunChance) {
		super(material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 1;
        effects= new HashMap<PotionEffect,Float>();
        this.stunChance = stunChance;
        effects.put(new PotionEffect(2,3*20,100), stunChance);
        effects.put(new PotionEffect(9,3*20,100), stunChance);
        effects.put(new PotionEffect(15,3*20,100), stunChance);
        effects.put(new PotionEffect(18,3*20,100), stunChance);
        this.setMaxDamage(material.getMaxUses() * 2);
        GameRegistry.registerItem(this, this.name);
	}

    public Map<PotionEffect, Float> getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting){
        return effects;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        par3List.add(EnumChatFormatting.GOLD+StatCollector.translateToLocal("attribute.name.weapon.daze")+" "+percentFormat.format(stunChance));
    }
}
