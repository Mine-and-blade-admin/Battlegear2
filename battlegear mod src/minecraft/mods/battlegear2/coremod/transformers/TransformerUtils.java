package mods.battlegear2.coremod.transformers;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Iterator;

import mods.battlegear2.coremod.BattleGearTranslator;

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
					((FieldInsnNode)nextNode).owner.equals(
							BattleGearTranslator.getMapedClassName("EntityPlayer")) &&
					((FieldInsnNode)nextNode).name.equals(
							BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by"))
					){
				
				//skip the next four
				it.next();
				it.next();
				it.next();
				it.next();
				
				
			}else if(nextNode instanceof InsnNode && nextNode.getOpcode() == AASTORE){
				
				newList.add(new MethodInsnNode(INVOKESTATIC, 
						"mods/battlegear2/common/utils/BattlegearUtils", 
						"setPlayerCurrentItem", 
						"(L"+BattleGearTranslator.getMapedClassName("EntityPlayer")+
						";L"+BattleGearTranslator.getMapedClassName("ItemStack")+";)V"));
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
