package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class ItemStackTransformer extends TransformerMethodProcess {
    private String entityPlayerClassName;
    private String itemStackClassName;
    private String destroyMethodName;

    public ItemStackTransformer() {
        super("net.minecraft.item.ItemStack", "func_77972_a", new String[]{"damageItem", "(ILnet/minecraft/entity/EntityLivingBase;)V"});
    }

    @Override
    void processMethod(MethodNode method) {
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = method.instructions.iterator();

        while (it.hasNext()) {
            AbstractInsnNode node = it.next();
            if (node instanceof MethodInsnNode && node.getOpcode() == INVOKEVIRTUAL) {
                MethodInsnNode methNode = (MethodInsnNode) node;
                if (methNode.owner.equals(entityPlayerClassName) && methNode.name.equals(destroyMethodName) && methNode.desc.equals(SIMPLEST_METHOD_DESC)) {
                    newList.add(new VarInsnNode(ALOAD, 0));
                    newList.add(new MethodInsnNode(INVOKESTATIC, UTILITY_CLASS, "onBowStackDepleted", "(L" + entityPlayerClassName + ";L" + itemStackClassName + ";)V"));
                    continue;
                }
            }
            newList.add(node);
        }
        method.instructions = newList;
    }

    @Override
    void setupMappings() {
        super.setupMappings();
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        destroyMethodName = BattlegearTranslator.getMapedMethodName("func_71028_bD", "destroyCurrentEquippedItem");
    }
}
