package mods.battlegear2.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

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
        this(player.getCommandSenderName(), player.inventory, player);
    }

    public BattlegearSyncItemPacket(String user, InventoryPlayer inventory, EntityPlayer player) {
        this.user = user;
        this.inventory = inventory;
        this.player = player;
    }

    public BattlegearSyncItemPacket() {
	}

	@Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.player = player.worldObj.getPlayerEntityByName(ByteBufUtils.readUTF8String(inputStream));
        if(this.player!=null){
            this.player.inventory.currentItem = inputStream.readInt();
            BattlegearUtils.setPlayerCurrentItem(this.player, ByteBufUtils.readItemStack(inputStream));

            for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
                ItemStack stack = ByteBufUtils.readItemStack(inputStream);
                this.player.inventory.setInventorySlotContents(InventoryPlayerBattle.OFFSET + i, stack);
            }
            ((IBattlePlayer) this.player).setSpecialActionTimer(0);
            if(!player.worldObj.isRemote){//Using data sent only by client
                try {
                    this.player.setItemInUse(ByteBufUtils.readItemStack(inputStream), inputStream.readInt());
                }catch (Exception e){}
            }
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
        ByteBufUtils.writeItemStack(out, inventory.getCurrentItem());

        for (int i = 0; i < InventoryPlayerBattle.EXTRA_INV_SIZE; i++) {
            ByteBufUtils.writeItemStack(out, inventory.getStackInSlot(i + InventoryPlayerBattle.OFFSET));
        }
        if(player.worldObj.isRemote){//client-side only thing
            ByteBufUtils.writeItemStack(out, player.getItemInUse());
        	out.writeInt(player.getItemInUseCount());
        }
	}
}