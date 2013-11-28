package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class ModelBipedTransformer extends TransformerMethodProcess {

    public ModelBipedTransformer() {
		super("net.minecraft.client.model.ModelBiped","func_78088_a");
	}

	private String modelBipedClassName;
    private String entityClassName;

    private String setRotationAngleMethodName;
    private String setRotationAngleMethodDesc;

    @Override
    void setupMappings() {
    	super.setupMappings();
        modelBipedClassName = BattlegearTranslator.getMapedClassName("ModelBiped");
        entityClassName = BattlegearTranslator.getMapedClassName("Entity");

        setRotationAngleMethodName = BattlegearTranslator.getMapedMethodName("ModelBiped", "func_78087_a");
        setRotationAngleMethodDesc = BattlegearTranslator.getMapedMethodDesc("ModelBiped", "func_78087_a");
	}
    
    @Override
    void processFields(List<FieldNode> fields){
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "onGroundOffhand", "F", null, null));
    }
    
    @Override
    void processMethod(MethodNode method){

        sendPatchLog("render");
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
