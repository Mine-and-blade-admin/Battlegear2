package mods.battlegear2.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerEventChild extends PlayerEvent{

	public final PlayerEvent parent;

	public PlayerEventChild(PlayerEvent parent) {
		super(parent.entityPlayer);
		this.parent = parent;
	}

    public void setCancelParentEvent(boolean cancel) {
        parent.setCanceled(cancel);
    }

    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        parent.setCanceled(cancel);
    }

    @Override
    public void setResult(Result value) {
        super.setResult(value);
        parent.setResult(value);
    }

    public static class OffhandAttackEvent extends PlayerEventChild {

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

    public static class QuiverArrowEvent extends PlayerEventChild{
        /**
         * The event from which this occurred
         */
        protected final ArrowLooseEvent event;
        public QuiverArrowEvent(ArrowLooseEvent event){
            super(event);
            this.event = event;
        }

        /**
         * @return the player using the bow
         */
        public EntityPlayer getArcher(){
            return event.entityPlayer;
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
        public static class Firing extends QuiverArrowEvent {
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
        public static class ChargeCalculations extends QuiverArrowEvent {
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
}
