package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:40 PM
 */
public final class BattlegearSyncItemPacket extends AbstractMBPacket {

    public static final String packetName = "MB2|SyncItem";
	private String user;
	private InventoryPlayer inventory;
	private EntityPlayer player;

    public BattlegearSyncItemPacket(EntityPlayer player){
        this(player.getCommandSenderName(), player.inventory, player);
    }

    private BattlegearSyncItemPacket(String user, InventoryPlayer inventory, EntityPlayer player) {
        this.user = user;
        this.inventory = inventory;
        this.player = player;
    }

    public BattlegearSyncItemPacket() {
	}

	@Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.player = player.worldObj.getPlayerEntityByName(user);
        if(this.player!=null) {
            int current = inputStream.readInt();
            if(InventoryPlayerBattle.isValidSwitch(current))
                this.player.inventory.currentItem = current;
            if(player.worldObj.isRemote) {
                ItemStack temp = ByteBufUtils.readItemStack(inputStream);
                if(!ItemStack.areItemStacksEqual(this.player.getCurrentEquippedItem(), temp))
                    BattlegearUtils.setPlayerCurrentItem(this.player, temp);
                int length = inputStream.readInt();
                int previous = ((InventoryPlayerBattle) this.player.inventory).extraItems.length;
                if(length != previous) {
                    if(previous > length)
                        previous = length;
                    ItemStack[] change = new ItemStack[length];
                    System.arraycopy(((InventoryPlayerBattle) this.player.inventory).extraItems, 0, change, 0, previous);
                    ((InventoryPlayerBattle) this.player.inventory).extraItems = change;
                    ((InventoryPlayerBattle) this.player.inventory).resizeExtra();
                }
                for (int i = 0; i < length; i++) {
                    ItemStack stack = ByteBufUtils.readItemStack(inputStream);
                    if(!ItemStack.areItemStacksEqual(this.player.inventory.getStackInSlot(InventoryPlayerBattle.OFFSET + i), stack))
                        ((InventoryPlayerBattle) this.player.inventory).setInventorySlotContents(InventoryPlayerBattle.OFFSET + i, stack, false);
                }
            }
            else if(BattlegearUtils.isPlayerInBattlemode(this.player)) {//Using data sent only by client
                ItemStack inUse = ByteBufUtils.readItemStack(inputStream);
                int time = inputStream.readInt();
                if(inUse!=null && time>0) {
                    this.player.setItemInUse(inUse, time);
                }
            }
            ((IBattlePlayer) this.player).setSpecialActionTimer(0);
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, user);
        out.writeInt(inventory.currentItem);
        if(!player.worldObj.isRemote) {
            ByteBufUtils.writeItemStack(out, inventory.getCurrentItem());
            int max = ((InventoryPlayerBattle) this.player.inventory).extraItems.length;
            out.writeInt(max);
            for (int i = 0; i < max; i++) {
                ByteBufUtils.writeItemStack(out, inventory.getStackInSlot(i + InventoryPlayerBattle.OFFSET));
            }
        }
        else if(BattlegearUtils.isPlayerInBattlemode(player)){//client-side only thing
            ByteBufUtils.writeItemStack(out, player.getItemInUse());
        	out.writeInt(player.getItemInUseCount());
        }
	}
}