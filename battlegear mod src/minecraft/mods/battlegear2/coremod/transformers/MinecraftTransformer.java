package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.MethodNode;

public final class MinecraftTransformer extends TransformerMethodProcess {

    public MinecraftTransformer() {
        super("net.minecraft.client.Minecraft", "func_147121_ag", new String[]{"rightClickMouse", SIMPLEST_METHOD_DESC});
        setDebug(true);
    }

    private String entityClientPlayerClass;
    private String playerInventoryFieldName;

    @Override
    void processMethod(MethodNode method) {
        sendPatchLog("Right Click Mouse");

        replaceInventoryArrayAccess(method, entityClientPlayerClass, playerInventoryFieldName, 5, 7, 6);
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        entityClientPlayerClass = BattlegearTranslator.getMapedClassName("client.entity.EntityPlayerSP");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");
    }
}
