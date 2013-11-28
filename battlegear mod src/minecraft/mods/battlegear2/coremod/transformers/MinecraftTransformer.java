package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.MethodNode;

public class MinecraftTransformer extends TransformerMethodProcess {

	public MinecraftTransformer(){
		super("net.minecraft.client.Minecraft", "func_71402_c");
	}
	
    private String entityClientPlayerClass;

    private String playerInventoryFieldName;

	@Override
	void processMethod(MethodNode method) {
        sendPatchLog("Click Mouse");

        replaceInventoryArrayAccess(method, entityClientPlayerClass, playerInventoryFieldName, 5, 9, 10);
	}

	@Override
	void setupMappings() {
		super.setupMappings();
		entityClientPlayerClass = BattlegearTranslator.getMapedClassName("EntityClientPlayerMP");

        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");
	}

}
