package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.weapons.IBackStabbable;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
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
        GameRegistry.register(this);
	}
	
	@Override//Daggers can harvest tallgrass and wool
	public boolean canHarvestBlock(IBlockState par1Block) {
        return par1Block.getBlock() instanceof BlockTallGrass || par1Block.getBlock() == Blocks.WOOL;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
        if(slot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
            map.put(extendedReach.getName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
            map.put(attackSpeed.getName(), new AttributeModifier(attackSpeedUUID, "Speed Modifier", this.hitTime, 0));
        }
        return map;
    }
	
	@Override//Here we simply cause more damage (hit will touch twice, one here and the other called vanilla)
	public boolean onBackStab(EntityLivingBase entityHit, EntityLivingBase entityHitting){
        return entityHit.attackEntityFrom(new EntityDamageSource(Battlegear.CUSTOM_DAMAGE_SOURCE + ".backstab", entityHitting), this.baseDamage);
    }
}
