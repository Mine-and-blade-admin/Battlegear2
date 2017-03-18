package mods.battlegear2.api.quiver;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Collection of helpers for arrow container rendering
 */
public final class QuiverMesh implements ItemMeshDefinition {
    /**
     * Describe the conditions to render the model on player's back
     */
    public static final IItemPropertyGetter BACK_MODEL = new IItemPropertyGetter() {
        @Override
        public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if(stack.getItem() instanceof IArrowContainer2 && ((IArrowContainer2) stack.getItem()).renderDefaultQuiverModel(stack)){
                if(entityIn instanceof EntityPlayer && QuiverArrowRegistry.getArrowContainer((EntityPlayer) entityIn) == stack){
                    return 1;
                }
            }
            return 0;
        }
    };
    /**
     * Describe the conditions to render the model with arrows
     */
    public static final IItemPropertyGetter HAS_ARROW = new IItemPropertyGetter() {
        @Override
        public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (stack.getItem() instanceof IArrowContainer2) {
                IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
                int maxStack = quiver.getSlotCount(stack);
                for (int i = 0; i < maxStack; i++) {
                    if (!quiver.getStackInSlot(stack, i).isEmpty())
                        return 1;
                }
            }
            return 0;
        }
    };
    /**
     * Describe when a bow is used with the arrow container
     */
    public static final IItemPropertyGetter BOW_USE = new IItemPropertyGetter() {
        @Override
        public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (stack.getItem() instanceof IArrowContainer2 && entityIn != null && entityIn.isHandActive()) {
                EnumHand hand = EnumHand.values()[entityIn.getActiveHand().ordinal() + 1 % 2];
                if(entityIn.getHeldItem(hand) == stack){
                    return 1;
                }
            }
            return 0;
        }
    };
    private final ItemMeshDefinition empty;
    private final String suf;

    /**
     * A mesh definition to apply a different model on item instances of IArrowContainer2
     * @param suffix appended to the path when the arrow container is not empty
     * @param emptyQuiverMesh applied by default, or when the arrow container is empty
     */
    @Deprecated()
    public QuiverMesh(String suffix, ItemMeshDefinition emptyQuiverMesh){
        suf = suffix;
        empty = emptyQuiverMesh;
    }

    @Nonnull
    @Override
    public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
        ModelResourceLocation emptyQuiver = empty.getModelLocation(stack);
        if(HAS_ARROW.apply(stack, null, null) > 0){
            String variant = emptyQuiver.getVariant();
            String path = emptyQuiver.toString().replace("#"+variant, suf);
            return new ModelResourceLocation(path, variant);
        }
        return emptyQuiver;
    }
}
