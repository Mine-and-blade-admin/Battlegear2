package mods.battlegear2.items;

import mods.battlegear2.api.IAllowItem;
import mods.battlegear2.api.IOffhandWield;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.IUsableItem;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = "APITest", name = "API Items")
public class APIItemsTest {
    private Logger log;
    @Mod.EventHandler
    public void loading(FMLPreInitializationEvent event){
        log = event.getModLog();
        register(new Sheathed(true), "backSheathed");
        register(new Sheathed(false), "hipSheathed");
        register(new Wielded(true), "wielded");
        register(new Wielded(false), "notWielded");
        register(new Allow(true), "allowCombo");
        register(new Allow(false), "stopCombo");
        register(new Usable(true), "usable");
        register(new Usable(false), "unUsable");
    }

    private void register(Item item, String name){
        ItemStack temp = new ItemStack(GameRegistry.registerItem(item.setUnlocalizedName(name), name, null));
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
    }

    private class ItemListener extends Item{
        /////Checked by reflection
        /*
        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            System.out.println("Item Use " + stack);
            return false;
        }

        @Override
        public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn)
        {
            System.out.println("Item Right Click "+ stack);
            return stack;
        }

        @Override
        public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            System.out.println("Item First Use "+ stack);
            return false;
        }
        /////////*/

        @Override
        public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
        {
            log.info("Item Use Finish "+ stack);
            return stack;
        }

        @Override
        public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
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

    private class Sheathed extends ItemListener implements ISheathed{
        private final boolean isOnBack;
        Sheathed(boolean back){
            isOnBack = back;
        }

        @Override
        public boolean sheatheOnBack(ItemStack item) {
            return isOnBack;
        }
    }

    private class Wielded extends ItemListener implements IOffhandWield{
        private final boolean isOffhand;
        Wielded(boolean off){
            isOffhand = off;
        }

        @Override
        public boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder) {
            if(wielder!=null)
                log.info("Holding " + wielder.getCurrentEquippedItem());
            return isOffhand;
        }
    }

    private class Allow extends ItemListener implements IAllowItem{
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

    private class Usable extends ItemListener implements IUsableItem{
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
