package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.api.core.IBattlePlayer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.ListIterator;

public final class EntityPlayerTransformer extends TransformerBase {

    public EntityPlayerTransformer() {
        super("net.minecraft.entity.player.EntityPlayer");
    }

    private String entityPlayerClassName;
    private String inventoryClassName;
    private String itemStackClassName;
    private String entityClassName;
    private String entityLivingClassName;

    private String playerInventoryFieldName;
    private String playerItemInUseField;
    private String swingProgressBooleanField;
    private String swingProgressIntField;
    private String swingProgressFloatField;

    private String onItemFinishMethodName;
    private String setCurrentItemArmourMethodName;
    private String setCurrentItemArmourMethodDesc;
    private String onUpdateMethodName;
    private String playerUpdateArmSwingMethodName;
    private String getArmSwingEndMethodName;

    private String interactWithMethodName;
    private String interactWithMethodDesc;

    @Override
    void addInterface(List<String> interfaces) {
        interfaces.add(Type.getInternalName(IBattlePlayer.class));
    }

    @Override
    boolean processFields(List<FieldNode> fields) {
        logger.log(Level.INFO, "\tAdding new fields to EntityPlayer");
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "offHandSwingProgress", "F", null, 0F));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "prevOffHandSwingProgress", "F", null, 0F));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "offHandSwingProgressInt", "I", null, 0));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "isOffHandSwingInProgress", "Z", null, false));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "specialActionTimer", "I", null, 0));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "isShielding", "Z", null, false));
        return true;
    }

    @Override
    boolean processMethods(List<MethodNode> methods) {
        int found = 0;
        for (MethodNode mn : methods) {
            if (mn.name.equals("<init>")) {
                logger.log(Level.INFO, "\tPatching constructor in EntityPlayer");
                ListIterator<AbstractInsnNode> it = mn.instructions.iterator();

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
            } else if (mn.name.equals(onItemFinishMethodName) && mn.desc.equals(SIMPLEST_METHOD_DESC)) {
                sendPatchLog("onItemUseFinish");
                InsnList newList = new InsnList();
                ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
                while (it.hasNext()) {
                    AbstractInsnNode next = it.next();
                    if (next instanceof MethodInsnNode && ((MethodInsnNode) next).owner.equals("net/minecraftforge/event/ForgeEventFactory")) {
                        found++;
                        int index = ((MethodInsnNode) next).desc.indexOf(")");
                        String newDesc = ((MethodInsnNode) next).desc.substring(0, index) + "I" + ((MethodInsnNode) next).desc.substring(index);
                        newList.add(new VarInsnNode(ILOAD, 1));
                        newList.add(new MethodInsnNode(INVOKESTATIC, UTILITY_CLASS, "beforeFinishUseEvent", newDesc));
                    } else {
                        newList.add(next);
                    }
                }
                mn.instructions = newList;
            } else if (mn.name.equals(onUpdateMethodName) && mn.desc.equals(SIMPLEST_METHOD_DESC)) {
                sendPatchLog("onUpdate");
                InsnList newList = new InsnList();
                ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
                while (it.hasNext()) {
                    AbstractInsnNode next = it.next();
                    if (next instanceof FieldInsnNode && ((FieldInsnNode) next).owner.equals(entityPlayerClassName) && ((FieldInsnNode) next).name.equals(playerInventoryFieldName)) {
                        found++;
                        newList.add(new VarInsnNode(ALOAD, 0));
                        newList.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerItemInUseField, "L" + itemStackClassName + ";"));
                        newList.add(new MethodInsnNode(INVOKESTATIC, UTILITY_CLASS, "getCurrentItemOnUpdate", "(L" + entityPlayerClassName + ";L" + itemStackClassName + ";)L" + itemStackClassName + ";"));
                        next = it.next();
                    } else {
                        newList.add(next);
                    }
                }
                mn.instructions = newList;
            } else if (mn.name.equals(setCurrentItemArmourMethodName) && mn.desc.equals(setCurrentItemArmourMethodDesc)) {

                sendPatchLog("setCurrentItemOrArmor");
                replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, mn.maxStack, mn.maxLocals);
                found++;
            } else if(mn.name.equals(interactWithMethodName) && mn.desc.equals(interactWithMethodDesc)){
                sendPatchLog("interactWith");
                MethodNode mv = new MethodNode(ACC_PUBLIC, interactWithMethodName, interactWithMethodDesc, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, UTILITY_CLASS, "interactWith", "(L" + entityPlayerClassName + ";L" + entityClassName + ";)Z");
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l1, 0);
                mv.visitLocalVariable("p_70998_1_", "L" + entityClassName + ";", null, l0, l1, 1);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
                mn.instructions = mv.instructions;
                found++;
            }
        }

        logger.log(Level.INFO, "\tCreating new methods in EntityPlayer");
        methods.add(methods.size(), generateAttackOffhandMethod());
        methods.add(methods.size(), generateSwingOffhand());
        methods.add(methods.size(), generateGetOffSwingMethod());
        methods.add(methods.size(), generateUpdateSwingArm());
        methods.add(methods.size(), generateIsBattleMode());
        methods.add(methods.size(), generateIsBlockingWithShield());
        methods.add(methods.size(), generateSetBlockingWithShield());
        methods.add(methods.size(), generateGetter(entityPlayerClassName, "getSpecialActionTimer", "specialActionTimer", "I"));
        methods.add(methods.size(), generateSetter(entityPlayerClassName, "setSpecialActionTimer", "specialActionTimer", "I"));
        return found == 5;
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

    private MethodNode generateSwingOffhand() {
        MethodNode mv = new MethodNode(ACC_PUBLIC, "swingOffItem", SIMPLEST_METHOD_DESC, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z");
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, entityPlayerClassName, getArmSwingEndMethodName, "()I");
        mv.visitInsn(ICONST_2);
        mv.visitInsn(IDIV);
        mv.visitJumpInsn(IF_ICMPGE, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        Label l2 = new Label();
        mv.visitJumpInsn(IFGE, l2);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_M1);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_1);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z");
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l4, 0);
        mv.visitMaxs(3, 1);
        mv.visitEnd();

        return mv;
    }

    private MethodNode generateGetOffSwingMethod() {

        MethodNode mv = new MethodNode(ACC_PUBLIC, "getOffSwingProgress", "(F)F", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgress", "F");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F");
        mv.visitInsn(FSUB);
        mv.visitVarInsn(FSTORE, 2);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(FLOAD, 2);
        mv.visitInsn(FCONST_0);
        mv.visitInsn(FCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFGE, l2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(FLOAD, 2);
        mv.visitInsn(FCONST_1);
        mv.visitInsn(FADD);
        mv.visitVarInsn(FSTORE, 2);
        mv.visitLabel(l2);
        mv.visitFrame(F_APPEND, 1, new Object[]{FLOAT}, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F");
        mv.visitVarInsn(FLOAD, 2);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FMUL);
        mv.visitInsn(FADD);
        mv.visitInsn(FRETURN);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l4, 0);
        mv.visitLocalVariable("frame", "F", null, l0, l4, 1);
        mv.visitLocalVariable("diff", "F", null, l1, l4, 2);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

        return mv;
    }

    private MethodNode generateUpdateSwingArm() {

        MethodNode mv = new MethodNode(ACC_PROTECTED, playerUpdateArmSwingMethodName, SIMPLEST_METHOD_DESC, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, entityLivingClassName, playerUpdateArmSwingMethodName, SIMPLEST_METHOD_DESC);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgress", "F");
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F");
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, entityPlayerClassName, getArmSwingEndMethodName, "()I");
        mv.visitVarInsn(ISTORE, 1);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z");
        Label l4 = new Label();
        mv.visitJumpInsn(IFEQ, l4);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        mv.visitVarInsn(ILOAD, 1);
        Label l7 = new Label();
        mv.visitJumpInsn(IF_ICMPLT, l7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z");
        mv.visitJumpInsn(GOTO, l7);
        mv.visitLabel(l4);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        mv.visitLabel(l7);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        mv.visitInsn(I2F);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(I2F);
        mv.visitInsn(FDIV);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgress", "F");
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, entityPlayerClassName, "specialActionTimer", "I");
        Label l11 = new Label();
        mv.visitJumpInsn(IFLE, l11);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z");
        Label l13 = new Label();
        mv.visitLabel(l13);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, swingProgressBooleanField, "Z");
        Label l14 = new Label();
        mv.visitLabel(l14);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(FCONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgress", "F");
        Label l15 = new Label();
        mv.visitLabel(l15);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I");
        Label l16 = new Label();
        mv.visitLabel(l16);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(FCONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, swingProgressFloatField, "F");
        Label l17 = new Label();
        mv.visitLabel(l17);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ICONST_0);
        mv.visitFieldInsn(PUTFIELD, entityPlayerClassName, swingProgressIntField, "I");
        mv.visitLabel(l11);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        Label l18 = new Label();
        mv.visitLabel(l18);
        mv.visitLocalVariable("this", "L" + entityPlayerClassName + ";", null, l0, l18, 0);
        mv.visitLocalVariable("var1", "I", null, l3, l18, 1);
        mv.visitMaxs(3, 2);
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
        entityLivingClassName = BattlegearTranslator.getMapedClassName("entity.EntityLivingBase");

        playerInventoryFieldName =
                BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");
        playerItemInUseField = BattlegearTranslator.getMapedFieldName("field_71074_e", "itemInUse");
        swingProgressBooleanField = BattlegearTranslator.getMapedFieldName("field_82175_bq", "isSwingInProgress");
        swingProgressIntField = BattlegearTranslator.getMapedFieldName("field_110158_av", "swingProgressInt");
        swingProgressFloatField = BattlegearTranslator.getMapedFieldName("field_70733_aJ", "swingProgress");
        onItemFinishMethodName =
                BattlegearTranslator.getMapedMethodName("func_71036_o", "onItemUseFinish");
        setCurrentItemArmourMethodName =
                BattlegearTranslator.getMapedMethodName("func_70062_b", "setCurrentItemOrArmor");
        setCurrentItemArmourMethodDesc = "(IL" + itemStackClassName + ";)V";
        onUpdateMethodName = BattlegearTranslator.getMapedMethodName("func_70071_h_", "onUpdate");
        playerUpdateArmSwingMethodName =
                BattlegearTranslator.getMapedMethodName("func_82168_bl", "updateArmSwingProgress");
        getArmSwingEndMethodName =
                BattlegearTranslator.getMapedMethodName("func_82166_i", "getArmSwingAnimationEnd");
        interactWithMethodName = BattlegearTranslator.getMapedMethodName("func_70998_m", "interactWith");
        interactWithMethodDesc = "(L"+entityClassName+";)Z";
    }
}
