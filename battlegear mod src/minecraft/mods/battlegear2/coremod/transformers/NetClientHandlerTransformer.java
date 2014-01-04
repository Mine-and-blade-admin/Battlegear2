package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.ListIterator;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class NetClientHandlerTransformer extends TransformerBase {

    public NetClientHandlerTransformer() {
		super("net.minecraft.client.multiplayer.NetClientHandler");
	}

	private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;

    private String packet20NamedEntitySpawnClassName;

    private String netClientHandlerHandleNamedEntitySpawnMethodName;
    private String netClientHandlerHandleNamedEntitySpawnMethodDesc;

    private String netClientHandlerHandleBlockItemSwitchMethodName;
    private String netClientHandlerHandleBlockItemSwitchMethodDesc;

    @Override
	void processMethods(List<MethodNode> methods) {
        for (MethodNode method : methods) {
            if (method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
                    method.desc.equals(netClientHandlerHandleNamedEntitySpawnMethodDesc)) {
                sendPatchLog("handleNamedEntitySpawn");

                replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
            }else if (method.name.equals(netClientHandlerHandleBlockItemSwitchMethodName) &&
                    method.desc.equals(netClientHandlerHandleBlockItemSwitchMethodDesc)) {
                sendPatchLog("handleBlockItemSwitch");

                ListIterator<AbstractInsnNode> insn = method.instructions.iterator();
                InsnList newList = new InsnList();

                while(insn.hasNext()){

                    AbstractInsnNode nextNode = insn.next();

                    if(nextNode instanceof JumpInsnNode && nextNode.getOpcode() == IFLT){
                        LabelNode label = ((JumpInsnNode) nextNode).label;
                        newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));
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

            }
        }
    }

	@Override
	void processFields(List<FieldNode> fields) {
		
	}

	@Override
	void setupMappings() {

        entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("client.entity.EntityOtherPlayerMP");
        packet20NamedEntitySpawnClassName = BattlegearTranslator.getMapedClassName("network.packet.Packet20NamedEntitySpawn");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by", "inventory");

        netClientHandlerHandleNamedEntitySpawnMethodName =
                BattlegearTranslator.getMapedMethodName("NetClientHandler", "func_72518_a", "handleNamedEntitySpawn");
        netClientHandlerHandleNamedEntitySpawnMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("NetClientHandler", "func_72518_a", "(L"+packet20NamedEntitySpawnClassName+";)V");

        netClientHandlerHandleBlockItemSwitchMethodName =
                BattlegearTranslator.getMapedMethodName("NetClientHandler", "func_72502_a", "handleBlockItemSwitch");
        netClientHandlerHandleBlockItemSwitchMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("NetClientHandler", "func_72502_a", "(Lnet/minecraft/network/packet/Packet16BlockItemSwitch;)V");
	}

}
