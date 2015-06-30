package mods.battlegear2.api;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

/**
 * Created by Olivier on 04/06/2015.
 */
public final class DefaultMesh implements ItemMeshDefinition {
    /**
     * The usual mesh definition for items, with "inventory" as variant
     */
    public final static ItemMeshDefinition INVENTORY = new DefaultMesh("inventory");

    private final String var;

    /**
     * A mesh definition that use the ItemStack unlocalized name as path for the model (removing the usual prefix)
     * @param variant the variant to use when instanciating the ModelResourceLocation
     */
    public DefaultMesh(String variant){
        this.var = variant;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        return new ModelResourceLocation(stack.getUnlocalizedName().replace("item.", ""), var);
    }
}
