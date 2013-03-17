package mods.battlegear2.coremod.transformers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.ListIterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import static mods.battlegear2.coremod.BattlegearObNames.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.IClassTransformer;
import cpw.mods.fml.relauncher.Side;

public class EntityPlayerTransformer implements IClassTransformer{

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		
		if(name.equals(entityPlayerClassName)){
			
			System.out.println("M&B - Patching Class EntityPlayer ("+name+")");
						
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode(ASM4);
			
			cr.accept(cn, 0);
			
			processFields(cn);
			processMethods(cn);
		
			System.out.println("\tCreating new methods in EntityPlayer");
			cn.methods.add(0, generateAttackOffhandMethod());
			cn.methods.add(1, generateSwingOffhand());
			cn.methods.add(2, generateGetOffSwingMethod());
			cn.methods.add(3, generateSwingAnimationEnd2());
			cn.methods.add(4, generateUpdateSwingArm());
			cn.methods.add(5, generateIsBattleMode());
			
			ClassWriter cw = new ClassWriter(0);
		    cn.accept(cw);
		    
		    
		    System.out.println("M&B - Patching Class EntityPlayer done");

			return cw.toByteArray();
			
		}else
			return bytes;
	}
	
	private void processFields(ClassNode cn) {
		System.out.println("\tAdding new fields to EntityPlayer");
		cn.fields.add(0, new FieldNode(ACC_PUBLIC, "offHandSwingProgress", "F", null, 0F));
		cn.fields.add(1, new FieldNode(ACC_PUBLIC, "prevOffHandSwingProgress", "F", null, 0F));
		cn.fields.add(2, new FieldNode(ACC_PUBLIC, "offHandSwingProgressInt", "I", null, 0));
		cn.fields.add(3, new FieldNode(ACC_PUBLIC, "isOffHandSwingInProgress", "Z", null, 0F));
		
		for (FieldNode fn: cn.fields) {
			if(fn.name.equals("L"+inventoryCurrentItremField) && fn.desc.equals(inventoryClassName+";")){
				System.out.println("M&B - Marking field inventory as final in EntityPlayer");
				fn.access = ACC_PUBLIC | ACC_FINAL;
			}
		}
	}

	private void processMethods(ClassNode cn) {
		for (MethodNode mn : cn.methods) {
			if(mn.name.equals("<init>")){
				System.out.println("\tPatching constructor in EntityPlayer");
				ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
				
				while(it.hasNext()){
					AbstractInsnNode insn = it.next();
					if(insn instanceof TypeInsnNode){
						if(((TypeInsnNode)insn).desc.equals(inventoryClassName)){
							((TypeInsnNode)insn).desc = "mods/battlegear2/common/inventory/InventoryPlayerBattle";
						}
						
					}else if (insn instanceof MethodInsnNode){
						if(((MethodInsnNode)insn).owner.equals(inventoryClassName)){
							((MethodInsnNode)insn).owner = "mods/battlegear2/common/inventory/InventoryPlayerBattle";
						}
					}
				}
			}else if(mn.name.equals(onItemFinishMethodName) && mn.desc.equals("()V")){
				System.out.println("\tPatching method onItemUseFinish in EntityPlayer");
				
				TransformerUtils.replaceInventoryArrayAccess(mn, entityPlayerClassName, playerInventoryFieldName, 3, 3);
				//mn = processOnItemFinish(mn);
			}
		}
	}

	@Deprecated
	private MethodNode processOnItemFinish(MethodNode mn) {
		InsnList newList = new InsnList();
		
		Iterator<AbstractInsnNode> it = mn.instructions.iterator();
		
		while(it.hasNext()){
			AbstractInsnNode nextNode = it.next();
			
			if(nextNode instanceof FieldInsnNode && 
					((FieldInsnNode)nextNode).owner.equals(entityPlayerClassName) &&
					((FieldInsnNode)nextNode).name.equals(playerInventoryFieldName) &&
					((FieldInsnNode)nextNode).desc.equals("L"+inventoryClassName+";")){
				
				//skip the next four
				it.next();
				it.next();
				it.next();
				it.next();
				
				
			}else if(nextNode instanceof InsnNode && nextNode.getOpcode() == AASTORE){
				
				newList.add(new MethodInsnNode(INVOKESTATIC, 
						"mods/battlegear2/common/utils/BattlegearUtils", 
						"setPlayerCurrentItem", 
						"(L"+entityPlayerClassName+";L"+itemStackClassName+";)V"));
			}else{
				newList.add(nextNode);
			}
		}
		
		
		
		mn.instructions = newList;
		
		mn.maxStack = 3;
		mn.maxLocals = 3;
		
		return mn;
	}

	private MethodNode generateAttackOffhandMethod() {
		MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "attackTargetEntityWithCurrentOffItem", "(L"+entityClassName+";)V", null, null);
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L"+inventoryClassName+";"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L"+inventoryClassName+";"));
		mn.instructions.add(new FieldInsnNode(GETFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
		mn.instructions.add(new FieldInsnNode(GETSTATIC, "mods/battlegear2/common/inventory/InventoryPlayerBattle", "WEAPON_SETS", "I"));
		mn.instructions.add(new InsnNode(IADD));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new VarInsnNode(ALOAD, 1));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, attackTargetMethodName, "(L"+entityClassName+";)V"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L"+inventoryClassName+";"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L"+inventoryClassName+";"));
		mn.instructions.add(new FieldInsnNode(GETFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
		mn.instructions.add(new FieldInsnNode(GETSTATIC, "mods/battlegear2/common/inventory/InventoryPlayerBattle", "WEAPON_SETS", "I"));
		mn.instructions.add(new InsnNode(ISUB));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, inventoryClassName, inventoryCurrentItremField, "I"));
		mn.instructions.add(new InsnNode(RETURN));
		
		mn.maxStack = 3;
		mn.maxLocals = 2;
		
		return mn;
	}
	
	private MethodNode generateSwingOffhand(){
		MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "swingOffItem", "()V", null, null);
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
		LabelNode l0  = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFEQ, l0));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityPlayerClassName, "getArmSwingAnimationEndCopy", "()I"));
		mn.instructions.add(new InsnNode(ICONST_2));
		mn.instructions.add(new InsnNode(IDIV));
		mn.instructions.add(new JumpInsnNode(IF_ICMPGE, l0));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		LabelNode l1  = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFGE, l1));
		mn.instructions.add(l0);
		mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(ICONST_M1));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(ICONST_1));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
		mn.instructions.add(l1);
		mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
		mn.instructions.add(new InsnNode(RETURN));	
		
		mn.maxStack = 3;
		mn.maxLocals = 1;
		
		return mn;
	}
	
	private MethodNode generateGetOffSwingMethod() {
		
		MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "getOffSwingProgress", "(F)F", null, null);
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgress", "F"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F"));
		mn.instructions.add(new InsnNode(FSUB));
		mn.instructions.add(new VarInsnNode(FSTORE, 2));
		
		mn.instructions.add(new VarInsnNode(FLOAD, 2));
		mn.instructions.add(new InsnNode(FCONST_0));
		mn.instructions.add(new InsnNode(FCMPG));
		LabelNode l0 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFGE, l0));
		
		mn.instructions.add(new VarInsnNode(FLOAD, 2));
		mn.instructions.add(new InsnNode(FCONST_1));
		mn.instructions.add(new InsnNode(FADD));
		mn.instructions.add(new VarInsnNode(FSTORE, 2));
		
		mn.instructions.add(l0);
		mn.instructions.add(new FrameNode(F_APPEND,1, new Object[] {FLOAT}, 0, null));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "prevOffHandSwingProgress", "F"));
		
		mn.instructions.add(new VarInsnNode(FLOAD, 2));
		mn.instructions.add(new VarInsnNode(FLOAD, 1));
		mn.instructions.add(new InsnNode(FMUL));
		mn.instructions.add(new InsnNode(FADD));
		mn.instructions.add(new InsnNode(FRETURN));
		
		mn.maxLocals = 3;
		mn.maxStack = 3;
		
		return mn;
	}
	
	
	
	private MethodNode generateSwingAnimationEnd2(){
		
		MethodNode mn = new MethodNode(ASM4, ACC_PRIVATE, "getArmSwingAnimationEndCopy", "()I", null, null);
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSpeedField, "L"+potionClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerPotionActiveMethodName, "(L"+potionClassName+";)Z"));
		LabelNode l0 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFEQ, l0));
		
		mn.instructions.add(new IntInsnNode(BIPUSH, 6));
		mn.instructions.add(new InsnNode(ICONST_1));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSpeedField, "L"+potionClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerGetActivePotionMethodName, "(L"+potionClassName+";)L"+potionEffectClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, potionEffectClassName, potionEffectGetAmpMethodName, "()I"));
		mn.instructions.add(new InsnNode(IADD));
		mn.instructions.add(new InsnNode(ICONST_1));
		mn.instructions.add(new InsnNode(IMUL));
		mn.instructions.add(new InsnNode(ISUB));
		LabelNode l1 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(GOTO, l1));
		
		mn.instructions.add(l0);
		mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		
		mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSlowField, "L"+potionClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerPotionActiveMethodName, "(L"+potionClassName+";)Z"));
		LabelNode l2 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFEQ, l2));
		
		mn.instructions.add(new IntInsnNode(BIPUSH, 6));
		mn.instructions.add(new InsnNode(ICONST_1));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETSTATIC, potionClassName, potionDigSlowField, "L"+potionClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, entityPlayerClassName, playerGetActivePotionMethodName, "(L"+potionClassName+";)L"+potionEffectClassName+";"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, potionEffectClassName, potionEffectGetAmpMethodName, "()I"));
		mn.instructions.add(new InsnNode(IADD));
		mn.instructions.add(new InsnNode(ICONST_2));
		mn.instructions.add(new InsnNode(IMUL));
		mn.instructions.add(new InsnNode(IADD));
		mn.instructions.add(new JumpInsnNode(GOTO, l1));
		
		
		mn.instructions.add(l2);
		mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
		mn.instructions.add(new IntInsnNode(BIPUSH, 6));
		
		mn.instructions.add(l1);
		mn.instructions.add(new FrameNode(F_SAME1, 0, null, 1, new Object[] {INTEGER}));
		mn.instructions.add(new InsnNode(IRETURN));
		
		mn.maxStack = 4;
		mn.maxLocals = 1;
		
		return mn;
	}
	
	
	private MethodNode generateUpdateSwingArm(){
		
		MethodNode mn = new MethodNode(ASM4, ACC_PROTECTED, playerUpdateArmSwingMethodName, "()V", null, null);
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityLivingClassName, playerUpdateArmSwingMethodName, "()V"));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName,"offHandSwingProgress", "F"));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName,"prevOffHandSwingProgress", "F"));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		
		mn.instructions.add(new MethodInsnNode(INVOKESPECIAL, entityPlayerClassName, "getArmSwingAnimationEndCopy", "()I"));
		mn.instructions.add(new VarInsnNode(ISTORE, 1));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		
		
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
		LabelNode l0 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IFEQ, l0));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(DUP));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		mn.instructions.add(new InsnNode(ICONST_1));
		mn.instructions.add(new InsnNode(IADD));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		mn.instructions.add(new VarInsnNode(ILOAD, 1));
		LabelNode l1 = new LabelNode();
		mn.instructions.add(new JumpInsnNode(IF_ICMPLT, l1));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(ICONST_0));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(ICONST_0));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "isOffHandSwingInProgress", "Z"));
		mn.instructions.add(new JumpInsnNode(GOTO, l1));
		
		mn.instructions.add(l0);
		mn.instructions.add(new FrameNode(F_APPEND,1, new Object[] {INTEGER}, 0, null));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new InsnNode(ICONST_0));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		
		mn.instructions.add(l1);
		mn.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
		
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, "offHandSwingProgressInt", "I"));
		mn.instructions.add(new InsnNode(I2F));
		mn.instructions.add(new VarInsnNode(ILOAD, 1));
		mn.instructions.add(new InsnNode(I2F));
		mn.instructions.add(new InsnNode(FDIV));
		mn.instructions.add(new FieldInsnNode(PUTFIELD, entityPlayerClassName, "offHandSwingProgress", "F"));
		mn.instructions.add(new InsnNode(RETURN));
		
		mn.maxStack = 3;
		mn.maxLocals = 2;
		
		
		return mn;
	}
	

	private MethodNode generateIsBattleMode() {
		MethodNode mn = new MethodNode(ASM4, ACC_PUBLIC, "isBattlemode", "()Z", null, null);
		mn.instructions.add(new VarInsnNode(ALOAD, 0));
		mn.instructions.add(new FieldInsnNode(GETFIELD, entityPlayerClassName, playerInventoryFieldName, "L"+inventoryClassName+";"));
		mn.instructions.add(new TypeInsnNode(CHECKCAST, "mods/battlegear2/common/inventory/InventoryPlayerBattle"));
		mn.instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "mods/battlegear2/common/inventory/InventoryPlayerBattle", "isBattlemode", "()Z"));
		mn.instructions.add(new InsnNode(IRETURN));
		
		mn.maxStack = 1;
		mn.maxLocals = 1;
		
		return mn;
	}


	

}
