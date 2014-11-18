package mods.battlegear2.api.quiver;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by GotoLink on 07/11/2014.
 * A generic dispensing arrow behavior for {@link BlockDispenser]
 */
public abstract class DispenseArrow extends BehaviorDefaultDispenseItem{

    /**
     * Spawn the new EntityArrow instance, or default to dispensing the item form.
     */
    @Override
    public final ItemStack dispenseStack(IBlockSource source, ItemStack itemStack) {
        EntityArrow arrow = this.getArrowEntity(source.getWorld(), itemStack);
        if(arrow != null) {
            IPosition iPosition = BlockDispenser.func_149939_a(source);
            EnumFacing enumfacing = BlockDispenser.func_149937_b(source.getBlockMetadata());
            this.setArrowProperties(arrow, iPosition, enumfacing);
            if(source.getWorld().spawnEntityInWorld(arrow))
                this.consume(itemStack);
            return itemStack;
        }
        return super.dispenseStack(source, itemStack);
    }

    /**
     * Instantiate the arrow.
     *
     * @param world where the dispenser is, and inside which the arrow will spawn
     * @param itemStack that the dispenser selected
     * @return null to default to the item dispensing
     */
    protected abstract EntityArrow getArrowEntity(World world, ItemStack itemStack);

    /**
     * Set the arrow properties after instantiation, but before spawn.
     * Default to setting position, direction, and the pick up flag.
     *
     * @param arrow that was instantiated
     * @param iPosition the dispenser position
     * @param enumfacing the dispenser face
     */
    protected void setArrowProperties(EntityArrow arrow, IPosition iPosition, EnumFacing enumfacing){
        arrow.setPosition(iPosition.getX(), iPosition.getY(), iPosition.getZ());
        arrow.yOffset = 0.0F;
        arrow.canBePickedUp = 1;
        arrow.setThrowableHeading((double) enumfacing.getFrontOffsetX(), (double) ((float) enumfacing.getFrontOffsetY() + 0.1F), (double) enumfacing.getFrontOffsetZ(), 1.1F, 6.0F);
    }

    /**
     * After the arrow has been successfully spawned.
     * Default to making a copy of the given stack with one size less.
     *
     * @param itemStack inside the dispenser, to consume from
     */
    protected void consume(ItemStack itemStack) {
        itemStack.splitStack(1);
    }
}
