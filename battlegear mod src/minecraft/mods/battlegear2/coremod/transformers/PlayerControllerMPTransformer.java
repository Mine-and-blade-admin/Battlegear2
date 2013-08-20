package mods.battlegear2.coremod.transformers;



import mods.battlegear2.coremod.BattlegearTranslator;
import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ASM4;


public class PlayerControllerMPTransformer implements IClassTransformer {

    private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;


    private String playerControllerMPsendUseItemMethodName;
    private String playerControllerMPsendUseItemMethodDesc;

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.multiplayer.PlayerControllerMP")) {

            entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityOtherPlayerMP");

            playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");

            playerControllerMPsendUseItemMethodName =
                    BattlegearTranslator.getMapedMethodName("PlayerControllerMP", "func_78769_a");
            playerControllerMPsendUseItemMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("PlayerControllerMP", "func_78769_a");

            System.out.println("M&B - Patching Class PlayerControllerMP (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);


            for (Object mnObj : cn.methods) {
                MethodNode method = (MethodNode)mnObj;
                if (method.name.equals(playerControllerMPsendUseItemMethodName) &&
                        method.desc.equals(playerControllerMPsendUseItemMethodDesc)) {
                    System.out.println("\tPatching method sendUseItem in PlayerControllerMP");
                    TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            System.out.println("M&B - Patching Class PlayerControllerMP (" + name + ") done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, name);
            }

            return cw.toByteArray();

        } else {
            return bytes;
        }

    }

}
