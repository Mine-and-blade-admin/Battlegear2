package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemMace extends OneHandedWeapon {

    private float stunChance;

	public ItemMace(ToolMaterial material, String name, float stunChance) {
		super(material,name);
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 1;
        this.stunChance = stunChance;
        this.setMaxDamage(material.getMaxUses() * 2);
        GameRegistry.registerItem(this, this.name);
	}

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(daze.getAttributeUnlocalizedName(), new AttributeModifier(dazeUUID, "Daze Modifier", this.stunChance, 0));
        return map;
    }
}
