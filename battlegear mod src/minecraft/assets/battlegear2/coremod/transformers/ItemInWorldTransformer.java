package assets.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import assets.battlegear2.common.utils.BattlegearUtils;
import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

import cpw.mods.fml.relauncher.IClassTransformer;

public class ItemInWorldTransformer implements IClassTransformer{

	private String entityPlayerClassName;
	private String inventoryPlayerClassName;
	private String itemStackClassName;
	private String entityOtherPlayerMPClassName;
	
	private String playerInventoryFieldName;
	private String mainInventoryArrayFieldName;
	
	private String tryUseItemMethodName;
	private String tryUseItemMethodDesc;
	private String setInventorySlotMethodName;
	private String setInventorySlotMethodDesc;
	
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(transformedName.equals("net.minecraft.item.ItemInWorldManager")){
			
			System.out.println("M&B - Patching Class ItemInWorldManager ("+name+")");
			
			entityPlayerClassName = BattleGearTranslator.getMapedClassName("EntityPlayer");
			inventoryPlayerClassName = BattleGearTranslator.getMapedClassName("InventoryPlayer");
			itemStackClassName = BattleGearTranslator.getMapedClassName("ItemStack");
			
			playerInventoryFieldName =
					BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by");
			mainInventoryArrayFieldName =
					BattleGearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a");
			
			tryUseItemMethodName = 
					BattleGearTranslator.getMapedMethodName("ItemInWorldManagaer", "func_73085_a");
			tryUseItemMethodDesc = 
					BattleGearTranslator.getMapedMethodDesc("ItemInWorldManagaer", "func_73085_a");
			setInventorySlotMethodName =
					BattleGearTranslator.getMapedMethodName("InventoryPlayer", "func_70299_a");
			setInventorySlotMethodDesc =
					BattleGearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70299_a");
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			for(MethodNode mn : cn.methods){
				
				if(mn.name.equals(tryUseItemMethodName) &&
						mn.desc.equals(tryUseItemMethodDesc)){
					processTryUseItemMethod(mn);
				}
				
			}
			
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class ItemInWorldManager done");
		    
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
			return cw.toByteArray();
		}else{
			return bytes;
		}
	}


	private void processTryUseItemMethod(MethodNode mn) {
		
		System.out.println("\tPatching method tryUseItem in ItemInWorldManager");
		TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 5, 7);	
		
		InsnList newList = new InsnList();
		
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();
		
		while(it.hasNext()){
			AbstractInsnNode node = it.next();
			
			if(node instanceof FieldInsnNode &&
					((FieldInsnNode) node).owner.equals(inventoryPlayerClassName) &&
					((FieldInsnNode) node).name.equals(mainInventoryArrayFieldName) &&
					((FieldInsnNode) node).desc.equals("[L"+itemStackClassName+";")){
				
				//Do Nothing
			}else if (node.getOpcode() == AASTORE){
				newList.add(new MethodInsnNode(INVOKEVIRTUAL, itemStackClassName, setInventorySlotMethodName, setInventorySlotMethodDesc));
			}else{
				newList.add(node);
			}
		}
		
		mn.instructions = newList;
		
	}

}










