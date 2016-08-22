package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class ItemInWorldTransformer extends TransformerMethodProcess {

    public ItemInWorldTransformer() {
        super("net.minecraft.server.management.ItemInWorldManager", "func_73085_a", new String[]{"tryUseItem", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"});
    }

    private String entityPlayerClassName;
    private String inventoryPlayerClassName;
    private String itemStackClassName;

    private String playerInventoryFieldName;
    private String mainInventoryArrayFieldName;

    private String setInventorySlotMethodName;
    private String setInventorySlotMethodDesc;

    @Override
    void processMethod(MethodNode mn) {
        sendPatchLog("tryUseItem");
        replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 3, 7);

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
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, setInventorySlotMethodName, setInventorySlotMethodDesc));
            } else {
                newList.add(node);
            }
        }

        mn.instructions = newList;
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");

        playerInventoryFieldName =
                BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");
        mainInventoryArrayFieldName =
                BattlegearTranslator.getMapedFieldName("field_70462_a", "mainInventory");

        setInventorySlotMethodName =
                BattlegearTranslator.getMapedMethodName("func_70299_a", "setInventorySlotContents");
        setInventorySlotMethodDesc = "(IL" + itemStackClassName + ";)V";
    }

}