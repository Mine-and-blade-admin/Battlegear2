package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ModelBipedTransformer implements IClassTransformer {

    private String modelBipedClassName;
    private String entityClassName;

    private String renderBipedMethodName;
    private String renderBibedMethodDesc;

    private String setRotationAngleMethodName;
    private String setRotationAngleMethodDesc;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if (transformedName.equals("net.minecraft.client.model.ModelBiped")) {

            modelBipedClassName = BattlegearTranslator.getMapedClassName("ModelBiped");
            entityClassName = BattlegearTranslator.getMapedClassName("Entity");

            setRotationAngleMethodName = BattlegearTranslator.getMapedMethodName("ModelBiped", "func_78087_a");
            setRotationAngleMethodDesc = BattlegearTranslator.getMapedMethodDesc("ModelBiped", "func_78087_a");

            renderBipedMethodName = BattlegearTranslator.getMapedMethodName("ModelBiped", "func_78088_a");
            renderBibedMethodDesc = BattlegearTranslator.getMapedMethodDesc("ModelBiped", "func_78088_a");


            System.out.println("M&B - Patching Class ModelBiped (" + name + ")");
            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);
            cr.accept(cn, 0);

            cn.fields.add(0, new FieldNode(ACC_PUBLIC, "onGroundOffhand", "F", null, null));

            for (Object mnObj : cn.methods) {
                MethodNode method = (MethodNode)mnObj;
                if (method.name.equals(renderBipedMethodName) &&
                        method.desc.equals(renderBibedMethodDesc)) {

                    processRotationAnglesMethod(method);

                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class ModelBiped (" + name + ") done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }

            return cw.toByteArray();

        } else {
            return bytes;
        }
    }

    private void processRotationAnglesMethod(MethodNode method) {

        System.out.println("\tPatching Method render in ModelBiped");
        Iterator<AbstractInsnNode> it = method.instructions.iterator();

        InsnList newInsn = new InsnList();

        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();


            if(nextInsn.getOpcode() == INVOKEVIRTUAL &&
                    ((MethodInsnNode)nextInsn).name.equals(setRotationAngleMethodName) &&
                    ((MethodInsnNode)nextInsn).desc.equals(setRotationAngleMethodDesc)){
                newInsn.add(nextInsn);

                newInsn.add(new VarInsnNode(ALOAD, 1));
                newInsn.add(new VarInsnNode(ALOAD, 0));
                newInsn.add(new VarInsnNode(FLOAD, 7));;

                newInsn.add(new MethodInsnNode(INVOKESTATIC,
                        "mods/battlegear2/client/utils/BattlegearRenderHelper",
                        "moveOffHandArm", "(L" + entityClassName+";L"+modelBipedClassName + ";F)V"));
            }else{
                newInsn.add(nextInsn);
            }



        }

    }


}
