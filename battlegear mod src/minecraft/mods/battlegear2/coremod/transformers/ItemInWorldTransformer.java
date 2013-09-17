package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ItemInWorldTransformer implements IClassTransformer {

    private String entityPlayerClassName;
    private String inventoryPlayerClassName;
    private String itemStackClassName;
    private String entityOtherPlayerMPClassName;

    private String playerInventoryFieldName;
    private String mainInventoryArrayFieldName;

    private String tryUseItemMethodName;
    private String tryUseItemMethodDesc;
    private String setInventorySlotMethodName;
    private String setInventorySlotMethodDesc;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.item.ItemInWorldManager")) {

            System.out.println("M&B - Patching Class ItemInWorldManager (" + name + ")");

            entityPlayerClassName = BattlegearTranslator.getMapedClassName("EntityPlayer");
            inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("InventoryPlayer");
            itemStackClassName = BattlegearTranslator.getMapedClassName("ItemStack");

            playerInventoryFieldName =
                    BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");
            mainInventoryArrayFieldName =
                    BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a");

            tryUseItemMethodName =
                    BattlegearTranslator.getMapedMethodName("ItemInWorldManager", "func_73085_a");
            tryUseItemMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("ItemInWorldManager", "func_73085_a");
            setInventorySlotMethodName =
                    BattlegearTranslator.getMapedMethodName("InventoryPlayer", "func_70299_a");
            setInventorySlotMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70299_a");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            for (Object mnObj : cn.methods) {
                MethodNode mn = (MethodNode)mnObj;

                if (mn.name.equals(tryUseItemMethodName) &&
                        mn.desc.equals(tryUseItemMethodDesc)) {
                    processTryUseItemMethod(mn);
                }

            }


            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            System.out.println("M&B - Patching Class ItemInWorldManager done");


            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }
            return cw.toByteArray();
        } else {
            return bytes;
        }
    }


    private void processTryUseItemMethod(MethodNode mn) {

        System.out.println("\tPatching method tryUseItem in ItemInWorldManager");
        TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 5, 7);

        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        while (it.hasNext()) {
            AbstractInsnNode node = it.next();

            if (node instanceof FieldInsnNode &&
                    ((FieldInsnNode) node).owner.equals(inventoryPlayerClassName) &&
                    ((FieldInsnNode) node).name.equals(mainInventoryArrayFieldName) &&
                    ((FieldInsnNode) node).desc.equals("[L" + itemStackClassName + ";")) {

                //Do Nothing
            } else if (node.getOpcode() == AASTORE) {
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, itemStackClassName, setInventorySlotMethodName, setInventorySlotMethodDesc));
            } else {
                newList.add(node);
            }
        }

        mn.instructions = newList;

    }

}










