package mods.battlegear2.items;

import mods.battlegear2.api.IShield;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.api.weapons.ISpecialEffect;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon,ISpecialEffect {

    //Will make it one more than a sword
    int mounted_extra_damage = 3;
    public Icon bigIcon;

    public ItemSpear(int par1, EnumToolMaterial material, String name) {
		super(par1,material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
	}
	
	@Override
	public float getReachModifierInBlocks(ItemStack stack) {
		return 2.0F;
	}

	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
		return offhand==null || offhand.getItem() instanceof IShield;
	}

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);

        bigIcon = par1IconRegister.registerIcon(this.getIconString()+".big");

    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        par3List.add(EnumChatFormatting.DARK_GREEN+
                StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                        new Object[] {decimal_format.format(mounted_extra_damage),
                                StatCollector.translateToLocal("attribute.weapon.mountedBonus")}));
    }

    @Override
    public boolean performEffects(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
        if(entityHitting.isRiding() || entityHitting.isSprinting())
        {
            entityHit.attackEntityFrom(new EntityDamageSource("battlegearExtra", entityHitting), mounted_extra_damage/2);
            return true;
        }else{
            return false;
        }
    }
}
