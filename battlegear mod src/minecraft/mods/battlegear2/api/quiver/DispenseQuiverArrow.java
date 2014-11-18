package mods.battlegear2.api.quiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

/**
 * Created by GotoLink on 08/11/2014.
 * Dispense arrows from {@link IArrowContainer2}
 * Needs to be added to {@link BlockDispenser#dispenseBehaviorRegistry} with the corresponding arrow container item
 */
public class DispenseQuiverArrow extends DispenseArrow{
    /**
     * The item that will attempt to use the arrow container, by default
     * @see #getUsedBow(ItemStack)
     */
    private final Item bow;
    /**
     * The charge value used when instantiating the arrow from {@link IArrowContainer2#getArrowType(ItemStack, World, EntityPlayer, float)}
     */
    private final float charge;

    public DispenseQuiverArrow(Item bow, float charge){
        this.bow = bow;
        this.charge = charge;
    }

    @Override
    protected EntityArrow getArrowEntity(World world, ItemStack itemStack) {
        if(itemStack.getItem() instanceof IArrowContainer2){
            ItemStack bowStack = getUsedBow(itemStack);
            EntityPlayer fake = getUsingPlayer(world);
            if(((IArrowContainer2) itemStack.getItem()).hasArrowFor(itemStack, bowStack, fake, ((IArrowContainer2) itemStack.getItem()).getSelectedSlot(itemStack))) {
                EntityArrow arrow = ((IArrowContainer2) itemStack.getItem()).getArrowType(itemStack, world, fake, charge);
                if (arrow != null) {
                    ((IArrowContainer2) itemStack.getItem()).onArrowFired(world, fake, itemStack, bowStack, arrow);
                }
                return arrow;
            }
        }
        return null;
    }

    /**
     * Simulates a stack containing a bow compatible with the arrow container.
     * Necessary to make a fake, since dispenser doesn't keep the bow data it was crafted with.
     *
     * @param itemStack inside the dispenser, containing the arrow
     * @return a "fake" bow to fire the arrow with
     */
    protected ItemStack getUsedBow(ItemStack itemStack){
        return bow!=null ? new ItemStack(bow) : null;
    }

    /**
     * Simulates a player using a bow to fire the arrow from the container
     * Necessary to build a fake, since redstone doesn't identify source of change
     *
     * @param world where the dispenser is located
     * @return a "fake" player
     */
    protected EntityPlayer getUsingPlayer(World world){
        return world instanceof WorldServer ? FakePlayerFactory.getMinecraft((WorldServer)world) : null;
    }

    /**
     * Prevent container stack size from being reduced
     * @param itemStack inside the dispenser, to consume from
     */
    @Override
    protected void consume(ItemStack itemStack){
    }
}
