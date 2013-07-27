package assets.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import assets.battlegear2.coremod.BattleGearTranslator;
import assets.battlegear2.coremod.BattlegearLoadingPlugin;

public class NetServerHandlerTransformer implements IClassTransformer{

	private String packet16BlockItemSwitchClassName;
	private String entityPlayerMPClassName;
	private String entityPlayerClassName;
	private String netServiceHandelerClassName;
	private String inventoryPlayerClassName;
	private String itemStackClassName;
	
	private String playerInventoryFieldName;
	private String packet16BlockItemSwitchId;
	private String netServiceHandelerPlayerField;
	
	private String handleBlockSwitchMethodName;
	private String handleBlockSwitchMethodDesc;
	private String handlePlaceMethodName;
	private String handlePlaceMethodDesc;
	private String inventoryGetCurrentMethodName;
	private String inventoryGetCurrentMethodDesc;
	private String itemStackCopyStackMethodName;
	private String itemStackCopyStackMethodDesc;
	
	
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		if(transformedName.equals("net.minecraft.network.NetServerHandler")){
			
			System.out.println("M&B - Patching Class NetServiceHandeler ("+name+")");
			
			netServiceHandelerClassName = BattleGearTranslator.getMapedClassName("NetServerHandler");
			packet16BlockItemSwitchClassName = BattleGearTranslator.getMapedClassName("Packet16BlockItemSwitch");
			entityPlayerMPClassName = BattleGearTranslator.getMapedClassName("EntityPlayerMP");
			inventoryPlayerClassName = BattleGearTranslator.getMapedClassName("InventoryPlayer");
			itemStackClassName = BattleGearTranslator.getMapedClassName("ItemStack");
			entityPlayerClassName = BattleGearTranslator.getMapedClassName("EntityPlayer");
			
			packet16BlockItemSwitchId = BattleGearTranslator.getMapedFieldName("Packet16BlockItemSwitch", "field_73386_a");
			playerInventoryFieldName = BattleGearTranslator.getMapedFieldName("EntityPlayer","field_71071_by");
			netServiceHandelerPlayerField = BattleGearTranslator.getMapedFieldName("NetServerHandler", "field_72574_e");
			
			
			handleBlockSwitchMethodName = BattleGearTranslator.getMapedMethodName("NetServerHandler","func_72502_a");
			handleBlockSwitchMethodDesc = BattleGearTranslator.getMapedMethodDesc("NetServerHandler","func_72502_a");
			
			handlePlaceMethodName = BattleGearTranslator.getMapedMethodName("NetServerHandler", "func_72472_a");
			handlePlaceMethodDesc = BattleGearTranslator.getMapedMethodDesc("NetServerHandler", "func_72472_a");
			
			inventoryGetCurrentMethodName = BattleGearTranslator.getMapedMethodName("InventoryPlayer", "func_70448_g");
			inventoryGetCurrentMethodDesc = BattleGearTranslator.getMapedMethodDesc("InventoryPlayer", "func_70448_g");
			
			itemStackCopyStackMethodName = BattleGearTranslator.getMapedMethodName("ItemStack", "func_77944_b");
			itemStackCopyStackMethodDesc = BattleGearTranslator.getMapedMethodDesc("ItemStack", "func_77944_b");
			
			
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			
			
			for (MethodNode mn : cn.methods) {				
				if(mn.name.equals(handleBlockSwitchMethodName) &&
					mn.desc.equals(handleBlockSwitchMethodDesc)){
					processSwitchBlockMethod(mn);
				}else if (mn.name.equals(handlePlaceMethodName) &&
						mn.desc.equals(handlePlaceMethodDesc)){
					processPlaceMethod(mn);
				}
			}
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
			System.out.println("M&B - Patching Class NetServiceHandeler done");
		    
		    
		    if(BattlegearLoadingPlugin.debug){
			    TransformerUtils.writeClassFile(cw, name);
		    }
		    

			return cw.toByteArray();

		}else{
			return bytes;
		}
	}
	
	private void processPlaceMethod(MethodNode mn) {
		System.out.println("\tPatching method handlePlace in NetServiceHandler");
		InsnList newList = new InsnList();
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();
		
		int fieldCount = 0;
		while(it.hasNext()){
			AbstractInsnNode nextNode = it.next();
			
			if(nextNode instanceof FieldInsnNode &&
					((FieldInsnNode) nextNode).owner.equals(entityPlayerMPClassName) &&
					((FieldInsnNode) nextNode).name.equals(playerInventoryFieldName)){
				fieldCount ++;
				
				if(fieldCount == 3){
					
					while(it.hasNext() && nextNode.getOpcode() != ACONST_NULL){
						nextNode = it.next();
					}
					
					newList.add(nextNode);
					newList.add(new MethodInsnNode(INVOKESTATIC, 
							"mods/battlegear2/common/utils/BattlegearUtils", 
							"setPlayerCurrentItem", 
							"(L"+BattleGearTranslator.getMapedClassName("EntityPlayer")+
							";L"+BattleGearTranslator.getMapedClassName("ItemStack")+";)V"));
					it.next();
					
					
				}else if (fieldCount == 4){
					
					while(it.hasNext() && nextNode.getOpcode() != AASTORE){
						nextNode = it.next();
					}
					
					
					newList.add(new VarInsnNode(ALOAD, 0));
					newList.add(new FieldInsnNode(GETFIELD, netServiceHandelerClassName, netServiceHandelerPlayerField, "L"+entityPlayerMPClassName+";"));
					newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerInventoryFieldName, "L"+inventoryPlayerClassName+";"));
					newList.add(new MethodInsnNode(INVOKEVIRTUAL, inventoryPlayerClassName, inventoryGetCurrentMethodName, inventoryGetCurrentMethodDesc));
					newList.add(new MethodInsnNode(INVOKESTATIC, itemStackClassName, itemStackCopyStackMethodName, itemStackCopyStackMethodDesc));
					newList.add(new MethodInsnNode(INVOKESTATIC, 
							"mods/battlegear2/common/utils/BattlegearUtils",
							"setPlayerCurrentItem", "(L"+entityPlayerClassName+";L"+itemStackClassName+";)V"));
					
					
					
				}else{
					newList.add(nextNode);
				}
			}else{
				newList.add(nextNode);
			}
			
		}
		
		
		mn.instructions = newList;
	}


	private void processSwitchBlockMethod(MethodNode mn) {
		System.out.println("\tPatching method handleBlockItemSwitch in NetServiceHandler");
		InsnList newList = new InsnList();
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();
		
		while(it.hasNext()){
			AbstractInsnNode nextInsn = it.next();
			newList.add(nextInsn);
			
			if(nextInsn instanceof FieldInsnNode &&
					nextInsn.getOpcode() == GETFIELD &&
					((FieldInsnNode) nextInsn).owner.equals(packet16BlockItemSwitchClassName) &&
					((FieldInsnNode) nextInsn).name .equals(packet16BlockItemSwitchId)){
				
				newList.add(new MethodInsnNode(INVOKESTATIC, "mods/battlegear2/common/inventory/InventoryPlayerBattle", "isValidSwitch", "(I)Z"));

				nextInsn = it.next();
				while (it.hasNext() &&
						(!(nextInsn instanceof JumpInsnNode) 
						|| !(nextInsn.getOpcode() == IF_ICMPGE))){
					
					System.out.println(nextInsn.getClass());
					nextInsn = it.next();
					
				}
				newList.add(new JumpInsnNode(IFEQ, ((JumpInsnNode)nextInsn).label));
			
				while(it.hasNext()){
					nextInsn = it.next();
					newList.add(nextInsn);
				}
				
			}
		}
		
		
		
		mn.instructions = newList;
		
	}
}
