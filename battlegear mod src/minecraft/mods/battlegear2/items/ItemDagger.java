package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.weapons.IBackStabbable;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.api.weapons.IHitTimeModifier;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;

public class ItemDagger extends OneHandedWeapon implements IBackStabbable,IHitTimeModifier,IExtendedReachWeapon{
    private final float hitTime;
    private final float reach;
	public ItemDagger(ToolMaterial material, String name, float hitTime, float reach) {
		super(material, name);
        this.hitTime = hitTime;
        this.reach = reach;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
        GameRegistry.registerItem(this, this.name);
	}
	
	@Override
	public boolean func_150897_b(Block par1Block)//Daggers can harvest tallgrass and wool
    {
        return par1Block == Blocks.tallgrass||par1Block == Blocks.wool;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(extendedReach.getAttributeUnlocalizedName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
        map.put(attackSpeed.getAttributeUnlocalizedName(), new AttributeModifier(attackSpeedUUID, "Speed Modifier", this.hitTime, 1));
        return map;
    }

	@Override
	public int getHitTime(ItemStack stack, EntityLivingBase target) {
		return -(int)(getModifiedAmount(stack, attackSpeed.getAttributeUnlocalizedName())*10);
	}
	
	@Override//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting){
        return entityHit.attackEntityFrom(new EntityDamageSource(Battlegear.CUSTOM_DAMAGE_SOURCE+".backstab", entityHitting), this.baseDamage);
	}

    @Override
    public float getReachModifierInBlocks(ItemStack stack) {
        return getModifiedAmount(stack, extendedReach.getAttributeUnlocalizedName());
    }
}
