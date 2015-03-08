package mods.battlegear2.coremod.transformers;

import mods.battlegear2.api.core.BattlegearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

public abstract class TransformerBase implements IClassTransformer, Opcodes{
    public static final String SIMPLEST_METHOD_DESC = "()V";
    public Logger logger = LogManager.getLogger("battlegear2");
	protected final String classPath;
	protected final String unobfClass;
	public TransformerBase(String classPath){
		this.classPath = classPath;
		this.unobfClass = classPath.substring(classPath.lastIndexOf('.')+1);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (transformedName.equals(classPath)) {
			logger.log(Level.INFO, "M&B - Patching Class "+ unobfClass +" (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode();

            cr.accept(cn, 0);

            setupMappings();
            boolean success = processFields(cn.fields) && processMethods(cn.methods);
            addInterface(cn.interfaces);
            
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);

            logger.log(success ?Level.INFO:Level.ERROR, "M&B - Patching Class " + unobfClass + (success ? " done" : " FAILED!"));
            if (!success && BattlegearTranslator.debug) {
                writeClassFile(cw, unobfClass+" ("+name+")");
            }
            
			return cw.toByteArray();

        } else
            return bytes;
	}

	void addInterface(List<String> interfaces) {}

	abstract boolean processMethods(List<MethodNode> methods);

	abstract boolean processFields(List<FieldNode> fields);

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
                    ((FieldInsnNode) nextNode.getNext()).owner.equals(BattlegearTranslator.getMapedClassName("entity.player.InventoryPlayer")) &&
                    ((FieldInsnNode) nextNode.getNext()).name.equals(BattlegearTranslator.getMapedFieldName("InventoryPlayer", "field_70462_a", "mainInventory"))
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
                        "mods/battlegear2/api/core/BattlegearUtils",
                        "setPlayerCurrentItem",
                        "(L" + BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer") +
                                ";L" + BattlegearTranslator.getMapedClassName("item.ItemStack") + ";)V"));

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
        logger.log(Level.INFO, "\tPatching method " + method + " in " + unobfClass);
    }

    public static MethodNode generateSetter(String className, String methodName, String fieldName, String fieldType){
        MethodNode mn = new MethodNode(ACC_PUBLIC, methodName, "("+fieldType+")V", null, null);
        mn.visitCode();
        mn.visitVarInsn(ALOAD, 0);
        int opCode;
        if(fieldType.equals("I") || fieldType.equals("Z")){
            opCode = ILOAD;
        }else if(fieldType.equals("L")){
            opCode = LLOAD;
        }else if(fieldType.equals("F")){
            opCode = FLOAD;
        }else if(fieldType.equals("D")){
            opCode = DLOAD;
        }else {
            opCode = ALOAD;
        }
        mn.visitVarInsn(opCode, 1);
        mn.visitFieldInsn(PUTFIELD, className, fieldName, fieldType);
        mn.visitInsn(RETURN);
        mn.visitMaxs(2, 2);
        mn.visitEnd();
        return mn;
    }

    public static MethodNode generateGetter(String className, String methodName, String fieldName, String fieldType){
        MethodNode mn = new MethodNode(ACC_PUBLIC, methodName, "()"+fieldType, null, null);
        mn.visitCode();
        mn.visitVarInsn(ALOAD, 0);
        mn.visitFieldInsn(GETFIELD, className, fieldName, fieldType);
        int opCode;
        if(fieldType.equals("I")){
            opCode = IRETURN;
        }else if(fieldType.equals("L")){
            opCode = LRETURN;
        }else if(fieldType.equals("F")){
            opCode = FRETURN;
        }else if(fieldType.equals("D")){
            opCode = DRETURN;
        }else {
            opCode = ARETURN;
        }
        mn.visitInsn(opCode);
        mn.visitMaxs(1, 1);
        mn.visitEnd();
        return mn;
    }
}
