package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.api.weapons.ISpecialEffect;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon,ISpecialEffect {

    //Will make it one more than a sword
    private final int mounted_extra_damage;
    private final float reach;
    public IIcon bigIcon;

    public ItemSpear(ToolMaterial material, String name, int mount, float reach) {
		super(material,name);
        this.mounted_extra_damage = mount;
        this.reach = reach;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
        GameRegistry.registerItem(this, this.name);
	}
	
	@Override
	public float getReachModifierInBlocks(ItemStack stack) {
		return getModifiedAmount(stack, extendedReach.getAttributeUnlocalizedName());
	}

	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
		return offhand==null || offhand.getItem() instanceof IShield;
	}

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(extendedReach.getAttributeUnlocalizedName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
        map.put(mountedBonus.getAttributeUnlocalizedName(), new AttributeModifier(mountedBonusUUID, "Attack Modifier", this.mounted_extra_damage, 0));
        return map;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
        return par1ItemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        bigIcon = par1IconRegister.registerIcon(this.getIconString()+".big");
    }

    @Override
    public boolean performEffects(EntityLivingBase entityHit, EntityLivingBase entityHitting) {
        if(entityHitting.isRiding() || entityHitting.isSprinting())
        {
            entityHit.attackEntityFrom(new EntityDamageSource(Battlegear.CUSTOM_DAMAGE_SOURCE+".mounted", entityHitting), mounted_extra_damage);
            return true;
        }else{
            return false;
        }
    }
}
