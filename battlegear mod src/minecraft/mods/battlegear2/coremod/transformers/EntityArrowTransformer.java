package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM4;
@Deprecated//Fields accessibility is already changed by Forge
public class EntityArrowTransformer implements IClassTransformer {

    private static String xTileField;
    private static String yTileField;
    private static String zTileField;
    private static String ticksInGroundField;


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.projectile.EntityArrow")) {

            xTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70247_d") ;
            yTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70248_e") ;
            zTileField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70247_f") ;
            ticksInGroundField = BattlegearTranslator.getMapedFieldName("EntityArrow", "field_70252_j") ;



            System.out.println("M&B - Patching Class EntityArrow (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);
            cr.accept(cn, 0);


            for(Object fnOb : cn.fields){
            	FieldNode fn = (FieldNode)fnOb;

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




            System.out.println("M&B - Patching Class EntityArrow done");;


            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, name);
            }


            return cw.toByteArray();

        } else
            return bytes;
    }
}
