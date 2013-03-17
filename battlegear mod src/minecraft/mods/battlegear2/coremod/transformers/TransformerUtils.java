package mods.battlegear2.coremod.transformers;

import static mods.battlegear2.coremod.BattlegearObNames.entityPlayerClassName;
import static mods.battlegear2.coremod.BattlegearObNames.inventoryClassName;
import static mods.battlegear2.coremod.BattlegearObNames.itemStackClassName;
import static mods.battlegear2.coremod.BattlegearObNames.playerInventoryFieldName;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Iterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformerUtils {

	public static MethodNode replaceInventoryArrayAccess(MethodNode method, String className, String fieldName , int maxStack, int maxLocal){
		
		InsnList newList = new InsnList();
		
		Iterator<AbstractInsnNode> it = method.instructions.iterator();
		
		while(it.hasNext()){
			AbstractInsnNode nextNode = it.next();
			
			if(nextNode instanceof FieldInsnNode && 
					((FieldInsnNode)nextNode).owner.equals(entityPlayerClassName) &&
					((FieldInsnNode)nextNode).name.equals(playerInventoryFieldName) &&
					((FieldInsnNode)nextNode).desc.equals("L"+inventoryClassName+";")){
				
				//skip the next four
				it.next();
				it.next();
				it.next();
				it.next();
				
				
			}else if(nextNode instanceof InsnNode && nextNode.getOpcode() == AASTORE){
				
				newList.add(new MethodInsnNode(INVOKESTATIC, 
						"mods/battlegear2/common/utils/BattlegearUtils", 
						"setPlayerCurrentItem", 
						"(L"+entityPlayerClassName+";L"+itemStackClassName+";)V"));
			}else{
				newList.add(nextNode);
			}
		}
		
		
		
		method.instructions = newList;
		
		method.maxStack = maxStack;
		method.maxLocals = maxLocal;
		
		return method;
		
	}
	
}
