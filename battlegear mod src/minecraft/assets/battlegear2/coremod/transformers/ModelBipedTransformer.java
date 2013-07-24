package assets.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

public class ModelBipedTransformer implements IClassTransformer{
	
	private String modelBipedClassName;
	private String entityClassName;
	
	private String setRotationAngleMethodName;
	private String setRotationAngleMethodDesc;
	

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		if(transformedName.equals("net.minecraft.client.model.ModelBiped")){
			
			modelBipedClassName = BattleGearTranslator.getMapedClassName("ModelBiped");
			entityClassName = BattleGearTranslator.getMapedClassName("Entity");
			
			setRotationAngleMethodName = BattleGearTranslator.getMapedMethodName("ModelBiped", "func_78087_a");
			setRotationAngleMethodDesc = BattleGearTranslator.getMapedMethodDesc("ModelBiped", "func_78087_a");
			
						
			System.out.println("M&B - Patching Class ModelBiped ("+name+")");
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			
			cn.fields.add(0, new FieldNode(ACC_PUBLIC, "onGroundOffhand", "F", null, null));
			
			for (MethodNode  method: cn.methods) {
				if(method.name.equals(setRotationAngleMethodName) && 
						method.desc.equals(setRotationAngleMethodDesc)){
					
					processRotationAnglesMethod(method);
					
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class ModelBiped ("+name+") done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
		    
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
