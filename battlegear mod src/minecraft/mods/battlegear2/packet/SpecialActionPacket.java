package mods.battlegear2.packet;


import mods.battlegear2.Battlegear;
import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.api.IShield;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

public class SpecialActionPacket extends AbstractMBPacket{

    public static final String packetName = "MB2|Special";

    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            Entity targetHit = player.worldObj.getEntityByID(inputStream.readInt());
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));


            if(targetHit instanceof EntityLiving){

                ItemStack mainhand = targetPlayer.getCurrentEquippedItem();
                ItemStack offhand = ((InventoryPlayerBattle)targetPlayer.inventory).getCurrentOffhandWeapon();

                if(offhand != null && offhand.getItem() instanceof IShield){
                    double d0 = targetHit.posX - targetPlayer.posX;
                    double d1;

                    for (d1 = targetHit.posZ - targetPlayer.posZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D)
                    {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    ((EntityLiving) targetHit).knockBack(player, 0, -d0*10, -d1*10);

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
        try {
            outputStream.writeInt(entityHit.entityId);
            Packet.writeString(player.username, outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload(packetName, bos.toByteArray());

        return packet;
    }
}
