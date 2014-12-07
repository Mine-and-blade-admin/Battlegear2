package mods.battlegear2;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.ISpecialBow;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

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
            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
            if(bow!=offhand)
                return isLoadedContainer(offhand, bow, player)?offhand:null;
            else{
                offhand = player.getCurrentEquippedItem();
                if(bow!=offhand)
                    return isLoadedContainer(offhand, bow, player)?offhand:null;
            }
            return null;
        }
    }

    //Check for IArrowContainer in player main inventory
    public static class MainQuiverSelection implements IQuiverSelection{

        @Override
        public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player) {
            ItemStack temp;
            for(int i=0;i<player.inventory.mainInventory.length; i++){
                temp = player.inventory.getStackInSlot(i);
                if(isLoadedContainer(temp, bow, player))
                    return temp;
            }
            return null;
        }
    }

	@SubscribeEvent
	public void onBowUse(ArrowNockEvent event){
		// change to use Result: DENY (cannot fire), DEFAULT (attempt standard nocking algorithm), ALLOW (nock without further checks)
		Result canDrawBow = Result.DEFAULT;
		// insert special bow check here:
		if (event.result.getItem() instanceof ISpecialBow) {
			canDrawBow = ((ISpecialBow) event.result.getItem()).canDrawBow(event.result, event.entityPlayer);
		}
		// Special bow did not determine a result, so use standard algorithms instead:
		if (canDrawBow == Result.DEFAULT && (event.entityPlayer.capabilities.isCreativeMode
				|| event.entityPlayer.inventory.hasItem(Items.arrow))) {
			canDrawBow = Result.ALLOW;
		}
		if (canDrawBow == Result.DEFAULT) {
			ItemStack quiver = QuiverArrowRegistry.getArrowContainer(event.result, event.entityPlayer);
			if (quiver != null && ((IArrowContainer2)quiver.getItem()).
				hasArrowFor(quiver, event.result, event.entityPlayer, ((IArrowContainer2) quiver.getItem()).getSelectedSlot(quiver))) {
				canDrawBow = Result.ALLOW;
			}
		}
		// only nock if allowed
		if (canDrawBow == Result.ALLOW) {
            int usage = mods.battlegear2.api.EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowCharge,event.result)*20000;
			event.entityPlayer.setItemInUse(event.result, event.result.getMaxItemUseDuration()-usage);
			event.setCanceled(true);
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
    	if(item!=null && item.getItem() instanceof IArrowContainer2){
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
            ItemStack stack = QuiverArrowRegistry.getArrowContainer(event.bow, event.entityPlayer);
            if(stack != null){
                IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
                World world = event.entityPlayer.worldObj;
                EntityArrow entityarrow = quiver.getArrowType(stack, world, event.entityPlayer, f*2.0F);
                if(entityarrow!=null){
                    PlayerEventChild.QuiverArrowEvent.Firing arrowEvent = new PlayerEventChild.QuiverArrowEvent.Firing(event, stack, entityarrow);
                    quiver.onPreArrowFired(arrowEvent);
                    if(!MinecraftForge.EVENT_BUS.post(arrowEvent)){
                        if (arrowEvent.isCritical || f == 1.0F)
                            entityarrow.setIsCritical(true);
                        if(arrowEvent.addEnchantments){
                            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, event.bow);
                            if (k > 0){
                                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
                            }
                            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, event.bow);
                            if (l > 0){
                                entityarrow.setKnockbackStrength(l);
                            }
                            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, event.bow) > 0){
                                entityarrow.setFire(100);
                            }
                        }
                        if(arrowEvent.bowDamage>0)
                            event.bow.damageItem(arrowEvent.bowDamage, event.entityPlayer);
                        if(arrowEvent.bowSoundVolume>0)
                            world.playSoundAtEntity(arrowEvent.getPlayer(), arrowEvent.bowSound, arrowEvent.bowSoundVolume, 1.0F / (arrowEvent.getPlayer().getRNG().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                        if (!world.isRemote)
                            world.spawnEntityInWorld(entityarrow);
                        quiver.onArrowFired(world, arrowEvent.getPlayer(), stack, event.bow, entityarrow);
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
        if(event.source.isProjectile() && event.source.getSourceOfDamage() instanceof AbstractMBArrow){
            boolean isCanceled = ((AbstractMBArrow) event.source.getSourceOfDamage()).onHitEntity(event.entity, event.source, event.ammount);
            event.setCanceled(isCanceled);
        }
    }
}
