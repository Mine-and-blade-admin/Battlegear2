package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.List;

import mods.battlegear2.api.core.IOffhandRender;
import mods.battlegear2.api.core.BattlegearTranslator;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ItemRendererTransformer extends TransformerBase {

    public ItemRendererTransformer() {
		super("net.minecraft.client.renderer.ItemRenderer");
	}

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
    void addInterface(List<String> interfaces) {
        interfaces.add(Type.getInternalName(IOffhandRender.class));
    }

    private void processUpdateEquippedMethod(MethodNode mn) {
        sendPatchLog("updateEquippedItem");
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

        sendPatchLog("renderItemInFirstPerson");
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

	@Override
	boolean processMethods(List<MethodNode> methods) {
        int found = 0;
		for (MethodNode mn : methods) {
            if (mn.name.equals(renderItem1stPersonMethodName) &&
                    mn.desc.equals(renderItem1stPersonMethodDesc)) {
                processRenderItemMethod(mn);
                found++;
            } else if (mn.name.equals(updateEquippedItemMethodName) &&
                    mn.desc.equals(updateEquippedItemMethodDesc)) {
                processUpdateEquippedMethod(mn);
                found++;
            }
        }
        methods.add(methods.size(),generateSetter(itemRendererClass, "setItemToRender", "offHandItemToRender", "L" + itemStackClass + ";"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setEquippedProgress", "equippedOffHandProgress", "F"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setPrevEquippedProgress", "prevEquippedOffHandProgress", "F"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getItemToRender", "offHandItemToRender", "L" + itemStackClass + ";"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getEquippedProgress", "equippedOffHandProgress", "F"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getPrevEquippedProgress", "prevEquippedOffHandProgress", "F"));
        return found == 2;
	}

	@Override
	boolean processFields(List<FieldNode> fields) {
        logger.log(Level.INFO, "\tAdding new fields to ItemRenderer");
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "offHandItemToRender", "L" + itemStackClass + ";", null, null));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "equippedItemOffhandSlot", "I", null, 0));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "equippedOffHandProgress", "F", null, 0F));
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "prevEquippedOffHandProgress", "F", null, 0F));
        return true;
    }

	@Override
	void setupMappings() {
		itemStackClass = BattlegearTranslator.getMapedClassName("item.ItemStack");
        itemRendererClass = BattlegearTranslator.getMapedClassName("client.renderer.ItemRenderer");
        minecraftClass = BattlegearTranslator.getMapedClassName("client.Minecraft");

        itemRendererMinecraftField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78455_a", "mc");
        itemRendereriteToRenderField = BattlegearTranslator.getMapedFieldName("ItemRenderer", "field_78453_b", "itemToRender");

        renderItem1stPersonMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78440_a", "renderItemInFirstPerson");
        renderItem1stPersonMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78440_a", "(F)V");

        updateEquippedItemMethodName = BattlegearTranslator.getMapedMethodName("ItemRenderer", "func_78441_a", "updateEquippedItem");
        updateEquippedItemMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemRenderer", "func_78441_a", "()V");

	}
}
