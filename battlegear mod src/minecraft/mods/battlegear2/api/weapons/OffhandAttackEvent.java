package mods.battlegear2.api.weapons;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class OffhandAttackEvent extends PlayerEventChild {

    public boolean swingOffhand = true;
    public boolean shouldAttack = true;
    public boolean cancelParent = true;
    public final EntityInteractEvent event;
    public final ItemStack mainHand;
    public final ItemStack offHand;

    public OffhandAttackEvent(EntityInteractEvent parent, ItemStack mainHand, ItemStack offHand) {
        super(parent);
        this.event = parent;
        this.mainHand = mainHand;
        this.offHand = offHand;
    }

	public Entity getTarget() {
        return event.target;
    }
}