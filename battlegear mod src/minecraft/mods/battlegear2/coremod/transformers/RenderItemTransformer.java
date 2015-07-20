package mods.battlegear2.coremod.transformers;

import org.objectweb.asm.tree.MethodNode;

/**
 * Created by Olivier on 19/07/2015.
 */
public class RenderItemTransformer extends TransformerMethodProcess{

    public RenderItemTransformer() {
        super("net.minecraft.client.renderer.entity.RenderItem", "func_", new String[]{"renderItemModelForEntity", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"});
    }

    @Override
    void processMethod(MethodNode method) {

    }


    @Override
    void setupMappings() {
        super.setupMappings();

    }
}
