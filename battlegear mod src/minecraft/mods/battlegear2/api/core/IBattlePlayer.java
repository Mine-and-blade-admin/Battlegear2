package mods.battlegear2.api.core;

import net.minecraft.entity.Entity;

/**
 * Interface added to {@link EntityPlayer} to support offhand management
 * @author GotoLink
 */
public interface IBattlePlayer{

    public void swingOffItem();

    public float getOffSwingProgress(float frame);

    public void attackTargetEntityWithCurrentOffItem(Entity target);

    public boolean isBattlemode();

    public boolean isBlockingWithShield();

    public void setBlockingWithShield(boolean block);

    public int getSpecialActionTimer();

    public void setSpecialActionTimer(int time);
}