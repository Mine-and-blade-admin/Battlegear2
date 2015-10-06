package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.weapons.IBackStabbable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemDagger extends OneHandedWeapon implements IBackStabbable {
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
	
	@Override//Daggers can harvest tallgrass and wool
	public boolean canHarvestBlock(Block par1Block) {
        return par1Block == Blocks.tallgrass||par1Block == Blocks.wool;
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(extendedReach.getAttributeUnlocalizedName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
        map.put(attackSpeed.getAttributeUnlocalizedName(), new AttributeModifier(attackSpeedUUID, "Speed Modifier", this.hitTime, 0));
        return map;
    }
	
	@Override//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting){
        return entityHit.attackEntityFrom(new EntityDamageSource(Battlegear.CUSTOM_DAMAGE_SOURCE + ".backstab", entityHitting), this.baseDamage);
    }
}
