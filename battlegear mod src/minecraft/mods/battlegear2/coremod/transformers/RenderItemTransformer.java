package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Created by Olivier on 19/07/2015.
 */
public class RenderItemTransformer extends TransformerMethodProcess{

    private String getItemInUseMethodName;
    private String itemModelMesher;
    public RenderItemTransformer() {
        super("net.minecraft.client.renderer.entity.RenderItem", "func_175049_a", new String[]{"renderItemModelForEntity", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V"});
    }

    @Override
    void processMethod(MethodNode method) {
        sendPatchLog("renderItemModelForEntity");
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        InsnList newList = new InsnList();
        boolean foundGetField = false;
        LabelNode node = null;
        while (it.hasNext()) {
            AbstractInsnNode nextNode = it.next();
            if(!foundGetField){
                if(nextNode instanceof FieldInsnNode && nextNode.getOpcode() == GETFIELD && ((FieldInsnNode) nextNode).name.equals(itemModelMesher)) {
                    ((VarInsnNode)nextNode.getPrevious()).var = 1;
                    nextNode = it.next();
                    ((VarInsnNode)nextNode).var = 2;
                    foundGetField = true;
                    newList.add(nextNode);
                    nextNode = it.next();
                    if (nextNode instanceof MethodInsnNode) {
                        newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "getItemModel", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/client/resources/model/IBakedModel;", false));
                        continue;
                    }
                }
            }else if(node == null){
                if(nextNode instanceof JumpInsnNode && nextNode.getOpcode() == GOTO)
                    node = ((JumpInsnNode)nextNode).label;
            }else if(nextNode instanceof MethodInsnNode && nextNode.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) nextNode).name.equals(getItemInUseMethodName)){
                newList.add(nextNode);
                newList.add(it.next());
                while(it.hasNext()){
                    nextNode = it.next();
                    if(nextNode instanceof JumpInsnNode && nextNode.getOpcode() == GOTO && ((JumpInsnNode) nextNode).label == node){
                        break;
                    }
                }
            }
            newList.add(nextNode);
        }
        method.instructions = newList;
    }


    @Override
    void setupMappings() {
        super.setupMappings();
        getItemInUseMethodName = BattlegearTranslator.getMapedMethodName("func_71011_bu", "getItemInUse");
        itemModelMesher = BattlegearTranslator.getMapedFieldName("field_175059_m", "itemModelMesher");
    }
}
