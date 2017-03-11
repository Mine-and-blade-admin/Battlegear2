package mods.battlegear2.api;

import mods.battlegear2.api.quiver.IArrowContainer2;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public abstract class PlayerEventChild extends PlayerEvent{

    /**
     * The event that this event is a child of
     */
	public final PlayerEvent parent;

	public PlayerEventChild(PlayerEvent parent) {
		super(parent.getEntityPlayer());
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

    public EntityPlayer getPlayer(){
        return parent.getEntityPlayer();
    }
    
	/**
	 * Event fired when a shield successfully blocks an attack (in {@link LivingHurtEvent})
	 */
	 public static class ShieldBlockEvent extends PlayerEventChild {
	 	public final ItemStack shield;
	 	public final DamageSource source;
	 	public final float ammount; // use same name as other Forge events
	 	public float ammountRemaining = 0.0F; // damage remaining after shield block event, if any
        /**
         * If the {@link IShield#blockAnimation(EntityPlayer, float)} should be called
         */
        public boolean performAnimation = true;
        /**
         * If the shield should be damaged based on the ammount and the result of {@link IShield#getDamageReduction(ItemStack, DamageSource)}
         */
        public boolean damageShield = true;
	 	public ShieldBlockEvent(PlayerEvent parent, ItemStack shield, DamageSource source, float ammount) {
	 		super(parent);
	 		this.shield = shield;
	 		this.source = source;
	 		this.ammount = ammount;
	 	}
	 }

    /**
     * Called when a player right clicks in battlemode
     * The parent event can be either {@link PlayerInteractEvent} or {@link EntityInteractEvent} if the OffhandAttackEvent allowed swinging
     * Both {@link ItemStack} can be empty
     * If cancelled, no offhand swinging will be performed
     */
    @Cancelable
    public static class OffhandSwingEvent extends PlayerEventChild {
        public final ItemStack mainHand;
        public final ItemStack offHand;

        public OffhandSwingEvent(PlayerEvent parent){
            super(parent);
            this.mainHand = parent.getEntityPlayer().getHeldItemMainhand();
            this.offHand = parent.getEntityPlayer().getHeldItemOffhand();
        }

        public boolean onEntity(){
            return parent instanceof PlayerInteractEvent.EntityInteract;
        }

        public boolean onBlock(){
            return parent instanceof PlayerInteractEvent.RightClickBlock;
        }
    }

    /**
     * Called when a player right clicks an entity in battlemode
     * Both {@link ItemStack} can be empty
     * Cancelling will prevent any further processing and prevails over the boolean fields
     */
    @Cancelable
    public static class OffhandAttackEvent extends PlayerEventChild {

        /**
         * If we should call the OffhandSwingEvent and perform swinging animation
         */
        public boolean swingOffhand = true;
        /**
         * If we should perform an attack on the entity with the offhand item
         * Note: Will post {@link AttackEntityEvent} and {@link Item#onLeftClickEntity(ItemStack, EntityPlayer, Entity)}
         * with {@link InventoryPlayer#currentItem} offset to the offhand.
         */
        public boolean shouldAttack = true;
        /**
         * If we should Prevent {@link PlayerInteractEvent.RightClickItem} and
         * {@link ItemStack#useItemRightClick(World, EntityPlayer, EnumHand)}
         * from being called for the item in offhand.
         */
        public boolean cancelParent = true;
        /**
         * The base entity interaction event
         * This event has already been posted in EventBus, handled by all potential listeners and was not cancelled.
         * Changing its state will have no effect.
         */
        public final PlayerInteractEvent.EntityInteract event;
        /**
         * Content of the main hand slot
         */
        public final ItemStack mainHand;
        /**
         * Content of the off hand slot
         */
        public final ItemStack offHand;

        public OffhandAttackEvent(PlayerInteractEvent.EntityInteract parent) {
            super(parent);
            this.event = parent;
            this.mainHand = parent.getEntityPlayer().getHeldItemMainhand();
            this.offHand = parent.getEntityPlayer().getHeldItemOffhand();
        }

        public Entity getTarget() {
            return ((PlayerInteractEvent.EntityInteract)parent).getTarget();
        }
    }

    /**
     * This event replicates the event usage of {@link PlayerInteractEvent} for the item in left hand on right click,
     * allowing support for other mods that use such event to customize item usage
     * Item#onItemUseFirst(...), Item#onItemRightClick(...) and Item#onItemUse(...) will then get called the same way as with the item in the player right hand for PlayerInteractEvent
     */
    @Cancelable
    public static class UseOffhandItemEvent extends PlayerEventChild{
        /**
         * If we should call the OffhandSwingEvent and perform swinging animation
         */
        public boolean swingOffhand;
        /**
         * The {@link ItemStack} held in left hand
         */
        public final ItemStack offhand;
        /**
         * The equivalent {@link PlayerInteractEvent} that would have been triggered if the offhand item was held in right hand and right click was pressed
         */
        public final PlayerInteractEvent event;
        public UseOffhandItemEvent(PlayerInteractEvent event){
            super(event);
            this.event = event;
            this.offhand = event.getItemStack();
            this.swingOffhand = onBlock();
        }

        public boolean onBlock(){
            return event instanceof PlayerInteractEvent.RightClickBlock || event instanceof PlayerInteractEvent.LeftClickBlock;
        }

        public void setUseBlock(Result trigger){
            if(event instanceof PlayerInteractEvent.RightClickBlock){
                ((PlayerInteractEvent.RightClickBlock) event).setUseBlock(trigger);
            }else if(event instanceof PlayerInteractEvent.LeftClickBlock){
                ((PlayerInteractEvent.LeftClickBlock) event).setUseBlock(trigger);
            }
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
            return getPlayer();
        }

        /**
         * @return the bow trying to fire
         */
        public ItemStack getBow()
        {
            return event.getBow();
        }

        /**
         * @return the amount of charge in the bow
         */
        public float getCharge()
        {
            return event.getCharge();
        }

        /**
         * Event fired after an arrow has been selected and taken from a {@link IArrowContainer2}, before it is actually spawned
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
             * Sound used right before spawning the arrow entity
             */
            public SoundEvent bowSound = SoundEvents.ENTITY_ARROW_SHOOT;
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

            /**
             * Change the slot from which an arrow is pulled of the quiver
             */
            public void setQuiverSlotUsed(int slot){
                if(quiver.getItem() instanceof IArrowContainer2){
                    if(slot < ((IArrowContainer2) quiver.getItem()).getSlotCount(quiver)){
                        if(!((IArrowContainer2) quiver.getItem()).getStackInSlot(quiver, slot).isEmpty()) {
                            ((IArrowContainer2) quiver.getItem()).setSelectedSlot(quiver, slot);
                        }
                    }
                }
            }
        }

        /**
         * The DEFAULT result for this event is the vanilla charge calculated value
         * Use setNewCharge to override the value with the one provided
         * Change the event result to DENY to prevent further processing
         */
        @HasResult
        public static class ChargeCalculations extends QuiverArrowEvent {
            /**
             * Returned value if the result is set to allow
             */
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
                return ItemBow.getArrowVelocity((int)super.getCharge());
            }

            public void setNewCharge(float charge){
                this.setResult(Result.ALLOW);
                this.charge = charge;
            }
        }
    }
}
