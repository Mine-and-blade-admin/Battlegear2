package mods.battlegear2.coremod.transformers;

import mods.battlegear2.coremod.BattlegearLoadingPlugin;
import mods.battlegear2.coremod.BattlegearTranslator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.ASM4;

public class NetClientHandlerTransformer implements IClassTransformer {

    private String entityOtherPlayerMPClassName;
    private String playerInventoryFieldName;

    private String packet20NamedEntitySpawnClassName;

    private String netClientHandlerHandleNamedEntitySpawnMethodName;
    private String netClientHandlerHandleNamedEntitySpawnMethodDesc;

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.multiplayer.NetClientHandler")) {

            entityOtherPlayerMPClassName = BattlegearTranslator.getMapedClassName("EntityOtherPlayerMP");
            packet20NamedEntitySpawnClassName = BattlegearTranslator.getMapedClassName("Packet20NamedEntitySpawn");
            playerInventoryFieldName = BattlegearTranslator.getMapedFieldName("EntityPlayer", "field_71071_by");

            netClientHandlerHandleNamedEntitySpawnMethodName =
                    BattlegearTranslator.getMapedMethodName("NetClientHandler", "func_72518_a");
            netClientHandlerHandleNamedEntitySpawnMethodDesc =
                    BattlegearTranslator.getMapedMethodDesc("NetClientHandler", "func_72518_a");

            System.out.println("M&B - Patching Class NetClientHandler (" + name + ")");

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode(ASM4);

            cr.accept(cn, 0);

            for (Object mnObj : cn.methods) {
                MethodNode method = (MethodNode)mnObj;
                if (method.name.equals(netClientHandlerHandleNamedEntitySpawnMethodName) &&
                        method.desc.equals(netClientHandlerHandleNamedEntitySpawnMethodDesc)) {
                    System.out.println("\tPatching method handleNamedEntitySpawn in NetClientHandler");

                    TransformerUtils.replaceInventoryArrayAccess(method, entityOtherPlayerMPClassName, playerInventoryFieldName, 9, 13);
                }
            }

            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);


            System.out.println("M&B - Patching Class NetClientHandler (" + name + ") done");

            if (BattlegearLoadingPlugin.debug) {
                TransformerUtils.writeClassFile(cw, name);
            }

            return cw.toByteArray();

        } else {
            return bytes;
        }

    }

}
