package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.ListIterator;

import mods.battlegear2.api.core.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class NetClientHandlerTransformer extends TransformerBase {

    public NetClientHandlerTransformer() {
		super("net.minecraft.client.network.NetHandlerPlayClient");
	}

	private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;

    private String netClientHandlerHandleNamedEntitySpawnMethodName;
    private String netClientHandlerHandleNamedEntitySpawnMethodDesc;

    private String netClientHandlerHandleBlockItemSwitchMethodName;
    private String netClientHandlerHandleBlockItemSwitchMethodDesc;

    @Override
	boolean processMethods(List<MethodNode> methods) {
        int found = 0;
        for (MethodNode method : methods) {
            if (method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
                    method.desc.equals(netClientHandlerHandleNamedEntitySpawnMethodDesc)) {
                sendPatchLog("handleSpawnPlayer");

                replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
                found++;
            }else if (method.name.equals(netClientHandlerHandleBlockItemSwitchMethodName) &&
                    method.desc.equals(netClientHandlerHandleBlockItemSwitchMethodDesc)) {
                sendPatchLog("handleHeldItemChange");

                ListIterator<AbstractInsnNode> insn = method.instructions.iterator();
                InsnList newList = new InsnList();

                while(insn.hasNext()){

                    AbstractInsnNode nextNode = insn.next();

                    if(nextNode instanceof JumpInsnNode && nextNode.getOpcode() == IFLT){
                        LabelNode label = ((JumpInsnNode) nextNode).label;
                        newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/api/core/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));
                        newList.add(new JumpInsnNode(IFEQ, label));//"if equal" branch

                        nextNode = insn.next();
                        while(insn.hasNext() && !(nextNode instanceof JumpInsnNode) && nextNode.getOpcode() != IF_ICMPGE){
                            nextNode = insn.next();//continue till "if int greater than or equal to" branch
                        }

                    }else{
                        newList.add(nextNode);
                    }

                }

                method.instructions = newList;
                found++;
            }
        }
        return found == 2;
    }

	@Override
	boolean processFields(List<FieldNode> fields) {
		return true;
	}

	@Override
	void setupMappings() {

        entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("client.entity.EntityOtherPlayerMP");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by", "inventory");

        netClientHandlerHandleNamedEntitySpawnMethodName =
                BattlegearTranslator.getMapedMethodName("NetHandlerPlayClient", "func_147237_a", "handleSpawnPlayer");
        netClientHandlerHandleNamedEntitySpawnMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("NetHandlerPlayClient", "func_147237_a", "(Lnet/minecraft/network/play/server/S0CPacketSpawnPlayer;)V");

        netClientHandlerHandleBlockItemSwitchMethodName =
                BattlegearTranslator.getMapedMethodName("NetHandlerPlayClient", "func_147257_a", "handleHeldItemChange");
        netClientHandlerHandleBlockItemSwitchMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("NetHandlerPlayClient", "func_147257_a", "(Lnet/minecraft/network/play/server/S09PacketHeldItemChange;)V");
	}

}
