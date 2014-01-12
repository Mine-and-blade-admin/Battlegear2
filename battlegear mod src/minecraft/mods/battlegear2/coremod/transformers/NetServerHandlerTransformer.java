package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.*;

import cpw.mods.fml.common.FMLCommonHandler;

public class NetServerHandlerTransformer extends TransformerBase {

    public NetServerHandlerTransformer() {
		super("net.minecraft.network.NetServerHandler");
	}

	private String packet16BlockItemSwitchClassName;
    private String entityPlayerMPClassName;
    private String entityPlayerClassName;
    private String netServiceHandelerClassName;
    private String inventoryPlayerClassName;
    private String itemStackClassName;

    private String playerInventoryFieldName;
    private String packet16BlockItemSwitchId;
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
                processSwitchBlockMethod(mn);
                found++;
            } else if (mn.name.equals(handlePlaceMethodName) &&
                    mn.desc.equals(handlePlaceMethodDesc)) {
                processPlaceMethod(mn);
                found++;
            }
        }
        return found == 2;
    }

    private void processPlaceMethod(MethodNode mn) {
        sendPatchLog("handlePlace");
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
                            "mods/battlegear2/utils/BattlegearUtils",
                            "setPlayerCurrentItem",
                            "(L" + entityPlayerClassName +
                                    ";L" + itemStackClassName + ";)V"));
                    it.next();//BattlegearUtils.setPlayerCurrentItem(playerEntity, null);


                } else if (fieldCount == 4) {

                    while (it.hasNext() && nextNode.getOpcode() != AASTORE) {//visit till storing into array
                        nextNode = it.next();
                    }

//BattlegearUtils.setPlayerCurrentItem(playerEntity, ItemStack.copyItemStack(this.playerEntity.inventory.getCurrentItem()));
                    newList.add(new VarInsnNode(ALOAD, 0));
                    newList.add(new FieldInsnNode(GETFIELD, netServiceHandelerClassName, netServiceHandelerPlayerField, "L" + entityPlayerMPClassName + ";"));
                    newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerInventoryFieldName, "L" + inventoryPlayerClassName + ";"));
                    newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, inventoryGetCurrentMethodName, inventoryGetCurrentMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC, itemStackClassName, itemStackCopyStackMethodName, itemStackCopyStackMethodDesc));
                    newList.add(new MethodInsnNode(INVOKESTATIC,
                            "mods/battlegear2/utils/BattlegearUtils",
                            "setPlayerCurrentItem", "(L" + entityPlayerClassName + ";L" + itemStackClassName + ";)V"));
                    
                    if(!FMLCommonHandler.instance().getModName().contains("mcpc")){//MCPC already adds a fix for this
	                    while(it.hasNext() && ! (nextNode instanceof JumpInsnNode && nextNode.getOpcode()==IFNE)){
	                    	nextNode = it.next();
	                    	newList.add(nextNode);
	                    }
	                    newList.add(new VarInsnNode(ALOAD, 9));
	                    newList.add(new JumpInsnNode(IFNULL, ((JumpInsnNode) nextNode).label));
                    }

                } else {
                    newList.add(nextNode);
                }
            } else {
                newList.add(nextNode);
            }

        }

        mn.instructions = newList;
    }


    private void processSwitchBlockMethod(MethodNode mn) {
        sendPatchLog("handleBlockItemSwitch");
        InsnList newList = new InsnList();
        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        while (it.hasNext()) {
            AbstractInsnNode nextInsn = it.next();
            newList.add(nextInsn);

            if (nextInsn instanceof FieldInsnNode &&
                    nextInsn.getOpcode() == GETFIELD &&
                    ((FieldInsnNode) nextInsn).owner.equals(packet16BlockItemSwitchClassName) &&
                    ((FieldInsnNode) nextInsn).name.equals(packet16BlockItemSwitchId)) {

                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/inventory/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));

                nextInsn = it.next();
                while (it.hasNext() &&
                        (!(nextInsn instanceof JumpInsnNode)
                                || !(nextInsn.getOpcode() == IF_ICMPGE))) {//"if int greater than or equal to" branch

                    nextInsn = it.next();

                }
                newList.add(new JumpInsnNode(IFEQ, ((JumpInsnNode) nextInsn).label));//make "if equal" branch

                while (it.hasNext()) {
                    nextInsn = it.next();
                    newList.add(nextInsn);
                }

            }
        }

        mn.instructions = newList;

    }

	@Override
	boolean processFields(List<FieldNode> fields) {
		return true;
	}

	@Override
	void setupMappings() {
		netServiceHandelerClassName = BattlegearTranslator.getMapedClassName("network.NetServerHandler");
        packet16BlockItemSwitchClassName = BattlegearTranslator.getMapedClassName("network.packet.Packet16BlockItemSwitch");
        entityPlayerMPClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayerMP");
        inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");

        packet16BlockItemSwitchId = BattlegearTranslator.getMapedFieldName("Packet16BlockItemSwitch", "field_73386_a", "id");
        playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by", "inventory");
        netServiceHandelerPlayerField = BattlegearTranslator.getMapedFieldName("NetServerHandler", "field_72574_e", "playerEntity");


        handleBlockSwitchMethodName = BattlegearTranslator.getMapedMethodName("NetServerHandler", "func_72502_a", "handleBlockItemSwitch");
        handleBlockSwitchMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetServerHandler", "func_72502_a", "(L"+packet16BlockItemSwitchClassName+";)V");

        handlePlaceMethodName = BattlegearTranslator.getMapedMethodName("NetServerHandler", "func_72472_a", "handlePlace");
        handlePlaceMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetServerHandler", "func_72472_a", "(Lnet/minecraft/network/packet/Packet15Place;)V");

        inventoryGetCurrentMethodName = BattlegearTranslator.getMapedMethodName("InventoryPlayer", "func_70448_g", "getCurrentItem");
        inventoryGetCurrentMethodDesc = BattlegearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70448_g", "()L"+itemStackClassName+";");

        itemStackCopyStackMethodName = BattlegearTranslator.getMapedMethodName("ItemStack", "func_77944_b", "copyItemStack");
        itemStackCopyStackMethodDesc = BattlegearTranslator.getMapedMethodDesc("ItemStack", "func_77944_b", "(L"+itemStackClassName+";)L"+itemStackClassName+";");

	}
}
