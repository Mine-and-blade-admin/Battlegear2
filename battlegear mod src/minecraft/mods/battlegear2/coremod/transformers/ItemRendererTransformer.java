package mods.battlegear2.coremod.transformers;


import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ItemRendererTransformer implements IClassTransformer {

    private String itemStackClass;
    private String itemRendererClass;
    private String minecraftClass;

    private String itemRendererMinecraftField;
    private String itemRendereriteToRenderField;

    private String renderItem1stPersonMethodName;
    private String renderItem1stPersonMethodDesc;
    private String updateEquippedItemMethodName;
    private String updateEquippedItemMethodDesc;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (transformedName.equals("net.minecraft.client.renderer.ItemRenderer")) {

            System.out.println("M&B - Patching Class ItemRenderer (" + name + ")");


            itemStackClass = BattlegearTranslator.getMapedClassName("ItemStack");
            itemRendererClass = BattlegearTranslator.getMapedClassName("ItemRenderer");
            minecraftClass = BattlegearTranslator.getMapedClassName("Minecraft");

            itemRendererMinecraftField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78455_a");
            itemRendereriteToRenderField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78453_b");

            renderItem1stPersonMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78440_a");
            renderItem1stPersonMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78440_a");

            updateEquippedItemMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78441_a");
            updateEquippedItemMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78441_a");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            processFields(cn);

            for (Object mnObj : cn.methods) {
                MethodNode mn = (MethodNode)mnObj;
                if (mn.name.equals(renderItem1stPersonMethodName) &&
                        mn.desc.equals(renderItem1stPersonMethodDesc)) {
                    processRenderItemMethod(mn);
                } else if (mn.name.equals(updateEquippedItemMethodName) &&
                        mn.desc.equals(updateEquippedItemMethodDesc)) {
                    processupdateEquippedMethod(mn);
                }
            }


            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class ItemRenderer done");


            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, name);
            }


            return cw.toByteArray();


        } else {
            return bytes;
        }
    }

    private void processupdateEquippedMethod(MethodNode mn) {
        System.out.println("\tPatching method updateEquippedItem in ItemRenderer");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == RETURN) {
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendereriteToRenderField, "L" + itemStackClass + ";"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "updateEquippedItem"
                        , "(L" + itemRendererClass + ";L" + minecraftClass + ";L" + itemStackClass + ";)V"));
            }

            newList.add(insn);
        }

        mn.instructions = newList;
    }

    private void processRenderItemMethod(MethodNode mn) {

        System.out.println("\tPatching method renderItemInFirstPerson in ItemRenderer");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insn = it.next();

            if (insn.getOpcode() == RETURN) {
                newList.add(new VarInsnNode(FLOAD, 1));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, "offHandItemToRender", "L" + itemStackClass + ";"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "renderItemInFirstPerson"
                        , "(FL" + minecraftClass + ";L" + itemRendererClass + ";L" + itemStackClass + ";)V"));
            }

            newList.add(insn);
        }

        mn.instructions = newList;
    }

    private void processFields(ClassNode cn) {
        System.out.println("\tAdding new fields to ItemRenderer");
        cn.fields.add(0, new FieldNode(ACC_PUBLIC, "offHandItemToRender", "L" + itemStackClass + ";", null, null));
        cn.fields.add(1, new FieldNode(ACC_PUBLIC, "equippedItemOffhandSlot", "I", null, 0));
        cn.fields.add(2, new FieldNode(ACC_PUBLIC, "equippedOffHandProgress", "F", null, 0F));
        cn.fields.add(3, new FieldNode(ACC_PUBLIC, "prevEquippedOffHandProgress", "F", null, 0F));
    }
}
