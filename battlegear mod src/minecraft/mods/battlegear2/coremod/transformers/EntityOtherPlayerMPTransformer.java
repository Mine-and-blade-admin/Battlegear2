package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import mods.battlegear2.coremod.BattleGearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class EntityOtherPlayerMPTransformer implements IClassTransformer{

	private String inventoryPlayerClassName;
	private String itemStackClassName;
	private String entityOtherPlayerMPClassName;
	private String itemClassName;
	
	private String mainInventoryArrayFieldName;
	private String currentItemFieldName;
	
	private String getStackInSlotMethodName;
	private String getStackInSlotMethodDesc;
	private String onUpdateMethodName;
	private String onUpdateMethodDesc;
	private String setCurrentItemMethodName;
	private String setCurrentItemMethodDesc;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.client.entity.EntityOtherPlayerMP")){
			
			System.out.println("M&B - Patching Class EntityOtherPlayerMP ("+name+")");
			
			inventoryPlayerClassName = BattleGearTranslator.getMapedClassName("InventoryPlayer");
			itemStackClassName = BattleGearTranslator.getMapedClassName("ItemStack");
			entityOtherPlayerMPClassName = BattleGearTranslator.getMapedClassName("EntityOtherPlayerMP");
			itemClassName = BattleGearTranslator.getMapedClassName("Item");
			
			currentItemFieldName =
					BattleGearTranslator.getMapedFieldName("InventoryPlayer", "field_71185_c");
			mainInventoryArrayFieldName =
					BattleGearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a");
			
			getStackInSlotMethodName = 
					BattleGearTranslator.getMapedMethodName("InventoryPlayer", "func_70301_a");
			getStackInSlotMethodDesc = 
					BattleGearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70301_a");
			setCurrentItemMethodName =
					BattleGearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70062_b");
			setCurrentItemMethodDesc =
					BattleGearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70062_b");
			onUpdateMethodName =
					BattleGearTranslator.getMapedMethodName("EntityOtherPlayerMP", "func_70071_h_");
			onUpdateMethodDesc =
					BattleGearTranslator.getMapedMethodDesc("EntityOtherPlayerMP", "func_70071_h_");
			
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			System.out.println(onUpdateMethodName);
			System.out.println(onUpdateMethodDesc);
			
			for(MethodNode mn : cn.methods){
				
				if(mn.name.equals(setCurrentItemMethodName) &&
						mn.desc.equals(setCurrentItemMethodDesc)){
					processSetCurrentItemMethod(mn);
				}else if(mn.name.equals(onUpdateMethodName) &&
						mn.desc.equals(onUpdateMethodDesc)){
					processOnUpdateMethod(mn);
				}
				
			}
			
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class EntityOtherPlayerMP done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
			return cw.toByteArray();
			
		}else{
			return bytes;
		}
}

	private void processOnUpdateMethod(MethodNode mn) {
		
		System.out.println("\tPatching method onUpdate in EntityOtherPlayerMP");
		InsnList newList = new InsnList();
		
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();
		
		System.out.println(inventoryPlayerClassName +" - "+mainInventoryArrayFieldName);
		
		while(it.hasNext()){
			AbstractInsnNode node = it.next();
			
			if(node instanceof FieldInsnNode &&
					node.getOpcode() == GETFIELD &&
					((FieldInsnNode) node).owner.equals(inventoryPlayerClassName) &&
					((FieldInsnNode) node).name.equals(mainInventoryArrayFieldName)){
				//Remove
				
				System.out.println("Remove");
			}else if(node.getOpcode() == AALOAD &&
					node.getNext() instanceof JumpInsnNode && 
					node.getNext().getOpcode() == IFNULL){
				System.out.println("Change");
				newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, 
						getStackInSlotMethodName, getStackInSlotMethodDesc));
			}else if(node.getOpcode() == AALOAD &&
					node.getNext() instanceof VarInsnNode &&
					node.getNext().getOpcode() == ASTORE){
				System.out.println("Change");
				newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, 
						getStackInSlotMethodName, getStackInSlotMethodDesc));
			}else if (node instanceof FieldInsnNode &&
					((FieldInsnNode)node).owner.equals(itemClassName) &&
					((FieldInsnNode)node).desc.startsWith("[") &&
					node.getOpcode() == GETSTATIC){
				
				AbstractInsnNode node2 = node.getPrevious();
				while(node2.getOpcode() != ASTORE){
					AbstractInsnNode nodeTemp = node2.getPrevious();
					newList.remove(node2);
					node2 = nodeTemp;
				}
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new VarInsnNode(ALOAD, 6));
				System.out.println("Delete Lots");
			}else{
				newList.add(node);
			}
		}
		
		mn.instructions = newList;
		
	}

	private void processSetCurrentItemMethod(MethodNode mn) {
		System.out.println("\tPatching method setCurrentItem in EntityOtherPlayerMP");
	}
}
