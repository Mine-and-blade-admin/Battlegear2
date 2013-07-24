package assets.battlegear2.coremod.transformers;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import assets.battlegear2.coremod.BattleGearTranslator;

public class TransformerUtils {

	public static MethodNode replaceInventoryArrayAccess(MethodNode method, String className, String fieldName, int maxStack, int maxLocal){
		return replaceInventoryArrayAccess(method, className, fieldName, 4, maxStack, maxLocal);
	}
	
	public static MethodNode replaceInventoryArrayAccess(MethodNode method, String className, String fieldName , int todelete, int maxStack, int maxLocal){
		
		InsnList newList = new InsnList();
		
		Iterator<AbstractInsnNode> it = method.instructions.iterator();
		
		while(it.hasNext()){
			AbstractInsnNode nextNode = it.next();
			
			if(nextNode instanceof FieldInsnNode && 
					nextNode.getNext() instanceof FieldInsnNode &&
					((FieldInsnNode)nextNode).owner.equals(className) &&
					((FieldInsnNode)nextNode).name.equals(fieldName) &&
					((FieldInsnNode)nextNode.getNext()).owner.equals(BattleGearTranslator.getMapedClassName("InventoryPlayer")) &&
					((FieldInsnNode)nextNode.getNext()).name.equals(BattleGearTranslator.getMapedFieldName("InventoryPlayer","field_70462_a"))
					){
				
				//skip the next 4
				for(int i = 0; i < todelete; i++){
					nextNode = it.next();
				}
				//add all until the AAStore
				nextNode = it.next();
				while(it.hasNext() && nextNode.getOpcode() != AASTORE){
					newList.add(nextNode);
					nextNode = it.next();
				}
				
				//Add New
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

	public static void writeClassFile(ClassWriter cw, String name) {
		try{
			File outDir = new File(System.getProperty("user.home"), "bg classFiles"+File.separator+"final");
			System.out.println(outDir.getAbsolutePath());
			outDir.mkdirs();			
			 DataOutputStream dout=new DataOutputStream(new FileOutputStream(new File(outDir,name+".class")));
	         dout.write(cw.toByteArray());
	         dout.flush();
	         dout.close();
	         
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
