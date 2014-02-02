package mods.battlegear2.coremod.transformers;

import java.util.List;

import mods.battlegear2.api.core.BattlegearTranslator;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerMethodProcess extends TransformerBase{

	private final String meth;
    private final String devName;
    private final String devDesc;
	private String methName;
	private String methDesc;

	public TransformerMethodProcess(String classPath, String method, String[] devs) {
		super(classPath);
        this.meth = method;
        this.devName = devs[0];
        this.devDesc = devs[1];
	}

	@Override
	boolean processMethods(List<MethodNode> methods) {
		for (MethodNode method : methods) {
            if (method.name.equals(methName) && method.desc.equals(methDesc)) {
            	processMethod(method);
            	return true;
            }
        }
        return false;
	}

	abstract void processMethod(MethodNode method);

	@Override
	boolean processFields(List<FieldNode> fields) {
		return true;
	}

	@Override
	void setupMappings() {
        methName =
            BattlegearTranslator.getMapedMethodName(unobfClass, meth, devName);
        methDesc =
            BattlegearTranslator.getMapedMethodDesc(unobfClass, meth, devDesc);
	}

}
