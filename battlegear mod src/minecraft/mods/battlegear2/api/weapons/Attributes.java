package mods.battlegear2.api.weapons;

import net.minecraft.entity.ai.attributes.RangedAttribute;

import java.util.UUID;

/**
 * Created by GotoLink on 31/10/2014.
 * Hold references from battlegear weapon attributes
 * No-OP
 */
public interface Attributes {

    UUID penetrateArmourUUID = UUID.fromString("db3f55d3-645c-4f38-a497-9c13a33db5cf");
    RangedAttribute armourPenetrate = new RangedAttribute("weapon.penetrateArmor", 0.0D, 0.0D, Double.MAX_VALUE);

    UUID dazeUUID = UUID.fromString("927f0df6-946e-4e78-a479-c2c13034edb5");
    RangedAttribute daze = new RangedAttribute("weapon.daze", 3.0D, 0.0D, Double.MAX_VALUE);

    UUID extendReachUUID = UUID.fromString("fb557a05-866e-4017-990b-aab8450bf41b");
    RangedAttribute extendedReach = new RangedAttribute("weapon.extendedReach", 0.0D, -5.0D, Double.MAX_VALUE);

    UUID attackSpeedUUID = UUID.fromString("4833af8b-40f2-44c5-8405-735f7003b1be");
    RangedAttribute attackSpeed = new RangedAttribute("weapon.attackSpeed", 0.0D, -10.0D, 10.0D);

    UUID mountedBonusUUID = UUID.fromString("fd234540-d099-4a05-a4ac-0ad7c11a8b65");
    RangedAttribute mountedBonus = new RangedAttribute("weapon.mountedBonus", 0.0D, 0.0D, Double.MAX_VALUE);
}
