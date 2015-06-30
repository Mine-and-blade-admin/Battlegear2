package mods.battlegear2.api.quiver;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

/**
 * Created by Olivier on 04/06/2015.
 */
public final class QuiverMesh implements ItemMeshDefinition {
    private final ItemMeshDefinition empty;
    private final String suf;

    /**
     * A mesh definition to apply a different model on item instances of IArrowContainer2
     * @param suffix appended to the path when the arrow container is not empty
     * @param emptyQuiverMesh applied by default, or when the arrow container is empty
     */
    public QuiverMesh(String suffix, ItemMeshDefinition emptyQuiverMesh){
        suf = suffix;
        empty = emptyQuiverMesh;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        ModelResourceLocation emptyQuiver = empty.getModelLocation(stack);
        if(stack.getItem() instanceof IArrowContainer2) {
            IArrowContainer2 quiver = (IArrowContainer2) stack.getItem();
            boolean hasArrows = false;
            int maxStack = quiver.getSlotCount(stack);
            for (int i = 0; i < maxStack && !hasArrows; i++) {
                hasArrows = quiver.getStackInSlot(stack, i) != null;
            }
            if(hasArrows){
                String variant = emptyQuiver.getVariant();
                String path = emptyQuiver.toString().replace("#"+variant, suf);
                return new ModelResourceLocation(path, variant);
            }
        }
        return emptyQuiver;
    }
}
