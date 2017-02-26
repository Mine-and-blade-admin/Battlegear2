package mods.battlegear2.api.weapons;

import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.IWield;

/**
 * A generic flag for weapon, to be implemented in any Item instance
 * <strong>Not</strong> necessary for an item to be wielded in battlegear slots
 */
public interface IBattlegearWeapon extends ISheathed,IWield,IAllowItem{

}