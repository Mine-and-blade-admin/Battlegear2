package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ModelBipedTransformer extends TransformerMethodProcess {

    public ModelBipedTransformer() {
		super("net.minecraft.client.model.ModelBiped","func_78087_a",new String[]{"setRotationAngles", "(FFFFFFLnet/minecraft/entity/Entity;)V"});
	}

	private String modelBipedClassName;
    private String entityClassName;
    private String isSneakFieldName;

    @Override
    void setupMappings() {
    	super.setupMappings();
        modelBipedClassName = BattlegearTranslator.getMapedClassName("client.model.ModelBiped");
        entityClassName = BattlegearTranslator.getMapedClassName("entity.Entity");

        isSneakFieldName = BattlegearTranslator.getMapedFieldName("ModelBiped", "field_78117_n", "isSneak");
	}
    
    @Override
    void processMethod(MethodNode method){

        sendPatchLog("setRotationAngles");
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();
            if(nextInsn.getOpcode() == ALOAD && ((VarInsnNode) nextInsn).var == 0){
                AbstractInsnNode follow = nextInsn.getNext();
                if(follow.getOpcode() == GETFIELD && ((FieldInsnNode) follow).desc.equals("Z") && ((FieldInsnNode) follow).name.equals(isSneakFieldName)) {
                    InsnList newInsn = new InsnList();
                    newInsn.add(new VarInsnNode(ALOAD, 7));
                    newInsn.add(new VarInsnNode(ALOAD, 0));
                    newInsn.add(new VarInsnNode(FLOAD, 6));
                    newInsn.add(new MethodInsnNode(INVOKESTATIC,
                            "mods/battlegear2/client/utils/BattlegearRenderHelper",
                            "moveOffHandArm", "(L" + entityClassName + ";L" + modelBipedClassName + ";F)V"));
                    method.instructions.insertBefore(nextInsn, newInsn);
                    break;
                }
            }
        }
    }
}
