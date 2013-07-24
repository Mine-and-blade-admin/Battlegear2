package assets.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ASM4;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

public class EntityAIControlledByPlayerTransformer implements IClassTransformer{

	
	private String entityPlayerClassName;
	
	private String playerInventoryFieldName;
	
	private String updateTaskMethodName;
	private String updateTaskMethodDesc;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.entity.ai.EntityAIControlledByPlayer")){
			
			System.out.println("M&B - Patching Class EntityAIControlledByPlayer ("+name+")");
			
			
			updateTaskMethodName = 
					BattleGearTranslator.getMapedMethodName("EntityAIControlledByPlayer", "func_75246_d");
			updateTaskMethodDesc = 
					BattleGearTranslator.getMapedMethodDesc("EntityAIControlledByPlayer", "func_75246_d");
			
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			for(MethodNode mn: cn.methods){
				if(mn.name.equals(updateTaskMethodName) &&
						mn.desc.equals(updateTaskMethodDesc)){
					System.out.println("\tPatching method updateTask in EntityAIControlledByPlayer");
									
					TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 3, 3);
				}
			}
			
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class EntityAIControlledByPlayer done");

		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }

			return cw.toByteArray();
		}else{
			return bytes;
		}
	}

}
