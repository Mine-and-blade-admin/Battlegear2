package mods.battlegear2.coremod.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.IClassTransformer;
import static mods.battlegear2.coremod.BattlegearObNames.*;
import static org.objectweb.asm.Opcodes.ASM4;

public class NetClientHandlerTransformer implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(name.equals(netClientHandlerClassName)){
			
			System.out.println("M&B - Patching Class NetClientHandler ("+name+")");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			for (MethodNode method: cn.methods) {
				if(method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
						method.desc.equals("L"+packet20NamedEntitySpawnClassName+";")){
					System.out.println("\tPatching method handleNamedEntitySpawn in NetClientHandler");
					TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class NetClientHandler ("+name+") done");

			return cw.toByteArray();
			
		}else{
			return bytes;
		}
		
	}

}
