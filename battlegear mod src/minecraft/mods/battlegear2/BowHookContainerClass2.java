package mods.battlegear2;

import mods.battlegear2.api.IArrowContainer;
import mods.battlegear2.api.IArrowContainer2;
import mods.battlegear2.api.QuiverArrowEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import java.util.Random;

public class BowHookContainerClass2 {

    @ForgeSubscribe
    public void onBowUse(ArrowNockEvent event){
        if(!event.isCanceled()){
            if(event.entityPlayer.capabilities.isCreativeMode
                    || event.entityPlayer.inventory.hasItem(Item.arrow.itemID)){
                return;
            }
            ItemStack quiver = getArrowContainer(event.result,event.entityPlayer);
            if(quiver != null &&
                    ((IArrowContainer2)quiver.getItem()).
                            hasArrowFor(quiver, event.result, event.entityPlayer, ((IArrowContainer2) quiver.getItem()).getSelectedSlot(quiver))){

                event.entityPlayer.setItemInUse(event.result, event.result.getItem().getMaxItemUseDuration(event.result));
            }
        }
    }

    public static ItemStack getArrowContainer(ItemStack result, EntityPlayer entityPlayer) {
        //Check for IArrowContainer in player main inventory
        for(ItemStack item : entityPlayer.inventory.mainInventory){
            if(item!=null && item.getItem() instanceof IArrowContainer2){
                int maxSlot = ((IArrowContainer2) item.getItem()).getSlotCount(item);
                System.out.println("Quiver");
                for(int i = 0; i < maxSlot; i++){
                    if(((IArrowContainer2) item.getItem()).hasArrowFor(item, result, entityPlayer, i)){
                        return item;
                    }
                }

            }
        }
        return  null;
    }

    @ForgeSubscribe
    public void onBowFiring(ArrowLooseEvent event) {
        if(!event.isCanceled()){
            EntityPlayer player = event.entityPlayer;
            ItemStack bow = event.bow;
            //Don't need to do anything in those cases
            if(player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bow) > 0){
                return;
            }
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
            ItemStack stack = getArrowContainer(event.bow, event.entityPlayer);
            if(stack != null){
                IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
                World world = player.worldObj;
                EntityArrow entityarrow = quiver.getArrowType(stack, world, player, f*2.0F);
                if(entityarrow!=null)
                {
                    if (f == 1.0F)
                    {
                        entityarrow.setIsCritical(true);
                    }
                    QuiverArrowEvent arrowEvent = new QuiverArrowEvent(event);
                    quiver.onPreArrowFired(arrowEvent);
                    if(!arrowEvent.isCanceled())
                    {
                        if(arrowEvent.addEnchantments)
                        {
                            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bow);

                            if (k > 0)
                            {
                                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
                            }

                            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bow);

                            if (l > 0)
                            {
                                entityarrow.setKnockbackStrength(l);
                            }

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bow) > 0)
                            {
                                entityarrow.setFire(100);
                            }
                        }
                        if(arrowEvent.bowDamage>0)
                            bow.damageItem(arrowEvent.bowDamage, player);
                        if(arrowEvent.bowSoundVolume>0)
                            world.playSoundAtEntity(player, "random.bow", arrowEvent.bowSoundVolume, 1.0F / (new Random().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                        if (!world.isRemote)
                        {
                            world.spawnEntityInWorld(entityarrow);
                        }
                        quiver.onArrowFired(world, player, stack, bow, entityarrow);
                        //Canceling the event, since we successfully fired our own arrow
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
