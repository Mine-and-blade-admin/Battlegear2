package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformerMultiFields extends TransformerBase{

	private final List<String> fieldNames;
	private List<String> translatedNames = new ArrayList<String>();
	public TransformerMultiFields(String classPath, String[][] fieldNames) {
		super(classPath);
		this.fieldNames = Arrays.asList(fieldNames[BattlegearLoadingPlugin.obfuscatedEnv?0:1]);
	}
	
	@Override
	void processMethods(List<MethodNode> methods) {
	}
	
	@Override
	void processFields(List<FieldNode> fields) {
		int found = 0;
		for(FieldNode fn : fields){
			if(translatedNames.contains(fn.name)){
				fn.access = ACC_PUBLIC;
				found += 1;
			}
			if(found >= translatedNames.size()){
				return;
			}
		}
	}
	
	@Override
	void setupMappings() {
		for(String field:fieldNames){
			translatedNames.add(BattlegearTranslator.getMapedFieldName(unobfClass,field,field));
		}
	}
}
