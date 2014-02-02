package mods.battlegear2.packet;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

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
    public void process(DataInputStream inputStream, EntityPlayer player) {
        try {
        	this.player = player.worldObj.getPlayerEntityByName(Packet.readString(inputStream, 30));
            if(this.player!=null){
            	this.player.inventory.currentItem = inputStream.readInt();
	            BattlegearUtils.setPlayerCurrentItem(this.player, Packet.readItemStack(inputStream));
	
	            for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
	                ItemStack stack = Packet.readItemStack(inputStream);
	
	                //if(stack!=null){
	                this.player.inventory.setInventorySlotContents(InventoryPlayerBattle.OFFSET + i, stack);
	                //}
	            }
                ((IBattlePlayer) this.player).setSpecialActionTimer(0);
	            if(!player.worldObj.isRemote){//Using data sent only by client
	            	this.player.setItemInUse(Packet.readItemStack(inputStream), inputStream.readInt());
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