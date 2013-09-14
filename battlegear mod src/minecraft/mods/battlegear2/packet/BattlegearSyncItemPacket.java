package mods.battlegear2.packet;

import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:40 PM
 */
public class BattlegearSyncItemPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|SyncItem";

    public static Packet250CustomPayload generatePacket(EntityPlayer player){
        return generatePacket(player.username, player.inventory);
    }

    public static Packet250CustomPayload generatePacket(String user, InventoryPlayer inventory) {
        ByteArrayOutputStream bos = null;
        DataOutputStream outputStream = null;

        try {
            bos = new ByteArrayOutputStream();
            outputStream = new DataOutputStream(bos);

            Packet.writeString(user, outputStream);
            outputStream.writeInt(inventory.currentItem);
            Packet.writeItemStack(inventory.getCurrentItem(), outputStream);

            for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
                Packet.writeItemStack(inventory.getStackInSlot(i + InventoryPlayerBattle.OFFSET), outputStream);
            }

            return new Packet250CustomPayload(packetName, bos.toByteArray());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(outputStream);
        }



        return null;
    }


    @Override
    public void process(Packet250CustomPayload packet, EntityPlayer player) {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        try {
            EntityPlayer targetPlayer = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            if(targetPlayer!=null){
	            targetPlayer.inventory.currentItem = inputStream.readInt();
	            BattlegearUtils.setPlayerCurrentItem(targetPlayer, Packet.readItemStack(inputStream));
	
	            for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
	                ItemStack stack = Packet.readItemStack(inputStream);
	
	                //if(stack!=null){
	                targetPlayer.inventory.setInventorySlotContents(InventoryPlayerBattle.OFFSET + i, stack);
	                //}
	            }
	            targetPlayer.specialActionTimer = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }
}