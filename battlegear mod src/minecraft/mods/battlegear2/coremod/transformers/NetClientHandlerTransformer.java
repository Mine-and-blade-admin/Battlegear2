package mods.battlegear2.coremod.transformers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import mods.battlegear2.coremod.BattleGearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.IClassTransformer;
import static org.objectweb.asm.Opcodes.ASM4;

public class NetClientHandlerTransformer implements IClassTransformer{
	
	private String entityOtherPlayerMPClassName;
	private String playerInventoryFieldName;
	private String packet20NamedEntitySpawnClassName;
	private String netClientHandlerHandleNamedEntitySpawnMethodName;
	private String netClientHandlerHandleNamedEntitySpawnMethodDesc;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.client.multiplayer.NetClientHandler")){
			
			entityOtherPlayerMPClassName = BattleGearTranslator.getMapedClassName("EntityOtherPlayerMP");
			packet20NamedEntitySpawnClassName = BattleGearTranslator.getMapedClassName("Packet20NamedEntitySpawn");
			playerInventoryFieldName = BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by");
			
			netClientHandlerHandleNamedEntitySpawnMethodName =
					BattleGearTranslator.getMapedMethodName("NetClientHandler", "func_72518_a");
			netClientHandlerHandleNamedEntitySpawnMethodDesc =
					BattleGearTranslator.getMapedMethodDesc("NetClientHandler", "func_72518_a");
			
			System.out.println("M&B - Patching Class NetClientHandler ("+name+")");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			for (MethodNode method: cn.methods) {
				if(method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
						method.desc.equals(netClientHandlerHandleNamedEntitySpawnMethodDesc)){
					System.out.println("\tPatching method handleNamedEntitySpawn in NetClientHandler");
					
					TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class NetClientHandler ("+name+") done");
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }

			return cw.toByteArray();
			
		}else{
			return bytes;
		}
		
	}

}
