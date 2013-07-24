package assets.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

public class RenderPlayerTransformer implements IClassTransformer{

	private String renderLivingClassName;
	private String renderPlayerClassName;
	private String modelBipedClassName;
	private String entityPlayerClassName;
	
	private String modelBipedFieldName;
	private String modelArmourFieldName;
	private String modelChestplateFieldName;
	
	private String doRenderLivingMethodName;
	private String doRenderLivingMethodDesc;
	
	private String renderPlayerMethodName;
	private String renderPlayerMethodDesc;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.client.renderer.entity.RenderPlayer")){
			
			renderLivingClassName = BattleGearTranslator.getMapedClassName("RenderLiving");
			renderPlayerClassName = BattleGearTranslator.getMapedClassName("RenderPlayer");
			entityPlayerClassName = BattleGearTranslator.getMapedClassName("EntityPlayer");
			modelBipedClassName = BattleGearTranslator.getMapedClassName("ModelBiped");
			System.out.println(modelBipedClassName);
			
			modelBipedFieldName = BattleGearTranslator.getMapedFieldName("RenderPlayer", "field_77109_a");
			modelArmourFieldName = BattleGearTranslator.getMapedFieldName("RenderPlayer", "field_77111_i");
			modelChestplateFieldName = BattleGearTranslator.getMapedFieldName("RenderPlayer", "field_77108_b");
			
			doRenderLivingMethodName = BattleGearTranslator.getMapedMethodName("RenderLiving", "func_77031_a");
			doRenderLivingMethodDesc = BattleGearTranslator.getMapedMethodDesc("RenderLiving", "func_77031_a");
			
			renderPlayerMethodName = BattleGearTranslator.getMapedMethodName("RenderPlayer", "func_77101_a");
			renderPlayerMethodDesc = BattleGearTranslator.getMapedMethodDesc("RenderPlayer", "func_77101_a");
			
			
			System.out.println("M&B - Patching Class RenderPlayer ("+name+")");
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			
			
			for(MethodNode mn: cn.methods){
				if(mn.name.equals(renderPlayerMethodName) &&
						mn.desc.equals(renderPlayerMethodDesc)){
					processRenderPlayerMethod(mn);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class RenderPlayer ("+name+") done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
		    
		    return cw.toByteArray();
		}else{
			return bytes;
		}
	}

	private void processRenderPlayerMethod(MethodNode mn) {
		
		AbstractInsnNode target = null;
		System.out.println("Processing RenderPlayer Method in class RenderPlayer");
		
		ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
		while(it.hasNext() && target == null){
			AbstractInsnNode node = it.next();
			
			if(node instanceof MethodInsnNode &&
					((MethodInsnNode) node).owner.equals(renderLivingClassName) &&
					((MethodInsnNode) node).name.equals(doRenderLivingMethodName) &&
					((MethodInsnNode) node).desc.equals(doRenderLivingMethodDesc)){
				
				node = it.previous();
				node = it.previous();
				System.out.println(node);
				while(node instanceof VarInsnNode){
					node = it.previous();
				}
				target = node;
				System.out.println(node);
			}
		}
		
		InsnList newList = new InsnList();
		it = mn.instructions.iterator();
		while(it.hasNext()){
			AbstractInsnNode node = it.next();
			
			if(node == target){
				newList.add(node);
				newList.add(new VarInsnNode(ALOAD, 1));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelBipedFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelArmourFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelChestplateFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(FLOAD, 9));
				newList.add(new MethodInsnNode(INVOKESTATIC,
						"mods/battlegear2/client/utils/BattlegearRenderHelper",
						"preRenderLiving",
						"(L"+entityPlayerClassName+";L"+modelBipedClassName+";L"+modelBipedClassName+";L"+modelBipedClassName+";F)V"));
			}else if(node.getNext() != null && node.getNext().getOpcode() == RETURN){
				
				newList.add(new VarInsnNode(ALOAD, 1));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelBipedFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelArmourFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(ALOAD, 0));
				newList.add(new FieldInsnNode(GETFIELD, renderPlayerClassName, modelChestplateFieldName, "L"+modelBipedClassName+";"));
				newList.add(new VarInsnNode(FLOAD, 9));
				newList.add(new MethodInsnNode(INVOKESTATIC,
						"mods/battlegear2/client/utils/BattlegearRenderHelper",
						"postRenderLiving",
						"(L"+entityPlayerClassName+";L"+modelBipedClassName+";L"+modelBipedClassName+";L"+modelBipedClassName+";F)V"));
			
				newList.add(node);
			}else{
				newList.add(node);
			}
			
			
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
