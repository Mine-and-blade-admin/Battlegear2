package mods.battlegear2.coremod.transformers;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.fml.relauncher.IClassTransformer;

import static mods.battlegear2.coremod.BattlegearObNames.*;
import static org.objectweb.asm.Opcodes.*;

public class ModelBipedTransformer implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		if(name.equals(modelBipedClassName)){
			
			System.out.println("M&B - Patching Class ModelBiped ("+name+")");
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			
			cn.fields.add(0, new FieldNode(ACC_PUBLIC, "onGroundOffhand", "F", null, null));
			
			for (MethodNode  method: cn.methods) {
				if(method.name.equals("a") && method.desc.equals("(FFFFFFL"+entityClassName+";)V")){
					processRotationAnglesMethod(method);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class ModelBiped ("+name+") done");
		    
		    return cw.toByteArray();
			
		}else{
			return bytes;
		}
	}

	private void processRotationAnglesMethod(MethodNode method) {
		
		System.out.println("\tPatching Method setRotationAngles in ModelBiped");
		Iterator<AbstractInsnNode> it = method.instructions.iterator();
		
		InsnList newInsn = new InsnList();
		
		while(it.hasNext()){
			AbstractInsnNode nextInsn = it.next();
			
			if(nextInsn.getOpcode() == RETURN){
				
				newInsn.add(new VarInsnNode(ALOAD, 0));
				newInsn.add(new MethodInsnNode(INVOKESTATIC,
						"mods/battlegear2/client/utils/BattlegearRenderHelper",
						"moveOffHandArm", "(L"+modelBipedClassName+";)V"));
				
				
			}
			newInsn.add(nextInsn);
		}
		
	}

	

}
