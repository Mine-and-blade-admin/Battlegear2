package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;
import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class EntityOtherPlayerMPTransformer extends TransformerBase {

    public EntityOtherPlayerMPTransformer() {
		super("net.minecraft.client.entity.EntityOtherPlayerMP");
	}

	private String inventoryPlayerClassName;
    private String itemStackClassName;
    private String entityOtherPlayerMPClassName;
    private String itemClassName;

    private String mainInventoryArrayFieldName;
    private String currentItemFieldName;
    private String playerInventoryFieldName;

    private String getStackInSlotMethodName;
    private String getStackInSlotMethodDesc;
    private String onUpdateMethodName;
    private String onUpdateMethodDesc;
    private String setCurrentItemMethodName;
    private String setCurrentItemMethodDesc;
    private String isItemInUseFieldName;
    private String limbSwingFieldName;
    
    @Deprecated
    private void processOnUpdateMethod(MethodNode mn) {
    	sendPatchLog("onUpdate");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        boolean done = false;
        while(it.hasNext() && !done){
            AbstractInsnNode node = it.next();
            if (!done && node instanceof FieldInsnNode &&
                    node.getOpcode() == GETFIELD &&
                    ((FieldInsnNode) node).owner.equals(entityOtherPlayerMPClassName) &&
                    ((FieldInsnNode) node).name.equals(isItemInUseFieldName)){
            	
            	newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, playerInventoryFieldName, "L"+inventoryPlayerClassName+";"));
            	newList.add(new VarInsnNode(ALOAD, 0));
            	newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, playerInventoryFieldName, "L"+inventoryPlayerClassName+";"));
            	newList.add(new FieldInsnNode(GETFIELD, inventoryPlayerClassName, currentItemFieldName, "I"));
            	newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, getStackInSlotMethodName, getStackInSlotMethodDesc));
                newList.add(new VarInsnNode(ASTORE, 6));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(node);
                int A_Load_Count = 0;
                
                while (it.hasNext() && !done) {
                	node = it.next();
                	if(node instanceof VarInsnNode && 
                			node.getOpcode() == ALOAD){
                		A_Load_Count ++;
                		if(A_Load_Count == 2){
                    		boolean found_AALoad = false;
                    		while(it.hasNext() && !found_AALoad){
                    			node = it.next();
                    			found_AALoad = node.getOpcode() == AALOAD;
                    		}
                    		newList.add(new VarInsnNode(ALOAD, 6));
                    	}else if (A_Load_Count == 3){
                    		newList.add(node);
                    		int AALoad_count = 0;
                    		while(it.hasNext() && AALoad_count < 2){
                    			node = it.next();
                    			if(node.getOpcode() == AALOAD){
                    				AALoad_count++;
                    			}
                    		}
                    		newList.add(new VarInsnNode(ALOAD, 6));
                    		done=true;
                    	}else{
                    		newList.add(node);
                    	}
                	}else{
                		newList.add(node);
                	}
				}
            }else{
            	newList.add(node);
            }
        }
        while(it.hasNext()){
        	newList.add(it.next());
        }
        mn.instructions = newList;
    }

    private void processOnUpdateMethod2(MethodNode mn) {

    	sendPatchLog("onUpdate");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        boolean done = false;
        while(it.hasNext() && !done){
            AbstractInsnNode node = it.next();
            if (node instanceof FieldInsnNode &&
                    node.getOpcode() == PUTFIELD &&
                    ((FieldInsnNode) node).owner.equals(entityOtherPlayerMPClassName) &&
                    ((FieldInsnNode) node).name.equals(limbSwingFieldName)){
                newList.add(node);
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 0));

                newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, isItemInUseFieldName, "Z"));
                newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/client/utils/BattlegearClientUtils", "entityOtherPlayerIsItemInUseHook", "(L"+entityOtherPlayerMPClassName+";Z)Z"));
                newList.add(new FieldInsnNode(PUTFIELD, entityOtherPlayerMPClassName, isItemInUseFieldName, "Z"));

                node = it.next();
                while(!(node instanceof InsnNode && node.getOpcode() == RETURN)){
                	node = it.next();
                }
            	newList.add(node);
                while(it.hasNext()){
                	node = it.next();
                	newList.add(node);
                }

                done = true;
            }else{
                newList.add(node);
            }

        }
        mn.instructions = newList;



        /*

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        System.out.println(inventoryPlayerClassName + " - " + mainInventoryArrayFieldName);

        while (it.hasNext()) {
            AbstractInsnNode node = it.next();

            if (node instanceof FieldInsnNode &&
                    node.getOpcode() == GETFIELD &&
                    ((FieldInsnNode) node).owner.equals(inventoryPlayerClassName) &&
                    ((FieldInsnNode) node).packetName.equals(mainInventoryArrayFieldName)) {
                //Remove

                System.out.println("Remove");
            } else if (node.getOpcode() == AALOAD &&
                    node.getNext() instanceof JumpInsnNode &&
                    node.getNext().getOpcode() == IFNULL) {
                System.out.println("Change");
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName,
                        getStackInSlotMethodName, getStackInSlotMethodDesc));
            } else if (node.getOpcode() == AALOAD &&
                    node.getNext() instanceof VarInsnNode &&
                    node.getNext().getOpcode() == ASTORE) {
                System.out.println("Change");
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName,
                        getStackInSlotMethodName, getStackInSlotMethodDesc));
            } else if (node instanceof FieldInsnNode &&
                    ((FieldInsnNode) node).owner.equals(itemClassName) &&
                    ((FieldInsnNode) node).desc.startsWith("[") &&
                    node.getOpcode() == GETSTATIC) {

                AbstractInsnNode node2 = node.getPrevious();
                while (node2.getOpcode() != ASTORE) {
                    AbstractInsnNode nodeTemp = node2.getPrevious();
                    newList.remove(node2);
                    node2 = nodeTemp;
                }
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new VarInsnNode(ALOAD, 6));
                System.out.println("Delete Lots");
            } else {
                newList.add(node);
            }
        }

        */

    }

    private void processSetCurrentItemMethod(MethodNode mn) {
        sendPatchLog("setCurrentItem");
        replaceInventoryArrayAccess(mn, entityOtherPlayerMPClassName, playerInventoryFieldName, 4,3,3);
    }

	@Override
	void processMethods(List<MethodNode> methods) {
		for (MethodNode mn : methods) {
            if (mn.name.equals(setCurrentItemMethodName) &&
                    mn.desc.equals(setCurrentItemMethodDesc)) {
                processSetCurrentItemMethod(mn);
            }

            if (mn.name.equals(onUpdateMethodName) &&
                    mn.desc.equals(onUpdateMethodDesc)) {
                processOnUpdateMethod2(mn);
            }
        }
	}

	@Override
	void processFields(List<FieldNode> fields) {
		
	}

	@Override
	void setupMappings() {
		inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer");
        itemStackClassName = BattlegearTranslator.getMapedClassName("item.ItemStack");
        entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("client.entity.EntityOtherPlayerMP");
        itemClassName = BattlegearTranslator.getMapedClassName("item.Item");

        isItemInUseFieldName = BattlegearTranslator.getMapedFieldName("EntityOtherPlayerMP", "field_71186_a", "isItemInUse");
        limbSwingFieldName = BattlegearTranslator.getMapedFieldName("EntityLivingBase", "field_70754_ba", "limbSwing");

        currentItemFieldName =
                BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70461_c", "currentItem");
        mainInventoryArrayFieldName =
                BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a", "mainInventory");
        playerInventoryFieldName =
                BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by", "inventory");


        getStackInSlotMethodName =
                BattlegearTranslator.getMapedMethodName("InventoryPlayer", "func_70301_a", "getStackInSlot");
        getStackInSlotMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70301_a", "(I)L"+itemStackClassName);
        setCurrentItemMethodName =
                BattlegearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70062_b", "setCurrentItemOrArmor");
        setCurrentItemMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70062_b", "(IL"+itemStackClassName+";)V");
        onUpdateMethodName =
                BattlegearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70071_h_", "onUpdate");
        onUpdateMethodDesc =
                BattlegearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70071_h_", "()V");
	}
}
