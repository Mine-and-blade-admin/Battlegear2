package mods.battlegear2.items;

import mods.battlegear2.api.*;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.weapons.WeaponRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(modid = "api_test", name = "API Items")
@Mod.EventBusSubscriber(modid = "api_test")
public class APIItemsTest {
    private static Logger log;
    @Mod.EventHandler
    public void loading(FMLPreInitializationEvent event){
        log = event.getModLog();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> itemRegistry){
        itemRegistry.getRegistry().register(register(new Sheathed(true), "backSheathed"));
        itemRegistry.getRegistry().register(register(new Wielded(true), "wielded"));
        itemRegistry.getRegistry().register(register(new Wielded(false), "notWielded"));
        itemRegistry.getRegistry().register(register(new Allow(true), "allowCombo"));
        itemRegistry.getRegistry().register(register(new Allow(false), "stopCombo"));
        itemRegistry.getRegistry().register(register(new Usable(true), "usable"));
        itemRegistry.getRegistry().register(register(new Usable(false), "unUsable"));
    }

    private static Item register(Item item, String name){
        ItemStack temp = new ItemStack(item.setUnlocalizedName(name).setRegistryName(name));
        log.info("Registered:" + temp);
        if(BattlegearUtils.checkForRightClickFunction(temp))
            log.info("Detected use item method override");
        try {
            if(BattlegearUtils.isMainHand(temp, null, null)) {
                boolean use = BattlegearUtils.usagePriorAttack(temp, null, false);
                log.info("Will " + (use ? "use" : "attack") + " in main hand");
            }
        } catch (Exception e) {
        }
        try {
            if(BattlegearUtils.isOffHand(temp, null, null)) {
                boolean use = BattlegearUtils.usagePriorAttack(temp, null, true);
                log.info("Will " + (use ? "use" : "attack") + " in off hand");
            }
        } catch (Exception e) {
        }
        return item;
    }

    private static class ItemListener extends Item{
        /////Checked by reflection
        /*
        @Override
        public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            System.out.println("Item Use " + playerIn.getHeldItem(hand));
            return EnumActionResult.PASS;
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
        {
            System.out.println("Item Right Click "+ playerIn.getHeldItem(hand));
            return super.onItemRightClick(worldIn, playerIn, hand);
        }

        @Override
        public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
        {
            System.out.println("Item First Use "+ player.getHeldItem(hand));
            return EnumActionResult.PASS;
        }
        */

        @Nonnull
        @Override
        public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase playerIn)
        {
            log.info("Item Use Finish "+ stack);
            return stack;
        }

        @Override
        public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
        {
            log.info("Item Use " + count + " Ticks " + stack);
        }

        @Override
        public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
        {
            log.info("Click Entity " + stack);
            return false;
        }
    }

    private static class Sheathed extends ItemListener implements ISheathed{
        private final boolean isOnBack;
        Sheathed(boolean back){
            isOnBack = back;
        }

        @Override
        public boolean sheatheOnBack(ItemStack item) {
            return isOnBack;
        }
    }

    private static class Wielded extends ItemListener implements IWield {
        private final boolean isOffhand;
        Wielded(boolean off){
            isOffhand = off;
        }

        @Override
        public WeaponRegistry.Wield getWieldStyle(ItemStack itemStack, EntityPlayer wielder) {
            if(wielder!=null)
                log.info("Holding " + wielder.getHeldItemMainhand() + wielder.getHeldItemOffhand());
            return isOffhand ? WeaponRegistry.Wield.LEFT : WeaponRegistry.Wield.RIGHT;
        }
    }

    private static class Allow extends ItemListener implements IAllowItem{
        private final boolean allowAll;
        Allow(boolean allow){
            allowAll = allow;
        }

        @Override
        public boolean allowOffhand(ItemStack mainhand, ItemStack offhand, EntityPlayer player) {
            log.info((allowAll ? "Allowed" : "Stopped") + " combination of " + mainhand + " and " + offhand);
            return allowAll;
        }
    }

    private static class Usable extends ItemListener implements IUsableItem{
        private final boolean isUsed;
        Usable(boolean used){
            isUsed = used;
        }

        @Override
        public boolean isUsedOverAttack(ItemStack itemStack, EntityPlayer player) {
            if(isUsed)
                log.info("Used over attack " + itemStack);
            else
                log.info("Attack over used " + itemStack);
            return isUsed;
        }
    }
}
