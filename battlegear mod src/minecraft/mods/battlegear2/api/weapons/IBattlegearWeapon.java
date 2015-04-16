package mods.battlegear2.api.weapons;

import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandDual;
import mods.battlegear2.api.ISheathed;

/**
 * A generic flag for weapon, to be implemented in any Item instance
 * <strong>Not</strong> necessary for an item to be wielded in battlegear slots
 * Note: Next version will replace IOffhandDual with IOffhandWield
 */
public interface IBattlegearWeapon extends ISheathed,IOffhandDual,IAllowItem{

}