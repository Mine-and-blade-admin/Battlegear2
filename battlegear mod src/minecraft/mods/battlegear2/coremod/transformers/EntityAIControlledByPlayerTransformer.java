package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.MethodNode;

public final class EntityAIControlledByPlayerTransformer extends TransformerMethodProcess {

    public EntityAIControlledByPlayerTransformer() {
        super("net.minecraft.entity.ai.EntityAIControlledByPlayer", "func_75246_d", new String[]{"updateTask", SIMPLEST_METHOD_DESC});
    }

    private String entityPlayerClassName;
    private String playerInventoryFieldName;

    @Override
    void processMethod(MethodNode method) {
        sendPatchLog("updateTask");
        replaceInventoryArrayAccess(method, entityPlayerClassName, playerInventoryFieldName, 12, 24);
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");
    }
}
