package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.api.core.IBattlePlayer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class EntityPlayerTransformer extends TransformerBase {

    public EntityPlayerTransformer() {
        super("net.minecraft.entity.player.EntityPlayer");
    }

    private String entityPlayerClassName;
    private String inventoryClassName;
    private String itemStackClassName;
    private String entityClassName;
    private String enumHandClassName;
    private String enumActionResult;
    private String equipmentSlotClassName;

    private String getItemStackMethodName;
    private String getItemStackMethodDesc;
    private String setCurrentItemArmourMethodName;
    private String setCurrentItemArmourMethodDesc;
    private String playEquipSoundMethodName;
    private String interactWithMethodName;
    private String interactWithMethodDesc;

    @Override
    void addInterface(List<String> interfaces) {
        interfaces.add(Type.getInternalName(IBattlePlayer.class));
    }

    @Override
    boolean processFields(List<FieldNode> fields) {
        logger.log(Level.INFO, "\tAdding new fields to EntityPlayer");
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "specialActionTimer", "I", null, 0));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "isShielding", "Z", null, false));
        return true;
    }

    @Override
    boolean processMethods(List<MethodNode> methods) {//TODO check replaceItemInInventory
        int found = 0;
        for (MethodNode mn : methods) {
            if (mn.name.equals("<init>")) {
                logger.log(Level.INFO, "\tPatching constructor in EntityPlayer");
                Iterator<AbstractInsnNode> it = mn.instructions.iterator();

                while (it.hasNext()) {
                    AbstractInsnNode insn = it.next();
                    if (insn instanceof TypeInsnNode) {
                        if (((TypeInsnNode) insn).desc.equals(inventoryClassName)) {
                            ((TypeInsnNode) insn).desc = "mods/battlegear2/api/core/InventoryPlayerBattle";
                        }

                    } else if (insn instanceof MethodInsnNode) {
                        if (((MethodInsnNode) insn).owner.equals(inventoryClassName)) {
                            ((MethodInsnNode) insn).owner = "mods/battlegear2/api/core/InventoryPlayerBattle";
                        }
                    }
                }
                found++;
            } else if (mn.name.equals(getItemStackMethodName) && mn.desc.equals(getItemStackMethodDesc)) {
                sendPatchLog("getItemStackFromSlot");
                MethodNode mv = new MethodNode(ACC_PUBLIC, getItemStackMethodName, getItemStackMethodDesc, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "getItemStackFromSlot", "(L" + entityPlayerClassName + ";L" + equipmentSlotClassName + ";)L" + itemStackClassName + ";");
                mv.visitInsn(ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l1, 0);
                mv.visitLocalVariable("slotIn", "L" + equipmentSlotClassName + ";", null, l0, l1, 1);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
                mn.instructions = mv.instructions;
                found++;
            } else if (mn.name.equals(setCurrentItemArmourMethodName) && mn.desc.equals(setCurrentItemArmourMethodDesc)) {

                sendPatchLog("setItemStackToSlot");
                MethodNode mv = new MethodNode(ACC_PUBLIC, setCurrentItemArmourMethodName, setCurrentItemArmourMethodDesc, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "setItemStackToSlot", "(L" + entityPlayerClassName + ";L" + equipmentSlotClassName + ";L" + itemStackClassName + ";)Z");

                Label l1 = new Label();
                mv.visitJumpInsn(IFEQ, l1);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, entityPlayerClassName, playEquipSoundMethodName, "(L" + itemStackClassName + ";)V");
                mv.visitLabel(l1);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitInsn(RETURN);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l3, 0);
                mv.visitLocalVariable("slotIn", "L" + equipmentSlotClassName + ";", null, l0, l3, 1);
                mv.visitLocalVariable("stack", "L" + itemStackClassName + ";", null, l0, l3, 2);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
                mn.instructions = mv.instructions;
                found++;
            } else if(mn.name.equals(interactWithMethodName) && mn.desc.equals(interactWithMethodDesc)){
                sendPatchLog("interactOn");
                InsnList newList = new InsnList();
                Iterator<AbstractInsnNode> it = mn.instructions.iterator();
                int count = 0;
                while (it.hasNext()) {
                    AbstractInsnNode insn = it.next();
                    if(insn instanceof FieldInsnNode && insn.getOpcode() == GETSTATIC && insn.getNext() instanceof InsnNode && insn.getNext().getOpcode() == ARETURN){
                        count++;
                        if(count == 5){//The last returning instruction
                            newList.add(new VarInsnNode(ALOAD, 0));
                            newList.add(new VarInsnNode(ALOAD, 1));
                            newList.add(new VarInsnNode(ALOAD, 2));
                            newList.add(new MethodInsnNode(INVOKESTATIC, UTILITY_CLASS, "interactWith"
                                    , "(L" + entityPlayerClassName + ";L" + entityClassName +";L" + enumHandClassName +";)L" + enumActionResult + ";"));
                            found++;
                            continue;
                        }
                    }
                    newList.add(insn);
                }
                mn.instructions = newList;
            }
        }

        logger.log(Level.INFO, "\tCreating new methods in EntityPlayer");
        methods.add(methods.size(), generateAttackOffhandMethod());
        methods.add(methods.size(), generateIsBattleMode());
        methods.add(methods.size(), generateIsBlockingWithShield());
        methods.add(methods.size(), generateSetBlockingWithShield());
        methods.add(methods.size(), generateGetter(entityPlayerClassName, "getSpecialActionTimer", "specialActionTimer", "I"));
        methods.add(methods.size(), generateSetter(entityPlayerClassName, "setSpecialActionTimer", "specialActionTimer", "I"));
        return found == 4;
    }

    private MethodNode generateIsBlockingWithShield() {
        MethodNode mv = new MethodNode(ACC_PUBLIC, "isBlockingWithShield", "()Z", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "canBlockWithShield", "(L" + entityPlayerClassName + ";)Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "isShielding", "Z");
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{INTEGER});
        mv.visitInsn(IRETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l3, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        return mv;
    }

    private MethodNode generateSetBlockingWithShield() {
        MethodNode mv = new MethodNode(ACC_PUBLIC, "setBlockingWithShield", "(Z)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "canBlockWithShield", "(L" + entityPlayerClassName + ";)Z");
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{entityPlayerClassName});
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
        mv.visitFrame(F_FULL, 2, new Object[]{entityPlayerClassName, INTEGER}, 2, new Object[]{entityPlayerClassName, INTEGER});
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "isShielding", "Z");
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l4, 0);
        mv.visitLocalVariable("block", "Z", null, l0, l4, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        return mv;
    }

    private MethodNode generateAttackOffhandMethod() {

        MethodNode mv = new MethodNode(ACC_PUBLIC, "attackTargetEntityWithCurrentOffItem", "(L" + entityClassName + ";)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "attackTargetEntityWithCurrentOffItem", "(L" + entityPlayerClassName + ";L" + entityClassName + ";)V");
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l2, 0);
        mv.visitLocalVariable("target", "L" + entityClassName + ";", null, l0, l2, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        return mv;
    }

    private MethodNode generateIsBattleMode() {
        MethodNode mv = new MethodNode(ACC_PUBLIC, "isBattlemode", "()Z", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "isPlayerInBattlemode", "(L" + entityPlayerClassName + ";)Z");
        mv.visitInsn(IRETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        return mv;
    }

    @Override
    void setupMappings() {
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        inventoryClassName = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        entityClassName = BattlegearTranslator.getMapedClassName("entity.Entity");
        equipmentSlotClassName = BattlegearTranslator.getMapedClassName("inventory.EntityEquipmentSlot");

        getItemStackMethodName =
                BattlegearTranslator.getMapedMethodName("func_184582_a", "getItemStackFromSlot");
        getItemStackMethodDesc = "(L"+equipmentSlotClassName+";)L" + itemStackClassName + ";";
        setCurrentItemArmourMethodName =
                BattlegearTranslator.getMapedMethodName("func_184201_a", "setItemStackToSlot");
        setCurrentItemArmourMethodDesc = "(L"+equipmentSlotClassName+";L" + itemStackClassName + ";)V";
        interactWithMethodName = BattlegearTranslator.getMapedMethodName("func_190775_a", "interactOn");
        enumHandClassName = BattlegearTranslator.getMapedClassName("util.EnumHand");
        enumActionResult = BattlegearTranslator.getMapedClassName("util.EnumActionResult");
        interactWithMethodDesc = "(L"+entityClassName+";L" + enumHandClassName +";)L"+enumActionResult +";";
        playEquipSoundMethodName = BattlegearTranslator.getMapedMethodName("func_184606_a_", "playEquipSound");
    }
}
