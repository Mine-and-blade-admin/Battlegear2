package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlemodeHookContainerClass;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public final class OffhandPlaceBlockPacket extends AbstractMBPacket{
    public static final String packetName = "MB2|Place";
    private int xPosition;
    private int yPosition;
    private int zPosition;
    /** The offset to use for block/item placement. */
    private int direction;
    private ItemStack itemStack;
    /** The offset from xPosition where the actual click took place */
    private float xOffset;
    /** The offset from yPosition where the actual click took place */
    private float yOffset;
    /** The offset from zPosition where the actual click took place */
    private float zOffset;

    public OffhandPlaceBlockPacket() {}

    public OffhandPlaceBlockPacket(ItemStack itemStack) {
        this(-1, -1, -1, 255, itemStack, 0.0F, 0.0F, 0.0F);
    }

    public OffhandPlaceBlockPacket(BlockPos pos, EnumFacing face, ItemStack par5ItemStack, float par6, float par7, float par8) {
        this(pos.getX(), pos.getY(), pos.getZ(), face.getIndex(), par5ItemStack, par6, par7, par8);
    }

    private OffhandPlaceBlockPacket(int par1, int par2, int par3, int par4, ItemStack par5ItemStack, float par6, float par7, float par8) {
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.direction = par4;
        this.itemStack = par5ItemStack.copy();
        this.xOffset = par6;
        this.yOffset = par7;
        this.zOffset = par8;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(this.xPosition);
        out.writeByte(this.yPosition);
        out.writeInt(this.zPosition);
        out.writeByte(this.direction);
        ByteBufUtils.writeItemStack(out, this.itemStack);
        out.writeByte((int)(this.xOffset * 16.0F));
        out.writeByte((int)(this.yOffset * 16.0F));
        out.writeByte((int)(this.zOffset * 16.0F));
    }

    @Override
    public void process(ByteBuf in, EntityPlayer player) {
        try{
            this.xPosition = in.readInt();
            this.yPosition = in.readUnsignedByte();
            this.zPosition = in.readInt();
            this.direction = in.readUnsignedByte();
            this.itemStack = ByteBufUtils.readItemStack(in);
            this.xOffset = (float)in.readUnsignedByte() / 16.0F;
            this.yOffset = (float)in.readUnsignedByte() / 16.0F;
            this.zOffset = (float)in.readUnsignedByte() / 16.0F;
        }catch(Exception io){
            return;
        }
        if(player == null || !(player instanceof EntityPlayerMP) || !BattlegearUtils.isPlayerInBattlemode(player))
            return;
        ItemStack offhandWeapon = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
        final BlockPos pos = new BlockPos(xPosition, yPosition, zPosition);
        final EnumFacing l = EnumFacing.getFront(direction);
        ((EntityPlayerMP) player).markPlayerActive();
        if (direction == 255) {
            if (offhandWeapon.isEmpty() || player.isSpectator())
                return;
            PlayerInteractEvent.RightClickItem event = new PlayerInteractEvent.RightClickItem(player, EnumHand.OFF_HAND);
            //BukkitWrapper.callBukkitInteractEvent(event, offhandWeapon);
            MinecraftForge.EVENT_BUS.post(new PlayerEventChild.UseOffhandItemEvent(event));
            if (!event.isCanceled()) {
                EnumActionResult placeResult = BattlemodeHookContainerClass.tryUseItem(player, offhandWeapon, Side.SERVER);
                offhandWeapon = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

                if (offhandWeapon.isEmpty()){
                    BattlegearUtils.setPlayerOffhandItem(player, ItemStack.EMPTY);
                    offhandWeapon = ItemStack.EMPTY;
                }
                if (offhandWeapon.isEmpty() || offhandWeapon.getMaxItemUseDuration() == 0)
                {
                    ((EntityPlayerMP) player).isChangingQuantityOnly = true;
                    BattlegearUtils.setPlayerOffhandItem(player, ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon().copy());
                    player.openContainer.detectAndSendChanges();
                    ((EntityPlayerMP) player).isChangingQuantityOnly = false;

                    if (!ItemStack.areItemStacksEqual(((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon(), this.itemStack) || placeResult != EnumActionResult.FAIL)
                    {
                        Battlegear.packetHandler.sendPacketToPlayer(new BattlegearSyncItemPacket(player).generatePacket(), (EntityPlayerMP) player);
                    }
                }
            }
        }
        else {
            ((EntityPlayerMP) player).connection.processTryUseItemOnBlock(new CPacketPlayerTryUseItemOnBlock(){
                @Override
                public BlockPos getPos()
                {
                    return pos;
                }
                @Override
                public EnumFacing getDirection()
                {
                    return l;
                }
                @Override
                public EnumHand getHand()
                {
                    return EnumHand.OFF_HAND;
                }
                @Override
                public float getFacingX()
                {
                    return xOffset;
                }
                @Override
                public float getFacingY()
                {
                    return yOffset;
                }
                @Override
                public float getFacingZ()
                {
                    return zOffset;
                }
            });
        }
    }
}
