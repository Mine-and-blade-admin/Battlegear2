package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class NetServerHandlerTransformer extends TransformerMethodProcess {

    public NetServerHandlerTransformer() {
        super("net.minecraft.network.NetHandlerPlayServer", "func_147355_a", "processHeldItemChange", "(Lnet/minecraft/network/play/client/CPacketHeldItemChange;)V");
        setDebug(true);
    }

    private String packet16BlockItemSwitchClassName;
    private String getItemSwitchId;

    @Override
    void processMethod(MethodNode mn) {
        sendPatchLog("processHeldItemChange");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        boolean done = false;
        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();
            newList.add(nextInsn);
            if (!done && nextInsn instanceof MethodInsnNode &&
                    nextInsn.getOpcode() == INVOKEVIRTUAL &&
                    ((MethodInsnNode) nextInsn).owner.equals(packet16BlockItemSwitchClassName) &&
                    ((MethodInsnNode) nextInsn).name.equals(getItemSwitchId)) {

                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/api/core/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));

                while (it.hasNext()) {
                    if (nextInsn instanceof JumpInsnNode && nextInsn.getOpcode() == IF_ICMPGE) {//"if int greater than or equal to" branch
                        newList.add(new JumpInsnNode(IFEQ, ((JumpInsnNode) nextInsn).label));//make "if equal" branch
                        done = true;
                        break;
                    }
                    nextInsn = it.next();
                }
            }
        }

        mn.instructions = newList;
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        packet16BlockItemSwitchClassName = BattlegearTranslator.getMapedClassName("network.play.client.CPacketHeldItemChange");
        getItemSwitchId = BattlegearTranslator.getMapedMethodName("func_149614_c", "getSlotId");
    }
}
