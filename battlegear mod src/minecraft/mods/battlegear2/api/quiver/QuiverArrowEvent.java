package mods.battlegear2.api.quiver;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

public class QuiverArrowEvent extends PlayerEventChild{
    /**
     * The event from which this occurred
     */
    protected final ArrowLooseEvent event;
    public QuiverArrowEvent(ArrowLooseEvent event){
        super(event);
        this.event = event;
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
    public float getCharge()
    {
        return event.charge;
    }

    /**
     * Event fired after an arrow has been selected and taken from a {@link #IArrowContainer2}, before it is actually spawned
     */
    @Cancelable
    public static class Firing extends QuiverArrowEvent{
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
         * Decides if critical state should be forced into the arrow
         */
        public boolean isCritical = false;
        /**
         * The quiver from which the arrow was pulled from
         */
        public final ItemStack quiver;
        /**
         * The arrow to be fired, can't be null
         */
        public final EntityArrow arrow;

        public Firing(ArrowLooseEvent parent, ItemStack quiver, EntityArrow arrow) {
            super(parent);
            this.quiver = quiver;
            this.arrow = arrow;
        }

    }

    @HasResult
    public static class ChargeCalculations extends QuiverArrowEvent{
        protected float charge;
        public ChargeCalculations(ArrowLooseEvent event){
            super(event);
        }

        @Override
        public float getCharge(){
            MinecraftForge.EVENT_BUS.post(this);
            switch (this.getResult()){
                case ALLOW:
                    return charge;
                case DENY:
                    return 0;
            }
            float f = super.getCharge()/20.0F;
            f = (f * f + f * 2.0F) / 3.0F;
            if ((double)f < 0.1D)
            {
                return 0;
            }
            if (f > 1.0F)
            {
                f = 1.0F;
            }
            return f;
        }

        public void setNewCharge(float charge){
            this.setResult(Result.ALLOW);
            this.charge = charge;
        }
    }
}
