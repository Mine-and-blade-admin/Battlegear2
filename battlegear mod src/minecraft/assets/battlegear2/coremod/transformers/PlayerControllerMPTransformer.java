package assets.battlegear2.coremod.transformers;


import static org.objectweb.asm.Opcodes.ASM4;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

import cpw.mods.fml.relauncher.IClassTransformer;


public class PlayerControllerMPTransformer implements IClassTransformer{
	
	private String entityOtherPlayerMPClassName;
	private String playerInventoryFieldName;
	
	
	private String playerControllerMPsendUseItemMethodName;
	private String playerControllerMPsendUseItemMethodDesc;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.client.multiplayer.PlayerControllerMP")){
			
			entityOtherPlayerMPClassName = BattleGearTranslator.getMapedClassName("EntityOtherPlayerMP");
			playerInventoryFieldName = BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by");
			
			playerControllerMPsendUseItemMethodName =
					BattleGearTranslator.getMapedMethodName("PlayerControllerMP", "func_78769_a");
			playerControllerMPsendUseItemMethodDesc =
					BattleGearTranslator.getMapedMethodDesc("PlayerControllerMP", "func_78769_a");
			
			System.out.println("M&B - Patching Class PlayerControllerMP ("+name+")");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			
			for (MethodNode method: cn.methods) {
				if(method.name.equals(playerControllerMPsendUseItemMethodName) &&
						method.desc.equals(playerControllerMPsendUseItemMethodDesc)){
					System.out.println("\tPatching method sendUseItem in PlayerControllerMP");
					TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class PlayerControllerMP ("+name+") done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }

			return cw.toByteArray();
			
		}else{
			return bytes;
		}
		
	}

}
