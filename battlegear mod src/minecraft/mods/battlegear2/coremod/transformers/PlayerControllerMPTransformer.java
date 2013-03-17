package mods.battlegear2.coremod.transformers;

import static mods.battlegear2.coremod.BattlegearObNames.entityOtherPlayerMPClassName;
import static mods.battlegear2.coremod.BattlegearObNames.netClientHandlerClassName;
import static mods.battlegear2.coremod.BattlegearObNames.netClientHandlerHandleNamedEntitySpawnMethodName;
import static mods.battlegear2.coremod.BattlegearObNames.packet20NamedEntitySpawnClassName;
import static mods.battlegear2.coremod.BattlegearObNames.playerInventoryFieldName;
import static org.objectweb.asm.Opcodes.ASM4;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.IClassTransformer;
import static mods.battlegear2.coremod.BattlegearObNames.*;

public class PlayerControllerMPTransformer implements IClassTransformer{
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(name.equals(playerControllerMPClassName)){
			
			System.out.println("M&B - Patching Class PlayerControllerMP ("+name+")");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			
			for (MethodNode method: cn.methods) {
				if(method.name.equals(playerControllerMPsendUseItemMethodName) &&
						method.desc.equals("L"+entityPlayerClassName+";"+worldClassName+";"+itemStackClassName+";)Z")){
					System.out.println("\tPatching method sendUseItem in PlayerControllerMP");
					TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class PlayerControllerMP ("+name+") done");

			return cw.toByteArray();
			
		}else{
			return bytes;
		}
		
	}

}
