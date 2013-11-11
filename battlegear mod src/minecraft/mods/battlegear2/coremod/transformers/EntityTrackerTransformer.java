package mods.battlegear2.coremod.transformers;


import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.network.NetServerHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class EntityTrackerTransformer  implements IClassTransformer {

    private String tryStartTrackingMethodName;
    private String tryStartTrackingMethodDesc;
    private String entityPlayerClassName;
    private String entityPlayerMPClassName;
    private String netServerHandlerClasName;
    private String packetClassName;
    private String playerMPplayerNetServerHandlerField;
    private String sendPacketToPlayerMethodName;
    private String sendPacketToPlayerMethodDesc;
    private String syncPacket = "mods/battlegear2/packet/BattlegearSyncItemPacket";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.EntityTrackerEntry")) {

            entityPlayerClassName = BattlegearTranslator.getMapedClassName("EntityPlayer");
            entityPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityPlayerMP");
            netServerHandlerClasName =BattlegearTranslator.getMapedClassName("NetServerHandler");
            packetClassName = BattlegearTranslator.getMapedClassName("Packet");

            playerMPplayerNetServerHandlerField = BattlegearTranslator.getMapedFieldName("EntityPlayerMP", "field_71135_a");

            tryStartTrackingMethodName = BattlegearTranslator.getMapedMethodName("EntityTrackerEntry", "func_73117_b");
            tryStartTrackingMethodDesc = BattlegearTranslator.getMapedMethodDesc("EntityTrackerEntry", "func_73117_b");
            sendPacketToPlayerMethodName = BattlegearTranslator.getMapedMethodName("NetServerHandler", "func_72567_b");
            sendPacketToPlayerMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetServerHandler", "func_72567_b");
            System.out.println("M&B - Patching Class EntityTrackerEntry (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);


            for (Object mnObj : cn.methods) {
                MethodNode method = (MethodNode)mnObj;
                if(method.name.equals(tryStartTrackingMethodName) &&
                        method.desc.equals(tryStartTrackingMethodDesc)){

                    System.out.println("\tPatching method tryStartTrackingEntity in EntityTrackerEntry");

                    InsnList newList = new InsnList();
                    ListIterator<AbstractInsnNode> insn = method.instructions.iterator();
                    boolean done = false;
                    AbstractInsnNode next;
                    while(insn.hasNext()){
                        next = insn.next();
                        if(!done &&
                                next instanceof TypeInsnNode &&
                                next.getOpcode() == CHECKCAST &&
                                ((TypeInsnNode) next).desc.equals(entityPlayerClassName)){

                            newList.add(next);
                            newList.add(insn.next());

                            newList.add(new VarInsnNode(ALOAD, 1));
                            newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerMPplayerNetServerHandlerField, "L"+netServerHandlerClasName +";"));
                            newList.add(new TypeInsnNode(NEW, syncPacket));
                            newList.add(new InsnNode(DUP));
                            newList.add(new VarInsnNode(ALOAD, 10));
                            newList.add(new MethodInsnNode(INVOKESPECIAL, syncPacket, "<init>", "(L"+entityPlayerClassName+";)V"));
                            newList.add(new MethodInsnNode(INVOKEVIRTUAL, syncPacket, "generatePacket", "()L"+packetClassName+";"));
                            newList.add(new MethodInsnNode(INVOKEVIRTUAL, netServerHandlerClasName, sendPacketToPlayerMethodName, sendPacketToPlayerMethodDesc));
                            done = true;
                        }else{
                            newList.add(next);
                        }


                    }


                    method.instructions = newList;
                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class EntityTrackerEntry (" + name + ") done");
            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }

            return cw.toByteArray();

        } else {
            return bytes;
        }
    }
}
