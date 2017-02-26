package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMace extends OneHandedWeapon {

    private float stunChance;

	public ItemMace(ToolMaterial material, String name, float stunChance) {
		super(material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 1;
        this.stunChance = stunChance;
        this.setMaxDamage(material.getMaxUses() * 2);
        GameRegistry.register(this);
	}

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
        if(slot.getSlotType() == EntityEquipmentSlot.Type.HAND)
            map.put(daze.getName(), new AttributeModifier(dazeUUID, "Daze Modifier", this.stunChance, 0));
        return map;
    }
}
