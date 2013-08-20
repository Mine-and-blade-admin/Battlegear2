package mods.battlegear2.packet;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.api.IShield;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.MathHelper;

import java.io.*;

public class SpecialActionPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|Special";

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        System.out.println("Process Special");
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            Entity targetHit = null;
            if(inputStream.readBoolean()){
                targetHit = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            }else{
                targetHit = player.worldObj.getEntityByID(inputStream.readInt());
            }

            System.out.println(targetHit);
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));

            if(targetHit instanceof EntityLivingBase){
                System.out.println("is Living");

                ItemStack mainhand = targetPlayer.getCurrentEquippedItem();
                ItemStack offhand = ((InventoryPlayerBattle)targetPlayer.inventory).getCurrentOffhandWeapon();

                if(offhand != null && offhand.getItem() instanceof IShield){
                    System.out.println("Bash");
                    double d0 = targetHit.posX - targetPlayer.posX;
                    double d1;

                    for (d1 = targetHit.posZ - targetPlayer.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D)
                    {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }


                    ((EntityLivingBase) targetHit).knockBack(player, 0, -d0, -d1);


                    if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER &&
                            targetHit instanceof EntityPlayer){
                        PacketDispatcher.sendPacketToPlayer(packet, (Player)targetHit);
                    }



                }else if(mainhand != null && offhand != null){
                    //This will be handeled elsewhere
                }else if (mainhand != null && mainhand.getItem() instanceof IBattlegearWeapon){

                }
                else if(mainhand != null){

                }


            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Packet250CustomPayload generatePacket(EntityPlayer player, ItemStack mainhand, ItemStack offhand, Entity entityHit) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(300);
        DataOutputStream outputStream = new DataOutputStream(bos);

        boolean isPlayer = entityHit instanceof EntityPlayer;


        try {
            outputStream.writeBoolean(isPlayer);
            if(isPlayer){
                Packet.writeString(((EntityPlayer) entityHit).username, outputStream);
            }else{
                outputStream.writeInt(entityHit.entityId);
            }

            Packet.writeString(player.username, outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload(packetName, bos.toByteArray());

        return packet;
    }
}
