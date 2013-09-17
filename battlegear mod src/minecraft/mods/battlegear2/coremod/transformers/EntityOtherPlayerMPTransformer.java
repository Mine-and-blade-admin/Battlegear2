package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class EntityOtherPlayerMPTransformer implements IClassTransformer {

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

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.entity.EntityOtherPlayerMP")) {

            System.out.println("M&B - Patching Class EntityOtherPlayerMP (" + name + ")");

            inventoryPlayerClassName = BattlegearTranslator.getMapedClassName("InventoryPlayer");
            itemStackClassName = BattlegearTranslator.getMapedClassName("ItemStack");
            entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityOtherPlayerMP");
            itemClassName = BattlegearTranslator.getMapedClassName("Item");

            isItemInUseFieldName = BattlegearTranslator.getMapedFieldName("EntityOtherPlayerMP", "field_71186_a");
            limbSwingFieldName = BattlegearTranslator.getMapedFieldName("EntityLivingBase", "field_70754_ba");

            currentItemFieldName =
                    BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_71185_c");
            mainInventoryArrayFieldName =
                    BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a");
            playerInventoryFieldName =
                    BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");


            getStackInSlotMethodName =
                    BattlegearTranslator.getMapedMethodName("InventoryPlayer", "func_70301_a");
            getStackInSlotMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70301_a");
            setCurrentItemMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70062_b");
            setCurrentItemMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70062_b");
            onUpdateMethodName =
                    BattlegearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70071_h_");
            onUpdateMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70071_h_");

            System.out.println(onUpdateMethodName);
            System.out.println(onUpdateMethodDesc);


            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);


            for (Object mnObj : cn.methods) {
                MethodNode mn = (MethodNode)mnObj;

                if (mn.name.equals(setCurrentItemMethodName) &&
                        mn.desc.equals(setCurrentItemMethodDesc)) {
                    processSetCurrentItemMethod(mn);
                }

                if (mn.name.equals(onUpdateMethodName) &&
                        mn.desc.equals(onUpdateMethodDesc)) {
                    processOnUpdateMethod(mn);
                }

            }


            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class EntityOtherPlayerMP done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, transformedName.substring(transformedName.lastIndexOf('.')+1)+" ("+name+")");
            }
            return cw.toByteArray();

        } else {
            return bytes;
        }
    }
    
    
    private void processOnUpdateMethod(MethodNode mn) {

        System.out.println("\tPatching method onUpdate in EntityOtherPlayerMP");
        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = mn.instructions.iterator();

        boolean done = false;
        while(it.hasNext() && !done){
            AbstractInsnNode node = it.next();
            if (!done && node instanceof FieldInsnNode &&
                    node.getOpcode() == GETFIELD &&
                    ((FieldInsnNode) node).owner.equals(entityOtherPlayerMPClassName) &&
                    ((FieldInsnNode) node).name.equals(isItemInUseFieldName)){
            	
            	//TODO replace with dynamic name
            	newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, "bn", "Luc;"));
            	newList.add(new VarInsnNode(ALOAD, 0));
            	newList.add(new FieldInsnNode(GETFIELD, entityOtherPlayerMPClassName, "bn", "Luc;"));
            	newList.add(new FieldInsnNode(GETFIELD, "uc", "c", "I"));
            	newList.add(new MethodInsnNode(INVOKEVIRTUAL, "uc", "a", "(I)Lyd;"));
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
        mn.maxLocals = mn.maxLocals;
    }

    private void processOnUpdateMethod2(MethodNode mn) {

        System.out.println("\tPatching method onUpdate in EntityOtherPlayerMP");
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

                newList.add(new InsnNode(RETURN));

                done = true;
            }else{
                newList.add(node);
            }

        }
        mn.instructions = newList;
        mn.maxLocals = mn.maxLocals;



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
        System.out.println("\tPatching method setCurrentItem in EntityOtherPlayerMP");
        TransformerUtils.replaceInventoryArrayAccess(mn, entityOtherPlayerMPClassName, playerInventoryFieldName, 4,3,3);



    }
}
