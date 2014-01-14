package mods.battlegear2.api.quiver;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

/**
 * Event fired after an arrow has been selected and taken from a {@link #IArrowContainer2}, before it is actually spawned
 */
@Cancelable
public class QuiverArrowEvent extends PlayerEventChild{

    /**
     * Damage done to the bow after arrow is fired
     */
	public int bowDamage = 1;
    /**
     * The volume of the sound emitted from the bow after arrow is fired
     */
	public float bowSoundVolume = 1.0F;
    /**
     * Decides if standard enchantments can be added to the arrow
     */
	public boolean addEnchantments = true;
    /**
     * The quiver from which the arrow was pulled from
     */
    public final ItemStack quiver;
    /**
     * The arrow to be fired, can't be null
     */
    public final EntityArrow arrow;
    /**
     * The event from which this occurred
     */
	protected final ArrowLooseEvent event;

	public QuiverArrowEvent(ArrowLooseEvent parent, ItemStack quiver, EntityArrow arrow) {
		super(parent);
		this.event = parent;
        this.quiver = quiver;
        this.arrow = arrow;
	}

    /**
     * @return the bow trying to fire
     */
	public ItemStack getBow()
	{
		return event.bow;
	}

    /**
     * @return the amount of charge in the bow
     */
	public int getCharge()
	{
		return event.charge;
	}
}
