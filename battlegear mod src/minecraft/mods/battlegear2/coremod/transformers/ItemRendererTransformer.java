package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.api.core.IOffhandRender;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public class ItemRendererTransformer extends TransformerBase {


    public ItemRendererTransformer() {
		super("net.minecraft.client.renderer.ItemRenderer");
	}

	private String itemStackClass;
    private String itemRendererClass;
    private String minecraftClass;

    private String itemRendererMinecraftField;
    private String itemRendereriteToRenderField;
    private String offhandToRenderField;
    private String offhandProgressField;
    private String offhandPrevProgressField;

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
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearRenderHelper", "updateEquippedItem"
                        , "(L" + itemRendererClass + ";L" + minecraftClass + ";)V"));
            }

            newList.add(insn);
        }

        mn.instructions = newList;
    }

	@Override
	boolean processMethods(List<MethodNode> methods) {
        int found = 0;
		for (MethodNode mn : methods) {
            if (mn.name.equals(updateEquippedItemMethodName) &&
                    mn.desc.equals(updateEquippedItemMethodDesc)) {
                processUpdateEquippedMethod(mn);
                found++;
            }
        }
        methods.add(methods.size(),generateSetter(itemRendererClass, "setItemToRender", offhandToRenderField, "L" + itemStackClass + ";"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setEquippedProgress", offhandProgressField, "F"));
        methods.add(methods.size(),generateSetter(itemRendererClass, "setPrevEquippedProgress", offhandPrevProgressField, "F"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getItemToRender", offhandToRenderField, "L" + itemStackClass + ";"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getEquippedItemSlot", "equippedItemOffhandSlot", "I"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getEquippedProgress", offhandProgressField, "F"));
        methods.add(methods.size(),generateGetter(itemRendererClass, "getPrevEquippedProgress", offhandPrevProgressField, "F"));
        return found == 1;
	}

	@Override
	boolean processFields(List<FieldNode> fields) {
        logger.log(Level.INFO, "\tAdding new fields to ItemRenderer");
        fields.add(fields.size(), new FieldNode(ACC_PUBLIC, "equippedItemOffhandSlot", "I", null, 0));
        return true;
    }

	@Override
	void setupMappings() {
		itemStackClass = BattlegearTranslator.getMapedClassName("item.ItemStack");
        itemRendererClass = BattlegearTranslator.getMapedClassName("client.renderer.ItemRenderer");
        minecraftClass = BattlegearTranslator.getMapedClassName("client.Minecraft");

        itemRendererMinecraftField = BattlegearTranslator.getMapedFieldName("field_78455_a", "mc");
        itemRendereriteToRenderField = BattlegearTranslator.getMapedFieldName("field_187467_d", "itemStackMainHand");
        offhandToRenderField = BattlegearTranslator.getMapedFieldName("field_187468_e", "itemStackOffHand");
        offhandProgressField = BattlegearTranslator.getMapedFieldName("field_187471_h", "equippedProgressOffHand");
        offhandPrevProgressField = BattlegearTranslator.getMapedFieldName("field_187472_i", "prevEquippedProgressOffHand");

        updateEquippedItemMethodName = BattlegearTranslator.getMapedMethodName("func_78441_a", "updateEquippedItem");
        updateEquippedItemMethodDesc = "()V";
	}
}
