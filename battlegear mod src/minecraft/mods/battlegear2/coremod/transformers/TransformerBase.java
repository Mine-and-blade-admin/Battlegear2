package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;

public abstract class TransformerBase implements IClassTransformer{

	protected final String classPath;
	protected final String unobfClass;
	public TransformerBase(String classPath){
		this.classPath = classPath;
		this.unobfClass = classPath.substring(classPath.lastIndexOf('.')+1);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.equals(classPath)) {
			System.out.println("M&B - Patching Class "+ unobfClass +" (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            setupMappings();
            processFields(cn.fields);
            processMethods(cn.methods);
            
			ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            System.out.println("M&B - Patching Class "+ unobfClass +" done");
            if (BattlegearLoadingPlugin.debug) {
                writeClassFile(cw, unobfClass+" ("+name+")");
            }
			return cw.toByteArray();

        } else
            return bytes;
	}

	abstract void processMethods(List<MethodNode> methods);

	abstract void processFields(List<FieldNode> fields);

	abstract void setupMappings();
	
	public static void writeClassFile(ClassWriter cw, String name) {
        try {
            File outDir = BattlegearLoadingPlugin.debugOutputLocation;
            outDir.mkdirs();
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(new File(outDir, name + ".class")));
            dout.write(cw.toByteArray());
            dout.flush();
            dout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static MethodNode replaceInventoryArrayAccess(MethodNode method, String className, String fieldName, int maxStack, int maxLocal) {
        return replaceInventoryArrayAccess(method, className, fieldName, 4, maxStack, maxLocal);
    }

    public static MethodNode replaceInventoryArrayAccess(MethodNode method, String className, String fieldName, int todelete, int maxStack, int maxLocal) {

        InsnList newList = new InsnList();

        Iterator<AbstractInsnNode> it = method.instructions.iterator();

        while (it.hasNext()) {
            AbstractInsnNode nextNode = it.next();

            if (nextNode instanceof FieldInsnNode &&
                    nextNode.getNext() instanceof FieldInsnNode &&
                    ((FieldInsnNode) nextNode).owner.equals(className) &&
                    ((FieldInsnNode) nextNode).name.equals(fieldName) &&
                    ((FieldInsnNode) nextNode.getNext()).owner.equals(BattlegearTranslator.getMapedClassName("InventoryPlayer")) &&
                    ((FieldInsnNode) nextNode.getNext()).name.equals(BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a"))
                    ) {

                //skip the next 4
                for (int i = 0; i < todelete; i++) {
                    nextNode = it.next();
                }
                //add all until the AAStore
                nextNode = it.next();
                while (it.hasNext() && nextNode.getOpcode() != AASTORE) {
                    newList.add(nextNode);
                    nextNode = it.next();
                }

                //Add New
                newList.add(new MethodInsnNode(INVOKESTATIC,
                        "mods/battlegear2/utils/BattlegearUtils",
                        "setPlayerCurrentItem",
                        "(L" + BattlegearTranslator.getMapedClassName("EntityPlayer") +
                                ";L" + BattlegearTranslator.getMapedClassName("ItemStack") + ";)V"));

            } else {
                newList.add(nextNode);
            }
        }

        method.instructions = newList;

        method.maxStack = maxStack;
        method.maxLocals = maxLocal;

        return method;

    }
    
    public void sendPatchLog(String method){
    	System.out.println("\tPatching method "+method+" in "+unobfClass);
    }
}
