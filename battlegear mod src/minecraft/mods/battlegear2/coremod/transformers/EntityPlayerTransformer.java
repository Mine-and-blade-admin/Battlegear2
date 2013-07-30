package mods.battlegear2.coremod.transformers;


import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class EntityPlayerTransformer implements IClassTransformer {

    private String entityPlayerClassName;
    private String inventoryClassName;
    private String itemStackClassName;
    private String entityClassName;
    private String potionClassName;
    private String potionEffectClassName;
    private String entityLivingClassName;


    private String playerInventoryFieldName;
    private String inventoryCurrentItremField;
    private String potionDigSpeedField;
    private String potionDigSlowField;


    private String onItemFinishMethodName;
    private String onItemFinishMethodDesc;
    private String setCurrentItemArmourMethodName;
    private String setCurrentItemArmourMethodDesc;
    private String attackTargetMethodName;
    private String attackTargetMethodDesc;
    private String playerPotionActiveMethodName;
    private String playerPotionActiveMethodDesc;
    private String playerGetActivePotionMethodName;
    private String playerGetActivePotionMethodDesc;
    private String potionEffectGetAmpMethodName;
    private String playerUpdateArmSwingMethodName;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) {

            entityPlayerClassName = BattlegearTranslator.getMapedClassName("EntityPlayer");
            inventoryClassName = BattlegearTranslator.getMapedClassName("InventoryPlayer");
            itemStackClassName = BattlegearTranslator.getMapedClassName("ItemStack");
            entityClassName = BattlegearTranslator.getMapedClassName("Entity");
            potionClassName = BattlegearTranslator.getMapedClassName("Potion");
            potionEffectClassName = BattlegearTranslator.getMapedClassName("PotionEffect");
            entityLivingClassName = BattlegearTranslator.getMapedClassName("EntityLivingBase");

            playerInventoryFieldName =
                    BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");
            inventoryCurrentItremField =
                    BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70461_c");
            potionDigSpeedField =
                    BattlegearTranslator.getMapedFieldName("Potion", "field_76422_e");
            potionDigSlowField =
                    BattlegearTranslator.getMapedFieldName("Potion", "field_76419_f");


            onItemFinishMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityPlayer", "func_71036_o");
            onItemFinishMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityPlayer", "func_71036_o");
            setCurrentItemArmourMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityPlayer", "func_70062_b");
            setCurrentItemArmourMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityPlayer", "func_70062_b");
            attackTargetMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityPlayer", "func_71059_n");
            attackTargetMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityPlayer", "func_71059_n");
            playerPotionActiveMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityLivingBase", "func_70644_a");
            playerPotionActiveMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityLivingBase", "func_70644_a");
            playerGetActivePotionMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityLivingBase", "func_70660_b");
            playerGetActivePotionMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityLivingBase", "func_70660_b");
            potionEffectGetAmpMethodName =
                    BattlegearTranslator.getMapedMethodName("PotionEffect", "func_76458_c");
            playerUpdateArmSwingMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityLivingBase", "func_82168_bl");


            System.out.println("M&B - Patching Class EntityPlayer (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            processFields(cn);
            processMethods(cn);

            System.out.println("\tCreating new methods in EntityPlayer");
            cn.methods.add(0, generateAttackOffhandMethod());
            cn.methods.add(1, generateSwingOffhand());
            cn.methods.add(2, generateGetOffSwingMethod());
            cn.methods.add(3, generateSwingAnimationEnd2());
            cn.methods.add(4, generateUpdateSwingArm());
            cn.methods.add(5, generateIsBattleMode());

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            System.out.println("M&B - Patching Class EntityPlayer done");


            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, name);
            }


            return cw.toByteArray();

        } else
            return bytes;
    }

    private void processFields(ClassNode cn) {
        System.out.println("\tAdding new fields to EntityPlayer");
        cn.fields.add(0, new FieldNode(ACC_PUBLIC, "offHandSwingProgress", "F", null, 0F));
        cn.fields.add(1, new FieldNode(ACC_PUBLIC, "prevOffHandSwingProgress", "F", null, 0F));
        cn.fields.add(2, new FieldNode(ACC_PUBLIC, "offHandSwingProgressInt", "I", null, 0));
        cn.fields.add(3, new FieldNode(ACC_PUBLIC, "isOffHandSwingInProgress", "Z", null, 0F));

        for (Object fnObj : cn.fields) {
            FieldNode fn = (FieldNode) fnObj;
            if (fn.name.equals("L" + inventoryCurrentItremField) && fn.desc.equals(inventoryClassName + ";")) {
                System.out.println("M&B - Marking field inventory as final in EntityPlayer");
                fn.access = ACC_PUBLIC | ACC_FINAL;
            }
        }
    }

    private void processMethods(ClassNode cn) {
        for (Object mnObj : cn.methods) {
            MethodNode mn = (MethodNode)mnObj;
            if (mn.name.equals("<init>")) {
                System.out.println("\tPatching constructor in EntityPlayer");
                ListIterator<AbstractInsnNode> it = mn.instructions.iterator();

                while (it.hasNext()) {
                    AbstractInsnNode insn = it.next();
                    if (insn instanceof TypeInsnNode) {
                        if (((TypeInsnNode) insn).desc.equals(inventoryClassName)) {
                            ((TypeInsnNode) insn).desc = "mods/battlegear2/inventory/InventoryPlayerBattle";
                        }

                    } else if (insn instanceof MethodInsnNode) {
                        if (((MethodInsnNode) insn).owner.equals(inventoryClassName)) {
                            ((MethodInsnNode) insn).owner = "mods/battlegear2/inventory/InventoryPlayerBattle";
                        }
                    }
                }
            } else if (mn.name.equals(onItemFinishMethodName) &&
                    mn.desc.equals(onItemFinishMethodDesc)) {
                System.out.println("\tPatching method onItemUseFinish in EntityPlayer");

                TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 3, 3);
            } else if (mn.name.equals(setCurrentItemArmourMethodName) &&
                    mn.desc.equals(setCurrentItemArmourMethodDesc)) {
                System.out.println("\tPatching method setCurrentItemOrArmor in EntityPlayer");

                TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 3, 3);
            }
        }
    }


    private MethodNode generateAttackOffhandMethod() {
        MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "attackTargetEntityWithCurrentOffItem", "(L" + entityClassName + ";)V", null, null);

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L" + inventoryClassName + ";"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L" + inventoryClassName + ";"));
        mn.instructions.add(new FieldInsnNode(GETFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
        mn.instructions.add(new FieldInsnNode(GETSTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "WEAPON_SETS", "I"));
        mn.instructions.add(new InsnNode(IADD));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new VarInsnNode(ALOAD, 1));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, attackTargetMethodName, attackTargetMethodDesc));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L" + inventoryClassName + ";"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L" + inventoryClassName + ";"));
        mn.instructions.add(new FieldInsnNode(GETFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
        mn.instructions.add(new FieldInsnNode(GETSTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "WEAPON_SETS", "I"));
        mn.instructions.add(new InsnNode(ISUB));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
        mn.instructions.add(new InsnNode(RETURN));

        mn.maxStack = 3;
        mn.maxLocals = 2;

        return mn;
    }

    private MethodNode generateSwingOffhand() {
        MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "swingOffItem", "()V", null, null);

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
        LabelNode l0 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFEQ, l0));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityPlayerClassName, "getArmSwingAnimationEndCopy", "()I"));
        mn.instructions.add(new InsnNode(ICONST_2));
        mn.instructions.add(new InsnNode(IDIV));
        mn.instructions.add(new JumpInsnNode(IF_ICMPGE, l0));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        LabelNode l1 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFGE, l1));
        mn.instructions.add(l0);
        mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(ICONST_M1));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(ICONST_1));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
        mn.instructions.add(l1);
        mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        mn.instructions.add(new InsnNode(RETURN));

        mn.maxStack = 3;
        mn.maxLocals = 1;

        return mn;
    }

    private MethodNode generateGetOffSwingMethod() {

        MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "getOffSwingProgress", "(F)F", null, null);

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgress", "F"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F"));
        mn.instructions.add(new InsnNode(FSUB));
        mn.instructions.add(new VarInsnNode(FSTORE, 2));

        mn.instructions.add(new VarInsnNode(FLOAD, 2));
        mn.instructions.add(new InsnNode(FCONST_0));
        mn.instructions.add(new InsnNode(FCMPG));
        LabelNode l0 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFGE, l0));

        mn.instructions.add(new VarInsnNode(FLOAD, 2));
        mn.instructions.add(new InsnNode(FCONST_1));
        mn.instructions.add(new InsnNode(FADD));
        mn.instructions.add(new VarInsnNode(FSTORE, 2));

        mn.instructions.add(l0);
        mn.instructions.add(new FrameNode(F_APPEND, 1, new Object[]{FLOAT}, 0, null));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F"));

        mn.instructions.add(new VarInsnNode(FLOAD, 2));
        mn.instructions.add(new VarInsnNode(FLOAD, 1));
        mn.instructions.add(new InsnNode(FMUL));
        mn.instructions.add(new InsnNode(FADD));
        mn.instructions.add(new InsnNode(FRETURN));

        mn.maxLocals = 3;
        mn.maxStack = 3;

        return mn;
    }


    private MethodNode generateSwingAnimationEnd2() {

        MethodNode mn = new MethodNode(ASM4, ACC_PRIVATE, "getArmSwingAnimationEndCopy", "()I", null, null);

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSpeedField, "L" + potionClassName + ";"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerPotionActiveMethodName, playerPotionActiveMethodDesc));
        LabelNode l0 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFEQ, l0));

        mn.instructions.add(new IntInsnNode(BIPUSH, 6));
        mn.instructions.add(new InsnNode(ICONST_1));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSpeedField, "L" + potionClassName + ";"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerGetActivePotionMethodName, playerGetActivePotionMethodDesc));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, potionEffectClassName, potionEffectGetAmpMethodName, "()I"));
        mn.instructions.add(new InsnNode(IADD));
        mn.instructions.add(new InsnNode(ICONST_1));
        mn.instructions.add(new InsnNode(IMUL));
        mn.instructions.add(new InsnNode(ISUB));
        LabelNode l1 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(GOTO, l1));

        mn.instructions.add(l0);
        mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));

        mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSlowField, "L" + potionClassName + ";"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerPotionActiveMethodName, "(L" + potionClassName + ";)Z"));
        LabelNode l2 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFEQ, l2));

        mn.instructions.add(new IntInsnNode(BIPUSH, 6));
        mn.instructions.add(new InsnNode(ICONST_1));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSlowField, "L" + potionClassName + ";"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerGetActivePotionMethodName, "(L" + potionClassName + ";)L" + potionEffectClassName + ";"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, potionEffectClassName, potionEffectGetAmpMethodName, "()I"));
        mn.instructions.add(new InsnNode(IADD));
        mn.instructions.add(new InsnNode(ICONST_2));
        mn.instructions.add(new InsnNode(IMUL));
        mn.instructions.add(new InsnNode(IADD));
        mn.instructions.add(new JumpInsnNode(GOTO, l1));


        mn.instructions.add(l2);
        mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        mn.instructions.add(new IntInsnNode(BIPUSH, 6));

        mn.instructions.add(l1);
        mn.instructions.add(new FrameNode(F_SAME1, 0, null, 1, new Object[]{INTEGER}));
        mn.instructions.add(new InsnNode(IRETURN));

        mn.maxStack = 4;
        mn.maxLocals = 1;

        return mn;
    }


    private MethodNode generateUpdateSwingArm() {

        MethodNode mn = new MethodNode(ASM4, ACC_PROTECTED, playerUpdateArmSwingMethodName, "()V", null, null);

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityLivingClassName, playerUpdateArmSwingMethodName, "()V"));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));

        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgress", "F"));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F"));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));

        mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityPlayerClassName, "getArmSwingAnimationEndCopy", "()I"));
        mn.instructions.add(new VarInsnNode(ISTORE, 1));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));


        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
        LabelNode l0 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IFEQ, l0));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(DUP));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        mn.instructions.add(new InsnNode(ICONST_1));
        mn.instructions.add(new InsnNode(IADD));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        mn.instructions.add(new VarInsnNode(ILOAD, 1));
        LabelNode l1 = new LabelNode();
        mn.instructions.add(new JumpInsnNode(IF_ICMPLT, l1));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(ICONST_0));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(ICONST_0));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
        mn.instructions.add(new JumpInsnNode(GOTO, l1));

        mn.instructions.add(l0);
        mn.instructions.add(new FrameNode(F_APPEND, 1, new Object[]{INTEGER}, 0, null));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new InsnNode(ICONST_0));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));

        mn.instructions.add(l1);
        mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));

        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
        mn.instructions.add(new InsnNode(I2F));
        mn.instructions.add(new VarInsnNode(ILOAD, 1));
        mn.instructions.add(new InsnNode(I2F));
        mn.instructions.add(new InsnNode(FDIV));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgress", "F"));
        mn.instructions.add(new InsnNode(RETURN));

        mn.maxStack = 3;
        mn.maxLocals = 2;


        return mn;
    }


    private MethodNode generateIsBattleMode() {
        MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "isBattlemode", "()Z", null, null);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L" + inventoryClassName + ";"));
        mn.instructions.add(new TypeInsnNode(CHECKCAST, "mods/battlegear2/inventory/InventoryPlayerBattle"));
        mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "mods/battlegear2/inventory/InventoryPlayerBattle", "isBattlemode", "()Z"));
        mn.instructions.add(new InsnNode(IRETURN));

        mn.maxStack = 1;
        mn.maxLocals = 1;

        return mn;
    }


}
