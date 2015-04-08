package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class EntityOtherPlayerMPTransformer extends TransformerBase {

    public EntityOtherPlayerMPTransformer() {
        super("net.minecraft.client.entity.EntityOtherPlayerMP");
    }

    private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;
    private String onUpdateMethodName;
    private String setCurrentItemMethodName;
    private String setCurrentItemMethodDesc;
    private String isItemInUseFieldName;
    private String limbSwingFieldName;

    private void processOnUpdateMethod2(MethodNode mn) {

        sendPatchLog("onUpdate");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        boolean done = false;
        while (it.hasNext() && !done) {
            AbstractInsnNode node = it.next();
            if (node instanceof FieldInsnNode &&
                    node.getOpcode() == PUTFIELD &&
                    ((FieldInsnNode) node).owner.equals(entityOtherPlayerMPClassName) &&
                    ((FieldInsnNode) node).name.equals(limbSwingFieldName)) {
                newList.add(node);
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));

                newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, isItemInUseFieldName, "Z"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearClientUtils", "entityOtherPlayerIsItemInUseHook", "(L" + entityOtherPlayerMPClassName + ";Z)Z"));
                newList.add(new FieldInsnNode(PUTFIELD, entityOtherPlayerMPClassName, isItemInUseFieldName, "Z"));

                node = it.next();
                while (!(node instanceof InsnNode && node.getOpcode() == RETURN)) {
                    node = it.next();
                }
                newList.add(node);
                while (it.hasNext()) {
                    node = it.next();
                    newList.add(node);
                }

                done = true;
            } else {
                newList.add(node);
            }

        }
        mn.instructions = newList;
    }

    private void processSetCurrentItemMethod(MethodNode mn) {
        sendPatchLog("setCurrentItem");
        replaceInventoryArrayAccess(mn, entityOtherPlayerMPClassName, playerInventoryFieldName, 4, 3, 3);
    }

    @Override
    boolean processMethods(List<MethodNode> methods) {
        int found = 0;
        for (MethodNode mn : methods) {
            if (mn.name.equals(setCurrentItemMethodName) && mn.desc.equals(setCurrentItemMethodDesc)) {
                processSetCurrentItemMethod(mn);
                found++;
            }

            if (mn.name.equals(onUpdateMethodName) && mn.desc.equals(SIMPLEST_METHOD_DESC)) {
                processOnUpdateMethod2(mn);
                found++;
            }
        }
        return found == 2;
    }

    @Override
    boolean processFields(List<FieldNode> fields) {
        return true;
    }

    @Override
    void setupMappings() {
        String itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("client.entity.EntityOtherPlayerMP");

        isItemInUseFieldName = BattlegearTranslator.getMapedFieldName("field_71186_a", "isItemInUse");
        limbSwingFieldName = BattlegearTranslator.getMapedFieldName("field_70754_ba", "limbSwing");
        playerInventoryFieldName =
                BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");

        setCurrentItemMethodName =
                BattlegearTranslator.getMapedMethodName("func_70062_b", "setCurrentItemOrArmor");
        setCurrentItemMethodDesc = "(IL" + itemStackClassName + ";)V";
        onUpdateMethodName =
                BattlegearTranslator.getMapedMethodName("func_70071_h_", "onUpdate");
    }
}
