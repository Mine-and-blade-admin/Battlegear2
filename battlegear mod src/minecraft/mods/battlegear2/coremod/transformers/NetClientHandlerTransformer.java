package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

public final class NetClientHandlerTransformer extends TransformerMethodProcess {

    public NetClientHandlerTransformer() {
        super("net.minecraft.client.network.NetHandlerPlayClient", "func_147257_a", "handleHeldItemChange", "(Lnet/minecraft/network/play/server/SPacketHeldItemChange;)V");
    }
    private String inventoryPlayerClass;

    @Override
    void processMethod(MethodNode method) {
        sendPatchLog("handleHeldItemChange");
        ListIterator<AbstractInsnNode> insn = method.instructions.iterator();

        while (insn.hasNext()) {
            AbstractInsnNode nextNode = insn.next();
            if (nextNode instanceof MethodInsnNode && nextNode.getOpcode() == INVOKESTATIC && ((MethodInsnNode) nextNode).owner.equals(inventoryPlayerClass)) {
                ((MethodInsnNode) nextNode).owner = "mods/battlegear2/api/core/InventoryPlayerBattle";
                ((MethodInsnNode) nextNode).name = "isValidSwitch";
                ((MethodInsnNode) nextNode).desc = "(I)Z";
                return;
            }
        }
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        inventoryPlayerClass = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
    }
}
