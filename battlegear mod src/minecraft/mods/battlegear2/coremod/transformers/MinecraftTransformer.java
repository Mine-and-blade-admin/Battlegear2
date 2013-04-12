package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ASM4;

import mods.battlegear2.coremod.BattleGearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.relauncher.IClassTransformer;

public class MinecraftTransformer implements IClassTransformer{

	private String entityClientPlayerClass;
	
	private String playerInventoryFieldName;
	
	private String clickMouseMethodName;
	private String clickMouseMethodDesc;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		if(transformedName.equals("net.minecraft.client.Minecraft")){
			
			entityClientPlayerClass = BattleGearTranslator.getMapedClassName("EntityClientPlayerMP");
			
			playerInventoryFieldName = BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by");
			
			clickMouseMethodName = BattleGearTranslator.getMapedMethodName("Minecraft", "func_71402_c");
			clickMouseMethodDesc = BattleGearTranslator.getMapedMethodDesc("Minecraft", "func_71402_c");
			
			
			
			System.out.println("M&B - Patching Class Minecraft ("+name+")");
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			
			
			for (MethodNode method: cn.methods) {
				if(method.name.equals(clickMouseMethodName) &&
						method.desc.equals(clickMouseMethodDesc)){
					System.out.println("\tPatching method Click Mouse in Minecraft");
					
					TransformerUtils.replaceInventoryArrayAccess(method, entityClientPlayerClass, playerInventoryFieldName, 5, 9, 10);
				}
			}
			
			
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    System.out.println("M&B - Patching Class Minecraft ("+name+") done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
		    
		    return cw.toByteArray();
			
		}
		else
			return bytes;
	}

}
