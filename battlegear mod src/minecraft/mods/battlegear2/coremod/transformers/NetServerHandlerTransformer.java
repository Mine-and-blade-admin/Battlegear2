package mods.battlegear2.coremod.transformers;


import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class NetServerHandlerTransformer implements IClassTransformer {

    private String packet16BlockItemSwitchClassName;
    private String entityPlayerMPClassName;
    private String entityPlayerClassName;
    private String netServiceHandelerClassName;
    private String inventoryPlayerClassName;
    private String itemStackClassName;

    private String playerInventoryFieldName;
    private String packet16BlockItemSwitchId;
    private String netServiceHandelerPlayerField;

    private String handleBlockSwitchMethodName;
    private String handleBlockSwitchMethodDesc;
    private String handlePlaceMethodName;
    private String handlePlaceMethodDesc;
    private String inventoryGetCurrentMethodName;
    private String inventoryGetCurrentMethodDesc;
    private String itemStackCopyStackMethodName;
    private String itemStackCopyStackMethodDesc;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (transformedName.equals("net.minecraft.network.NetServerHandler")) {

            System.out.println("M&B - Patching Class NetServerHandler (" + name + ")");

            netServiceHandelerClassName = BattlegearTranslator.getMapedClassName("NetServerHandler");
            packet16BlockItemSwitchClassName = BattlegearTranslator.getMapedClassName("Packet16BlockItemSwitch");
            entityPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityPlayerMP");
            inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("InventoryPlayer");
            itemStackClassName = BattlegearTranslator.getMapedClassName("ItemStack");
            entityPlayerClassName = BattlegearTranslator.getMapedClassName("EntityPlayer");

            packet16BlockItemSwitchId = BattlegearTranslator.getMapedFieldName("Packet16BlockItemSwitch", "field_73386_a");
            playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");
            netServiceHandelerPlayerField = BattlegearTranslator.getMapedFieldName("NetServerHandler", "field_72574_e");


            handleBlockSwitchMethodName = BattlegearTranslator.getMapedMethodName("NetServerHandler", "func_72502_a");
            handleBlockSwitchMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetServerHandler", "func_72502_a");

            handlePlaceMethodName = BattlegearTranslator.getMapedMethodName("NetServerHandler", "func_72472_a");
            handlePlaceMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetServerHandler", "func_72472_a");

            inventoryGetCurrentMethodName = BattlegearTranslator.getMapedMethodName("InventoryPlayer", "func_70448_g");
            inventoryGetCurrentMethodDesc = BattlegearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70448_g");

            itemStackCopyStackMethodName = BattlegearTranslator.getMapedMethodName("ItemStack", "func_77944_b");
            itemStackCopyStackMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemStack", "func_77944_b");


            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);
            cr.accept(cn, 0);


            for (Object mnObj : cn.methods) {
                MethodNode mn = (MethodNode)mnObj;
                if (mn.name.equals(handleBlockSwitchMethodName) &&
                        mn.desc.equals(handleBlockSwitchMethodDesc)) {
                    processSwitchBlockMethod(mn);
                } else if (mn.name.equals(handlePlaceMethodName) &&
                        mn.desc.equals(handlePlaceMethodDesc)) {
                    processPlaceMethod(mn);
                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class NetServiceHandeler done");


            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }


            return cw.toByteArray();

        } else {
            return bytes;
        }
    }

    private void processPlaceMethod(MethodNode mn) {
        System.out.println("\tPatching method handlePlace in NetServerHandler");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        int fieldCount = 0;
        while (it.hasNext()) {
            AbstractInsnNode nextNode = it.next();

            if (nextNode instanceof FieldInsnNode &&
                    ((FieldInsnNode) nextNode).owner.equals(entityPlayerMPClassName) &&
                    ((FieldInsnNode) nextNode).name.equals(playerInventoryFieldName)) {
                fieldCount++;//count number of playerEntity.inventory use

                if (fieldCount == 3) {

                    while (it.hasNext() && nextNode.getOpcode() != ACONST_NULL) {//visit till pushing null onto stack
                        nextNode = it.next();
                    }

                    newList.add(nextNode);
                    newList.add(new MethodInsnNode(INVOKESTATIC,
                            "mods/battlegear2/utils/BattlegearUtils",
                            "setPlayerCurrentItem",
                            "(L" + BattlegearTranslator.getMapedClassName("EntityPlayer") +
                                    ";L" + BattlegearTranslator.getMapedClassName("ItemStack") + ";)V"));
                    it.next();//BattlegearUtils.setPlayerCurrentItem(playerEntity, null);


                } else if (fieldCount == 4) {

                    while (it.hasNext() && nextNode.getOpcode() != AASTORE) {//visit till storing into array
                        nextNode = it.next();
                    }

//BattlegearUtils.setPlayerCurrentItem(playerEntity, ItemStack.copyItemStack(this.playerEntity.inventory.getCurrentItem()));
                    newList.add(new VarInsnNode(ALOAD, 0));
                    newList.add(new FieldInsnNode(GETFIELD, netServiceHandelerClassName, netServiceHandelerPlayerField, "L" + entityPlayerMPClassName + ";"));
                    newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerInventoryFieldName, "L" + inventoryPlayerClassName + ";"));
                    newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, inventoryGetCurrentMethodName, inventoryGetCurrentMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC, itemStackClassName, itemStackCopyStackMethodName, itemStackCopyStackMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC,
                            "mods/battlegear2/utils/BattlegearUtils",
                            "setPlayerCurrentItem", "(L" + entityPlayerClassName + ";L" + itemStackClassName + ";)V"));


                } else {
                    newList.add(nextNode);
                }
            } else {
                newList.add(nextNode);
            }

        }


        mn.instructions = newList;
    }


    private void processSwitchBlockMethod(MethodNode mn) {
        System.out.println("\tPatching method handleBlockItemSwitch in NetServerHandler");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();
            newList.add(nextInsn);

            if (nextInsn instanceof FieldInsnNode &&
                    nextInsn.getOpcode() == GETFIELD &&
                    ((FieldInsnNode) nextInsn).owner.equals(packet16BlockItemSwitchClassName) &&
                    ((FieldInsnNode) nextInsn).name.equals(packet16BlockItemSwitchId)) {

                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));

                nextInsn = it.next();
                while (it.hasNext() &&
                        (!(nextInsn instanceof JumpInsnNode)
                                || !(nextInsn.getOpcode() == IF_ICMPGE))) {//"if int greater than or equal to" branch

                    System.out.println(nextInsn.getClass());
                    nextInsn = it.next();

                }
                newList.add(new JumpInsnNode(IFEQ, ((JumpInsnNode) nextInsn).label));//make "if equal" branch

                while (it.hasNext()) {
                    nextInsn = it.next();
                    newList.add(nextInsn);
                }

            }
        }


        mn.instructions = newList;

    }
}
