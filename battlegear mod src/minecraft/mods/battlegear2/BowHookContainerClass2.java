package mods.battlegear2;

import java.util.Random;

import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.IQuiverSelection;
import mods.battlegear2.api.quiver.QuiverArrowEvent;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

public class BowHookContainerClass2 {
    public BowHookContainerClass2(){
        QuiverArrowRegistry.addQuiverSelection(new OffhandQuiverSelection());
        QuiverArrowRegistry.addQuiverSelection(new MainQuiverSelection());
    }

    //Check for IArrowContainer in player offhand
    public static class OffhandQuiverSelection implements IQuiverSelection{

        @Override
        public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player) {
            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
            return isLoadedContainer(offhand, bow, player)?offhand:null;
        }
    }

    //Check for IArrowContainer in player main inventory
    public static class MainQuiverSelection implements IQuiverSelection{

        @Override
        public ItemStack getQuiverFor(ItemStack bow, EntityPlayer player) {
            ItemStack temp;
            for(int i=0;i<player.inventory.getSizeInventory(); i++){
                temp = player.inventory.getStackInSlot(i);
                if(temp==null){
                    continue;
                }
                if(isLoadedContainer(temp, bow, player))
                    return temp;
            }
            return null;
        }
    }

    @ForgeSubscribe(receiveCanceled=true)
    public void onBowUse(ArrowNockEvent event){
    	boolean canDrawBow = false;
        if(event.entityPlayer.capabilities.isCreativeMode
                || event.entityPlayer.inventory.hasItem(Item.arrow.itemID)){
        	canDrawBow = true;
        }
        if(!canDrawBow){
        	ItemStack quiver = QuiverArrowRegistry.getArrowContainer(event.result, event.entityPlayer);
	        if(quiver != null &&
	                ((IArrowContainer2)quiver.getItem()).
	                        hasArrowFor(quiver, event.result, event.entityPlayer, ((IArrowContainer2) quiver.getItem()).getSelectedSlot(quiver))){
	        	canDrawBow = true;
	        }
        }
	    if(canDrawBow){
            event.entityPlayer.setItemInUse(event.result, event.result.getItem().getMaxItemUseDuration(event.result)-EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowCharge.effectId,event.result)*20000);
            event.setCanceled(true);
	    }
    }

    /**
     * Convenience feature for "AnonymousProductions" dude
     * We have yet to decide when to perform this
     * @param bow
     * @param loadedArrow
     */
    public static void writeBowNBT(ItemStack bow, ItemStack loadedArrow) {
        NBTTagCompound tags = new NBTTagCompound();
        loadedArrow.writeToNBT(tags);
        if(!bow.hasTagCompound()){
            bow.stackTagCompound = new NBTTagCompound();
        }
        bow.stackTagCompound.setCompoundTag("Battlegear2-LoadedArrow", tags);
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

    @ForgeSubscribe(receiveCanceled=true)
    public void onBowFiring(ArrowLooseEvent event) {
        //Check if bow is charged enough
        int j = event.charge;
        float f = (float)j / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if ((double)f < 0.1D)
        {
            return;
        }
        if (f > 1.0F)
        {
            f = 1.0F;
        }
        ItemStack stack = QuiverArrowRegistry.getArrowContainer(event.bow, event.entityPlayer);
        if(stack != null){
            IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
            World world = event.entityPlayer.worldObj;
            EntityArrow entityarrow = quiver.getArrowType(stack, world, event.entityPlayer, f*2.0F);
            if(entityarrow!=null){
                if (f == 1.0F)
                    entityarrow.setIsCritical(true);
                QuiverArrowEvent arrowEvent = new QuiverArrowEvent(event, stack, entityarrow);
                quiver.onPreArrowFired(arrowEvent);
                if(!MinecraftForge.EVENT_BUS.post(arrowEvent)){
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
                        world.playSoundAtEntity(event.entityPlayer, "random.bow", arrowEvent.bowSoundVolume, 1.0F / (new Random().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!world.isRemote)
                        world.spawnEntityInWorld(entityarrow);
                    quiver.onArrowFired(world, event.entityPlayer, stack, event.bow, entityarrow);
                    //Canceling the event, since we successfully fired our own arrow
                    event.setCanceled(true);
                }
            }
        }
    }

    //Start hooks for arrows
    @ForgeSubscribe
    public void onEntityHitByArrow(LivingAttackEvent event){
        if(event.source.isProjectile() && event.source.getSourceOfDamage() instanceof AbstractMBArrow){
            boolean isCanceled = ((AbstractMBArrow) event.source.getSourceOfDamage()).onHitEntity(event.entity, event.source, event.ammount);
            event.setCanceled(isCanceled);
        }
    }
}
