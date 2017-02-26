package mods.battlegear2;

import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.ISpecialBow;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class BowHookContainerClass2 {

    public static final BowHookContainerClass2 INSTANCE = new BowHookContainerClass2();

    private BowHookContainerClass2(){
        QuiverArrowRegistry.addQuiverSelection(new OffhandQuiverSelection());
        QuiverArrowRegistry.addQuiverSelection(new MainQuiverSelection());
    }

    //Check for IArrowContainer in player opposite hand
    public static class OffhandQuiverSelection implements IQuiverSelection{

        @Override
        public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player) {
            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOppositeHand();
            if(bow!=offhand)
                return isLoadedContainer(offhand, bow, player)?offhand:ItemStack.EMPTY;
            else{
                offhand = player.getHeldItemMainhand();
                if(bow!=offhand)
                    return isLoadedContainer(offhand, bow, player)?offhand:ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
    }

    //Check for IArrowContainer in player main inventory
    public static class MainQuiverSelection implements IQuiverSelection{

        @Override
        public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player) {
            ItemStack temp;
            for(int i=0;i<player.inventory.mainInventory.size(); i++){
                temp = player.inventory.getStackInSlot(i);
                if(isLoadedContainer(temp, bow, player))
                    return temp;
            }
            return ItemStack.EMPTY;
        }
    }

	@SubscribeEvent
	public void onBowUse(ArrowNockEvent event){
		// change to use Result: DENY (cannot fire), DEFAULT (attempt standard nocking algorithm), ALLOW (nock without further checks)
		Result canDrawBow = Result.DEFAULT;
		// insert special bow check here:
		if (event.getBow().getItem() instanceof ISpecialBow) {
			canDrawBow = ((ISpecialBow) event.getBow().getItem()).canDrawBow(event.getBow(), event.getEntityPlayer());
		}
		// Special bow did not determine a result, so use standard algorithms instead:
		if (canDrawBow == Result.DEFAULT && (event.hasAmmo() || event.getEntityPlayer().capabilities.isCreativeMode)) {
			canDrawBow = Result.ALLOW;
		}
		if (canDrawBow == Result.DEFAULT) {
			ItemStack quiver = QuiverArrowRegistry.getArrowContainer(event.getBow(), event.getEntityPlayer());
			if (!quiver.isEmpty() && ((IArrowContainer2)quiver.getItem()).
				hasArrowFor(quiver, event.getBow(), event.getEntityPlayer(), ((IArrowContainer2) quiver.getItem()).getSelectedSlot(quiver))) {
				canDrawBow = Result.ALLOW;
			}
		}
		// only nock if allowed
		if (canDrawBow == Result.ALLOW) {
			event.getEntityPlayer().setActiveHand(event.getHand());
			event.setAction(ActionResult.newResult(EnumActionResult.SUCCESS, event.getBow()));
		}
	}

    @SubscribeEvent
    public void onBowStartDraw(LivingEntityUseItemEvent.Start use){
        if(use.getDuration() > 1 && BattlegearUtils.isBow(use.getItem().getItem())){
            int lvl = mods.battlegear2.api.EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowCharge, use.getItem());
            if(lvl > 0){
                use.setDuration(use.getDuration()- lvl*20000);
                if(use.getDuration() <=0){
                    use.setDuration(1);
                }
            }
        }
    }

    /**
     *
     * @param item the item to check
     * @param bow the bow trying to fire an arrow
     * @param entityPlayer the player trying to fire an arrow
     * @return true if the item can give an arrow
     */
    public static boolean isLoadedContainer(ItemStack item, ItemStack bow, EntityPlayer entityPlayer){
    	if(item.getItem() instanceof IArrowContainer2){
            int maxSlot = ((IArrowContainer2) item.getItem()).getSlotCount(item);
            for(int i = 0; i < maxSlot; i++){
                if(((IArrowContainer2) item.getItem()).hasArrowFor(item, bow, entityPlayer, i)){
                    return true;
                }
            }
        }
    	return false;
    }

    @SubscribeEvent
    public void onBowFiring(ArrowLooseEvent event) {
        //Check if bow is charged enough
        float f = new PlayerEventChild.QuiverArrowEvent.ChargeCalculations(event).getCharge();
        if(f>0){
            ItemStack stack = QuiverArrowRegistry.getArrowContainer(event.getBow(), event.getEntityPlayer());
            if(stack.getItem() instanceof IArrowContainer2){
                IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
                World world = event.getEntityPlayer().world;
                EntityArrow entityarrow = quiver.getArrowType(stack, world, event.getEntityPlayer(), f*3.0F);
                if(entityarrow!=null){
                    PlayerEventChild.QuiverArrowEvent.Firing arrowEvent = new PlayerEventChild.QuiverArrowEvent.Firing(event, stack, entityarrow);
                    quiver.onPreArrowFired(arrowEvent);
                    entityarrow.setAim(arrowEvent.getArcher(), arrowEvent.getArcher().rotationPitch, arrowEvent.getArcher().rotationYaw, 0, f*3F, 1F);
                    if(!MinecraftForge.EVENT_BUS.post(arrowEvent)){
                        if (arrowEvent.isCritical || f == 1.0F)
                            entityarrow.setIsCritical(true);
                        if(arrowEvent.addEnchantments){
                            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, event.getBow());
                            if (k > 0){
                                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
                            }
                            int l = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, event.getBow());
                            if (l > 0){
                                entityarrow.setKnockbackStrength(l);
                            }
                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, event.getBow()) > 0){
                                entityarrow.setFire(100);
                            }
                        }
                        if(arrowEvent.bowDamage>0)
                            event.getBow().damageItem(arrowEvent.bowDamage, event.getEntityPlayer());
                        if(arrowEvent.bowSoundVolume>0)
                            world.playSound(null, arrowEvent.getArcher().posX, arrowEvent.getArcher().posY, arrowEvent.getArcher().posZ, arrowEvent.bowSound, SoundCategory.PLAYERS, arrowEvent.bowSoundVolume, 1.0F / (arrowEvent.getPlayer().getRNG().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                        if (!world.isRemote)
                            world.spawnEntity(entityarrow);
                        quiver.onArrowFired(world, arrowEvent.getPlayer(), stack, event.getBow(), entityarrow);
                        //Canceling the event, since we successfully fired our own arrow
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    //Start hooks for arrows
    @SubscribeEvent
    public void onEntityHitByArrow(LivingAttackEvent event){
        if(event.getSource().isProjectile() && event.getSource().getSourceOfDamage() instanceof AbstractMBArrow){
            boolean isCanceled = ((AbstractMBArrow) event.getSource().getSourceOfDamage()).onHitEntity(event.getEntity(), event.getSource(), event.getAmount());
            event.setCanceled(isCanceled);
        }
    }
}
