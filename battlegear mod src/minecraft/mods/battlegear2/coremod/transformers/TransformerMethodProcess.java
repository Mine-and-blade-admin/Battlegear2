package mods.battlegear2.coremod.transformers;

import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class TransformerMethodProcess extends TransformerBase{

	private final String meth;
	private String methName;
	private String methDesc;

	public TransformerMethodProcess(String classPath, String method) {
		super(classPath);
		this.meth = method;
	}

	@Override
	void processMethods(List<MethodNode> methods) {
		for (MethodNode method : methods) {
            if (method.name.equals(methName) && method.desc.equals(methDesc)) {
            	processMethod(method);
            	return;
            }
        }
	}

	abstract void processMethod(MethodNode method);

	@Override
	void processFields(List<FieldNode> fields) {
		
	}

	@Override
	void setupMappings() {
		methName =
                BattlegearTranslator.getMapedMethodName(unobfClass, meth);
		methDesc =
                BattlegearTranslator.getMapedMethodDesc(unobfClass, meth);
	}

}
