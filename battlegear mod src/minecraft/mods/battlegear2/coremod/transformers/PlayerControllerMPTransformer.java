package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.MethodNode;

public class PlayerControllerMPTransformer extends TransformerMethodProcess {

    public PlayerControllerMPTransformer() {
		super("net.minecraft.client.multiplayer.PlayerControllerMP", "func_78769_a", new String[]{"sendUseItem", "(Lnet/minecraft/entity/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"});
	}

	private String entityPlayerClassName;
    private String playerInventoryFieldName;

    @Override
	void processMethod(MethodNode method) {
        sendPatchLog("sendUseItem");
        replaceInventoryArrayAccess(method, entityPlayerClassName, playerInventoryFieldName, 9, 13);
    }

	@Override
	void setupMappings() {
		super.setupMappings();
		entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by", "inventory");
	}

}
