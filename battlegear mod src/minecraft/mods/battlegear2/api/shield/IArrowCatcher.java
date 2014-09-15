package mods.battlegear2.api.shield;

import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
/**
 * Defines an {@link Item} that can catch {@link IProjectile}
 * Used by {@link ItemShield} to block arrows
 * @author GotoLink
 */
public interface IArrowCatcher {
    /**
     *
     * @param shield the {@link ItemStack} corresponding to the shield in use
     * @param player the {@link EntityPlayer} holding the shield
     * @param projectile heading towards the shield
     * @return true if the projectile has been caught
     */
	public boolean catchArrow(ItemStack shield, EntityPlayer player, IProjectile projectile);
}
