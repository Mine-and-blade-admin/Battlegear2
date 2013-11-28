package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import java.util.List;

import mods.battlegear2.coremod.BattlegearTranslator;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class EntityArrowTransformer extends TransformerBase {

    public EntityArrowTransformer() {
		super("net.minecraft.entity.projectile.EntityArrow");
	}

	private static String xTileField;
    private static String yTileField;
    private static String zTileField;
    private static String ticksInGroundField;

	@Override
	void processMethods(List<MethodNode> methods) {
	}

	@Override
	void processFields(List<FieldNode> fields) {
		for(FieldNode fn : fields){
            System.out.println(fn.name+"\t"+fn.desc);
            if (((
                    fn.name.equals(xTileField) ||
                            fn.name.equals(yTileField) ||
                            fn.name.equals(zTileField) ||
                            fn.name.equals(ticksInGroundField)
            )

            )&& fn.desc.equals("I")) {

                fn.access = ACC_PUBLIC;

            }
        }
	}

	@Override
	void setupMappings() {
		xTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70247_d") ;
        yTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70248_e") ;
        zTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70247_f") ;
        ticksInGroundField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70252_j") ;
	}
}
