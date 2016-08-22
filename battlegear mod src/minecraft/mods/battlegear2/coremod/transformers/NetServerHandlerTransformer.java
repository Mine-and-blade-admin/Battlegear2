package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class NetServerHandlerTransformer extends TransformerBase {

    public NetServerHandlerTransformer() {
        super("net.minecraft.network.NetHandlerPlayServer");
        setDebug(true);
    }

    private String packet16BlockItemSwitchClassName;
    private String entityPlayerMPClassName;
    private String entityPlayerClassName;
    private String netServiceHandelerClassName;
    private String inventoryPlayerClassName;
    private String itemStackClassName;
    private String slotClassName;

    private String playerInventoryFieldName;
    private String getItemSwitchId;
    private String netServiceHandelerPlayerField;

    private String handleBlockSwitchMethodName;
    private String handleBlockSwitchMethodDesc;
    private String handlePlaceMethodName;
    private String handlePlaceMethodDesc;
    private String inventoryGetCurrentMethodName;
    private String inventoryGetCurrentMethodDesc;
    private String itemStackCopyStackMethodName;
    private String itemStackCopyStackMethodDesc;

    @Override
    boolean processMethods(List<MethodNode> methods) {
        int found = 0;
        for (MethodNode mn : methods) {
            if (mn.name.equals(handleBlockSwitchMethodName) &&
                    mn.desc.equals(handleBlockSwitchMethodDesc)) {
                if (processSwitchBlockMethod(mn))
                    found++;
            } else if (mn.name.equals(handlePlaceMethodName) &&
                    mn.desc.equals(handlePlaceMethodDesc)) {
                if (processPlaceMethod(mn))
                    found++;
            }
        }
        return found == 2;
    }

    private boolean processPlaceMethod(MethodNode mn) {
        sendPatchLog("processPlayerBlockPlacement");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        int fieldCount = 0;
        while (it.hasNext()) {
            AbstractInsnNode nextNode = it.next();

            if (nextNode instanceof FieldInsnNode &&
                    ((FieldInsnNode) nextNode).owner.equals(entityPlayerMPClassName) &&
                    ((FieldInsnNode) nextNode).name.equals(playerInventoryFieldName)) {
                fieldCount++;//count number of playerEntity.inventory use

                if (fieldCount == 3) {

                    while (it.hasNext() && nextNode.getOpcode() != ACONST_NULL) {//visit till pushing null onto stack
                        nextNode = it.next();
                    }

                    newList.add(nextNode);
                    newList.add(new MethodInsnNode(INVOKESTATIC,
                            UTILITY_CLASS,
                            "setPlayerCurrentItem",
                            "(L" + entityPlayerClassName +
                                    ";L" + itemStackClassName + ";)V"));
                    it.next();//BattlegearUtils.setPlayerCurrentItem(playerEntity, null);


                } else if (fieldCount == 4) {

                    while (it.hasNext() && nextNode.getOpcode() != AASTORE) {//visit till storing into array
                        nextNode = it.next();
                    }

                    //BattlegearUtils.setPlayerCurrentItem(playerEntity, ItemStack.copyItemStack(this.playerEntity.inventory.getCurrentItem().copy()));
                    newList.add(new VarInsnNode(ALOAD, 0));
                    newList.add(new FieldInsnNode(GETFIELD, netServiceHandelerClassName, netServiceHandelerPlayerField, "L" + entityPlayerMPClassName + ";"));
                    newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerInventoryFieldName, "L" + inventoryPlayerClassName + ";"));
                    newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, inventoryGetCurrentMethodName, inventoryGetCurrentMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC, itemStackClassName, itemStackCopyStackMethodName, itemStackCopyStackMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC,
                            UTILITY_CLASS,
                            "setPlayerCurrentItem", "(L" + entityPlayerClassName + ";L" + itemStackClassName + ";)V"));
                    String fml = FMLCommonHandler.instance().getModName();
                    // MCPC and Minecraft Forkage already add fixes for this, (but not Cauldron ?)
                    if (fml.contains("cauldron") || (!fml.contains("mcpc") && !Launch.blackboard.containsKey("IsForkage"))) {
                        int slotIndex = 0;
                        while (it.hasNext()) {
                            nextNode = it.next();
                            newList.add(nextNode);
                            if (nextNode instanceof VarInsnNode && nextNode.getOpcode() == ASTORE)
                                slotIndex = ((VarInsnNode) nextNode).var;
                            if (nextNode instanceof FieldInsnNode && nextNode.getOpcode() == PUTFIELD)
                                break;
                        }
                        newList.add(it.next());
                        nextNode = it.next();
                        newList.add(nextNode);
                        if (nextNode instanceof LineNumberNode && slotIndex != 0) {
                            int line = ((LineNumberNode) nextNode).line;
                            LabelNode L0 = new LabelNode();
                            // if(slot == null) return;
                            newList.add(new VarInsnNode(ALOAD, slotIndex));
                            newList.add(new JumpInsnNode(IFNONNULL, L0));
                            newList.add(new InsnNode(RETURN));
                            newList.add(L0);
                            newList.add(new LineNumberNode(line + 1, L0));
                            newList.add(new FrameNode(F_APPEND, 1, new Object[]{slotClassName}, 0, null));
                        } else
                            return false;
                    }

                } else {
                    newList.add(nextNode);
                }
            } else {
                newList.add(nextNode);
            }

        }

        mn.instructions = newList;
        return fieldCount > 4;
    }


    private boolean processSwitchBlockMethod(MethodNode mn) {
        sendPatchLog("processHeldItemChange");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();
        boolean done = false;
        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();
            newList.add(nextInsn);
            if (!done && nextInsn instanceof MethodInsnNode &&
                    nextInsn.getOpcode() == INVOKEVIRTUAL &&
                    ((MethodInsnNode) nextInsn).owner.equals(packet16BlockItemSwitchClassName) &&
                    ((MethodInsnNode) nextInsn).name.equals(getItemSwitchId)) {

                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/api/core/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));

                while (it.hasNext()) {
                    if (nextInsn instanceof JumpInsnNode && nextInsn.getOpcode() == IF_ICMPGE) {//"if int greater than or equal to" branch
                        newList.add(new JumpInsnNode(IFEQ, ((JumpInsnNode) nextInsn).label));//make "if equal" branch
                        done = true;
                        break;
                    }
                    nextInsn = it.next();
                }
            }
        }

        mn.instructions = newList;
        return done;
    }

    @Override
    boolean processFields(List<FieldNode> fields) {
        return true;
    }

    @Override
    void setupMappings() {
        netServiceHandelerClassName = BattlegearTranslator.getMapedClassName("network.NetHandlerPlayServer");
        packet16BlockItemSwitchClassName = BattlegearTranslator.getMapedClassName("network.play.client.C09PacketHeldItemChange");
        entityPlayerMPClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayerMP");
        inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
        slotClassName = BattlegearTranslator.getMapedClassName("inventory.Slot");

        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("field_71071_by", "inventory");
        netServiceHandelerPlayerField = BattlegearTranslator.getMapedFieldName("field_147369_b", "playerEntity");


        getItemSwitchId = BattlegearTranslator.getMapedMethodName("func_149614_c", "getSlotId");
        handleBlockSwitchMethodName = BattlegearTranslator.getMapedMethodName("func_147355_a", "processHeldItemChange");
        handleBlockSwitchMethodDesc = "(L" + packet16BlockItemSwitchClassName + ";)V";

        handlePlaceMethodName = BattlegearTranslator.getMapedMethodName("func_147346_a", "processPlayerBlockPlacement");
        handlePlaceMethodDesc = "(Lnet/minecraft/network/play/client/C08PacketPlayerBlockPlacement;)V";

        inventoryGetCurrentMethodName = BattlegearTranslator.getMapedMethodName("func_70448_g", "getCurrentItem");
        inventoryGetCurrentMethodDesc = "()L" + itemStackClassName + ";";

        itemStackCopyStackMethodName = BattlegearTranslator.getMapedMethodName("func_77944_b", "copyItemStack");
        itemStackCopyStackMethodDesc = "(L" + itemStackClassName + ";)L" + itemStackClassName + ";";

    }
}
