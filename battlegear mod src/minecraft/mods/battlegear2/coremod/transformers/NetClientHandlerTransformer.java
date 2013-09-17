package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class NetClientHandlerTransformer implements IClassTransformer {

    private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;

    private String packet20NamedEntitySpawnClassName;

    private String netClientHandlerHandleNamedEntitySpawnMethodName;
    private String netClientHandlerHandleNamedEntitySpawnMethodDesc;

    private String netClientHandlerHandleBlockItemSwitchMethodName;
    private String netClientHandlerHandleBlockItemSwitchMethodDesc;

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.multiplayer.NetClientHandler")) {

            entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityOtherPlayerMP");
            packet20NamedEntitySpawnClassName = BattlegearTranslator.getMapedClassName("Packet20NamedEntitySpawn");
            playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");

            netClientHandlerHandleNamedEntitySpawnMethodName =
                    BattlegearTranslator.getMapedMethodName("NetClientHandler", "func_72518_a");
            netClientHandlerHandleNamedEntitySpawnMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("NetClientHandler", "func_72518_a");

            netClientHandlerHandleNamedEntitySpawnMethodName =
                    BattlegearTranslator.getMapedMethodName("NetClientHandler", "func_72502_a");
            netClientHandlerHandleNamedEntitySpawnMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("NetClientHandler", "func_72502_a");

            System.out.println("M&B - Patching Class NetClientHandler (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            for (Object mnObj : cn.methods) {
                MethodNode method = (MethodNode)mnObj;
                if (method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
                        method.desc.equals(netClientHandlerHandleNamedEntitySpawnMethodDesc)) {
                    System.out.println("\tPatching method handleNamedEntitySpawn in NetClientHandler");

                    TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
                }else if (method.name.equals(netClientHandlerHandleBlockItemSwitchMethodName) &&
                        method.desc.equals(netClientHandlerHandleBlockItemSwitchMethodDesc)) {
                    System.out.println("\tPatching method handleBlockItemSwitch in NetClientHandler");

                    ListIterator<AbstractInsnNode> insn = method.instructions.iterator();
                    InsnList newList = new InsnList();

                    while(insn.hasNext()){

                        AbstractInsnNode nextNode = insn.next();

                        if(nextNode instanceof JumpInsnNode && nextNode.getOpcode() == IFLT){
                            LabelNode label = ((JumpInsnNode) nextNode).label;
                            newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));
                            newList.add(new JumpInsnNode(IFEQ, label));

                            nextNode = insn.next();
                            while(insn.hasNext() && !(nextNode instanceof JumpInsnNode) && nextNode.getOpcode() != IF_ICMPGE){
                                nextNode = insn.next();
                            }

                        }else{
                            newList.add(nextNode);
                        }

                    }

                    method.instructions = newList;

                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            System.out.println("M&B - Patching Class NetClientHandler (" + name + ") done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }

            return cw.toByteArray();

        } else {
            return bytes;
        }

    }

}
