package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

public class EntityAIControlledByPlayerTransformer implements IClassTransformer {


    private String entityPlayerClassName;

    private String playerInventoryFieldName;

    private String updateTaskMethodName;
    private String updateTaskMethodDesc;

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.ai.EntityAIControlledByPlayer")) {

            System.out.println("M&B - Patching Class EntityAIControlledByPlayer (" + name + ")");

            entityPlayerClassName = BattlegearTranslator.getMapedClassName("EntityPlayer");
            playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");
            updateTaskMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityAIControlledByPlayer", "func_75246_d");
            updateTaskMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityAIControlledByPlayer", "func_75246_d");


            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            for (Object mnObj : cn.methods) {
                MethodNode mn = (MethodNode)mnObj;

                if (mn.name.equals(updateTaskMethodName) &&
                        mn.desc.equals(updateTaskMethodDesc)) {
                    System.out.println("\tPatching method updateTask in EntityAIControlledByPlayer");

                    TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 8, 23);
                }
            }


            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class EntityAIControlledByPlayer done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }

            return cw.toByteArray();
        } else {
            return bytes;
        }
    }

}
