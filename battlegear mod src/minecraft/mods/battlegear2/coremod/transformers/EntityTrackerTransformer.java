package mods.battlegear2.coremod.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.ListIterator;

import mods.battlegear2.api.core.BattlegearTranslator;

import org.objectweb.asm.tree.*;

public class EntityTrackerTransformer extends TransformerMethodProcess {

    public EntityTrackerTransformer() {
		super("net.minecraft.entity.EntityTrackerEntry", "func_73117_b", new String[]{"tryStartWachingThis", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V"});
	}

    private String entityPlayerClassName;
    private String entityPlayerMPClassName;
    private String netServerHandlerClasName;
    private String playerMPplayerNetServerHandlerField;
    private String sendPacketToPlayerMethodName;
    private String sendPacketToPlayerMethodDesc;
    private static final String syncPacket = "mods/battlegear2/packet/BattlegearSyncItemPacket";

    @Override
	void processMethod(MethodNode method) {

        sendPatchLog("tryStartTrackingEntity");
        InsnList newList = new InsnList();
        ListIterator<AbstractInsnNode> insn = method.instructions.iterator();
        boolean done = false;
        AbstractInsnNode next;
        while(insn.hasNext()){
            next = insn.next();
            if(!done &&
                    next instanceof TypeInsnNode &&
                    next.getOpcode() == CHECKCAST &&
                    ((TypeInsnNode) next).desc.equals(entityPlayerClassName)){

                newList.add(next);
                newList.add(insn.next());
                newList.add(insn.next());

                newList.add(new VarInsnNode(ALOAD, 1));
                newList.add(new FieldInsnNode(GETFIELD, entityPlayerMPClassName, playerMPplayerNetServerHandlerField, "L"+netServerHandlerClasName +";"));
                newList.add(new TypeInsnNode(NEW, syncPacket));
                newList.add(new InsnNode(DUP));
                newList.add(new VarInsnNode(ALOAD, 10));
                newList.add(new MethodInsnNode(INVOKESPECIAL, syncPacket, "<init>", "(L"+entityPlayerClassName+";)V"));
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, syncPacket, "generatePacket", "()Lcpw/mods/fml/common/network/internal/FMLProxyPacket;"));
                newList.add(new MethodInsnNode(INVOKEVIRTUAL, netServerHandlerClasName, sendPacketToPlayerMethodName, sendPacketToPlayerMethodDesc));
                done = true;
            }else{
                newList.add(next);
            }

        }
        method.instructions = newList;
    }

	@Override
	void setupMappings() {
		super.setupMappings();
		 entityPlayerClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayer");
         entityPlayerMPClassName = BattlegearTranslator.getMapedClassName("entity.player.EntityPlayerMP");
         netServerHandlerClasName =BattlegearTranslator.getMapedClassName("network.NetHandlerPlayServer");
         String packetClassName = BattlegearTranslator.getMapedClassName("network.Packet");

         playerMPplayerNetServerHandlerField = BattlegearTranslator.getMapedFieldName("EntityPlayerMP", "field_71135_a", "playerNetServerHandler");

         sendPacketToPlayerMethodName = BattlegearTranslator.getMapedMethodName("NetHandlerPlayServer", "func_147359_a", "sendPacket");
         sendPacketToPlayerMethodDesc = BattlegearTranslator.getMapedMethodDesc("NetHandlerPlayServer", "func_147359_a", "(L"+packetClassName+";)V");
	}
}
