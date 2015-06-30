package mods.battlegear2.api.weapons;

import net.minecraft.entity.ai.attributes.RangedAttribute;

import java.util.UUID;

/**
 * Created by GotoLink on 31/10/2014.
 * Hold UUID references from battlegear weapon attributes
 * Note: The attributes will also apply to modifiers added in armor items, due to the way attributes are implemented by Minecraft
 */
public interface Attributes {

    /**
     * Used by battlegear modifier in waraxes for corresponding attribute
     * UUIDMost:-2648303689460330696
     * UUIDLeast:-6586624321849018929
     */
    UUID penetrateArmourUUID = UUID.fromString("db3f55d3-645c-4f38-a497-9c13a33db5cf");
    /**
     * Additional "generic" damage by-passing armor applied when an entity attack another (including players)
     */
    RangedAttribute armourPenetrate = new RangedAttribute(null, "weapon.penetrateArmor", 0.0D, 0.0D, Double.MAX_VALUE);

    /**
     * Used by battlegear modifier in maces for corresponding attribute
     * UUIDMost:-7890572669426446728
     * UUIDLeast:-6595026044314128971
     */
    UUID dazeUUID = UUID.fromString("927f0df6-946e-4e78-a479-c2c13034edb5");
    /**
     * Chance for dazing an entity on attack (including players)
     * The implementation apply various "bad" potion effects
     * Base value is 0, so at least one modifier with "add" (ie index=0) operation is required
     * for other modifiers to do anything
     */
    RangedAttribute daze = new RangedAttribute(null, "weapon.daze", 0.0D, 0.0D, 1.0D);

    /**
     * Used by battlegear modifier in spears and daggers for corresponding attribute
     * UUIDMost:-336228433020436457
     * UUIDLeast:-7418648252753644517
     */
    UUID extendReachUUID = UUID.fromString("fb557a05-866e-4017-990b-aab8450bf41b");
    /**
     * Distance added to the default maximum reach for attacks
     * Can reduce or increase said range
     * Limited to players
     */
    RangedAttribute extendedReach = (RangedAttribute) new RangedAttribute(null, "weapon.extendedReach", 0.0D, -5.0D, Double.MAX_VALUE).setShouldWatch(true);

    /**
     * Used by battlegear modifier in daggers for corresponding attribute
     * UUIDMost:5202695007167988933
     * UUIDLeast:-8933607432081133122
     */
    UUID attackSpeedUUID = UUID.fromString("4833af8b-40f2-44c5-8405-735f7003b1be");
    /**
     * Factor removed from max hit-shield time on hitting an entity (note: requires a weapon held)
     * Base value is 0, so at least one modifier with "add" (ie index=0) operation is required
     * for other modifiers to do anything
     */
    RangedAttribute attackSpeed = new RangedAttribute(null, "weapon.attackSpeed", 0.0D, -10.0D, 10.0D);

    /**
     * Used by battlegear modifier in spears for corresponding attribute
     * UUIDMost:-206245013248980475
     * UUIDLeast:-6580873033721148571
     */
    UUID mountedBonusUUID = UUID.fromString("fd234540-d099-4a05-a4ac-0ad7c11a8b65");
    /**
     * Additional damage applied when a living attacker is riding
     */
    RangedAttribute mountedBonus = new RangedAttribute(null, "weapon.mountedBonus", 0.0D, 0.0D, Double.MAX_VALUE);
}
