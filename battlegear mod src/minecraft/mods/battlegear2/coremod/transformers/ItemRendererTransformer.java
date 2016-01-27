package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.api.core.IOffhandRender;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class ItemRendererTransformer extends TransformerBase {

    public ItemRendererTransformer() {
        super("net.minecraft.client.renderer.ItemRenderer");
    }

    private String itemStackClass;
    private String itemRendererClass;
    private String minecraftClass;
    private String itemRendererMinecraftField;
    private String renderItem1stPersonMethodName;
    private String renderItem1stPersonMethodDesc;
    private String updateEquippedItemMethodName;

    @Override
    void addInterface(List<String> interfaces) {
        interfaces.add(Type.getInternalName(IOffhandRender.class));
    }

    private AbstractInsnNode getReturnNode(MethodNode mn) {
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insn = it.next();
            if (insn.getOpcode() == RETURN) {
                return insn;
            }
        }
        return null;
    }

    private void processUpdateEquippedMethod(MethodNode mn) {
        sendPatchLog("updateEquippedItem");
        AbstractInsnNode insnNode = mn.instructions.getFirst();
        if (insnNode != null) {
            InsnList newList = new InsnList();
            newList.add(new VarInsnNode(ALOAD, 0));
            newList.add(new VarInsnNode(ALOAD, 0));
            newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
            newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "updateEquippedItem"
                    , "(L" + itemRendererClass + ";L" + minecraftClass + ";)V"));
            mn.instructions.insertBefore(insnNode, newList);
        }
    }

    private void processRenderItemMethod(MethodNode mn) {
        sendPatchLog("renderItemInFirstPerson");
        AbstractInsnNode insnNode = getReturnNode(mn);
        if (insnNode != null) {
            InsnList newList = new InsnList();
            newList.add(new VarInsnNode(FLOAD, 1));
            newList.add(new VarInsnNode(ALOAD, 0));
            newList.add(new FieldInsnNode(GETFIELD, itemRendererClass, itemRendererMinecraftField, "L" + minecraftClass + ";"));
            newList.add(new VarInsnNode(ALOAD, 0));
            newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "renderItemInFirstPerson"
                    , "(FL" + minecraftClass + ";L" + itemRendererClass + ";)V"));
            mn.instructions.insertBefore(insnNode, newList);
        }
    }

    @Override
    boolean processMethods(List<MethodNode> methods) {
        int found = 0;
        for (MethodNode mn : methods) {
            if (mn.name.equals(renderItem1stPersonMethodName) && mn.desc.equals(renderItem1stPersonMethodDesc)) {
                processRenderItemMethod(mn);
                found++;
            } else if (mn.name.equals(updateEquippedItemMethodName) && mn.desc.equals(SIMPLEST_METHOD_DESC)) {
                processUpdateEquippedMethod(mn);
                found++;
            }
        }
        methods.add(methods.size(), generateSetter(itemRendererClass, "setItemToRender", "offHandItemToRender", "L" + itemStackClass + ";"));
        methods.add(methods.size(), generateSetter(itemRendererClass, "setEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(), generateSetter(itemRendererClass, "setEquippedProgress", "equippedOffHandProgress", "F"));
        methods.add(methods.size(), generateSetter(itemRendererClass, "setPrevEquippedProgress", "prevEquippedOffHandProgress", "F"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getItemToRender", "offHandItemToRender", "L" + itemStackClass + ";"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getEquippedProgress", "equippedOffHandProgress", "F"));
        methods.add(methods.size(), generateGetter(itemRendererClass, "getPrevEquippedProgress", "prevEquippedOffHandProgress", "F"));
        return found == 2;
    }

    @Override
    boolean processFields(List<FieldNode> fields) {
        logger.log(Level.INFO, "\tAdding new fields to ItemRenderer");
        fields.add(fields.size(), new FieldNode(ACC_PRIVATE, "offHandItemToRender", "L" + itemStackClass + ";", null, null));
        fields.add(fields.size(), new FieldNode(ACC_PRIVATE, "equippedItemOffhandSlot", "I", null, -1));
        fields.add(fields.size(), new FieldNode(ACC_PRIVATE, "equippedOffHandProgress", "F", null, 0F));
        fields.add(fields.size(), new FieldNode(ACC_PRIVATE, "prevEquippedOffHandProgress", "F", null, 0F));
        return true;
    }

    @Override
    void setupMappings() {
        itemStackClass = BattlegearTranslator.getMapedClassName("item.ItemStack");
        itemRendererClass = BattlegearTranslator.getMapedClassName("client.renderer.ItemRenderer");
        minecraftClass = BattlegearTranslator.getMapedClassName("client.Minecraft");

        itemRendererMinecraftField = BattlegearTranslator.getMapedFieldName("field_78455_a", "mc");

        renderItem1stPersonMethodName = BattlegearTranslator.getMapedMethodName("func_78440_a", "renderItemInFirstPerson");
        renderItem1stPersonMethodDesc = "(F)V";

        updateEquippedItemMethodName = BattlegearTranslator.getMapedMethodName("func_78441_a", "updateEquippedItem");
    }
}
