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
	private String user;
	private InventoryPlayer inventory;
	private EntityPlayer player;

    public BattlegearSyncItemPacket(EntityPlayer player){
        this(player.username, player.inventory, player);
    }

    public BattlegearSyncItemPacket(String user, InventoryPlayer inventory, EntityPlayer player) {
            this.user = user;
            this.inventory = inventory;
            this.player = player;
    }


    public BattlegearSyncItemPacket() {
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
	            if(!player.worldObj.isRemote){//Using data sent only by client
	            	targetPlayer.setItemInUse(Packet.readItemStack(inputStream), inputStream.readInt());
	            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(inputStream);
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Packet.writeString(user, out);
        out.writeInt(inventory.currentItem);
        Packet.writeItemStack(inventory.getCurrentItem(), out);

        for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
            Packet.writeItemStack(inventory.getStackInSlot(i + InventoryPlayerBattle.OFFSET), out);
        }
        if(player.worldObj.isRemote){//client-side only thing
        	Packet.writeItemStack(player.getItemInUse(), out);
        	out.writeInt(player.getItemInUseCount());
        }
	}
}