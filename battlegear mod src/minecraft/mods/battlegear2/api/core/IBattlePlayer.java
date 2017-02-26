package mods.battlegear2.api.core;

import net.minecraft.entity.Entity;

/**
 * Interface added to {@link EntityPlayer} to support offhand management
 * @author GotoLink
 */
public interface IBattlePlayer{

    /**
     * Hotswap the {@link EntityPlayer} current item to offhand, behaves like
     * {@link EntityPlayer#attackTargetEntityWithCurrentItem(Entity)}
     * @param target to attack
     */
    void attackTargetEntityWithCurrentOffItem(Entity target);

    /**
     * Checks {@link InventoryPlayerBattle#isBattlemode()}, to see if current item is offset in the battle slots range
     * @return true if player has pressed the bound key to activate dual-wielding, resulting in current item offset
     */
    boolean isBattlemode();

    /**
     * Helper for {@link IShield} usage
     * @return true if a {@link IShield} is being used in offhand
     */
    boolean isBlockingWithShield();

    /**
     * Helper for {@link IShield} usage,
     * sets the flag according to argument if {@link IShield} is being held in offhand
     * @param block new value for the shield block flag
     */
    void setBlockingWithShield(boolean block);

    /**
     * Getter for the special timer field
     * @return the field value
     */
    int getSpecialActionTimer();

    /**
     * Setter for the special timer field
     * @param time new value to set
     */
    void setSpecialActionTimer(int time);
}