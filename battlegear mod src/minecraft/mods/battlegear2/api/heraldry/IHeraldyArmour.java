package mods.battlegear2.api.heraldry;

import net.minecraft.inventory.EntityEquipmentSlot;

public interface IHeraldyArmour extends IHeraldryItem{

	String getBaseArmourPath(EntityEquipmentSlot armourSlot);

    String getPatternArmourPath(PatternStore pattern, int index, EntityEquipmentSlot armourSlot);
}
