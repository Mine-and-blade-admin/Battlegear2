package mods.battlegear2.packet;

import io.netty.buffer.ByteBuf;
import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlemodeHookContainerClass;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
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
        this.itemStack = ItemStack.copyItemStack(par5ItemStack);
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
        boolean placeResult = true;
        BlockPos pos = new BlockPos(xPosition, yPosition, zPosition);
        EnumFacing l = EnumFacing.getFront(direction);
        ((EntityPlayerMP) player).markPlayerActive();
        if (direction == 255){
            if (offhandWeapon == null)
                return;
            PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, new BlockPos(0, 0, 0), null, player.getEntityWorld());
            MinecraftForge.EVENT_BUS.post(new PlayerEventChild.UseOffhandItemEvent(event, offhandWeapon));
            if (event.useItem != Event.Result.DENY){
                if (((EntityPlayerMP) player).theItemInWorldManager.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    BattlegearUtils.refreshAttributes(player);
                    BattlemodeHookContainerClass.tryUseItem(player, offhandWeapon, Side.SERVER);
                    BattlegearUtils.refreshAttributes(player);
                }
            }
        }
        else {
            MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (yPosition >= mcServer.getBuildLimit() - 1 && (l == EnumFacing.UP || yPosition >= mcServer.getBuildLimit())) {
                ChatComponentTranslation chat = new ChatComponentTranslation("build.tooHigh", mcServer.getBuildLimit());
                chat.getChatStyle().setColor(EnumChatFormatting.RED);
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S02PacketChat(chat));
            } else {
                double dist = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance() + 1;
                dist *= dist;
                if (((EntityPlayerMP) player).playerNetServerHandler.hasMoved && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) < dist && !mcServer.isBlockProtected(player.getEntityWorld(), pos, player) && player.getEntityWorld().getWorldBorder().contains(pos)) {
                    placeResult = activateBlockOrUseItem((EntityPlayerMP) player, offhandWeapon, pos, l, xOffset, yOffset, zOffset);
                }
            }
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(player.getEntityWorld(), pos));
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S23PacketBlockChange(player.getEntityWorld(), pos.offset(l)));
        }
        offhandWeapon = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

        if (offhandWeapon != null && offhandWeapon.stackSize <= 0){
            BattlegearUtils.setPlayerOffhandItem(player, null);
            offhandWeapon = null;
        }
        if (offhandWeapon == null || offhandWeapon.getMaxItemUseDuration() == 0)
        {
            ((EntityPlayerMP) player).isChangingQuantityOnly = true;
            BattlegearUtils.setPlayerOffhandItem(player, ItemStack.copyItemStack(((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon()));
            player.openContainer.detectAndSendChanges();
            ((EntityPlayerMP) player).isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon(), this.itemStack) || !placeResult)
            {
                Battlegear.packetHandler.sendPacketToPlayer(new BattlegearSyncItemPacket(player).generatePacket(), (EntityPlayerMP) player);
            }
        }
    }

    /**
     * From ItemInWorldManager:
     * Activate the clicked on block, or use the given itemStack.
     */
    public boolean activateBlockOrUseItem(EntityPlayerMP playerMP, ItemStack itemStack, BlockPos pos, EnumFacing side, float xOffset, float yOffset, float zOffset)
    {
        World theWorld = playerMP.getEntityWorld();
        if (playerMP.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR) {
            return playerMP.theItemInWorldManager.activateBlockOrUseItem(playerMP, theWorld, itemStack, pos, side, xOffset, yOffset, zOffset);
        }
        PlayerInteractEvent event = new PlayerInteractEvent(playerMP, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, pos, side, theWorld);
        MinecraftForge.EVENT_BUS.post(new PlayerEventChild.UseOffhandItemEvent(event, itemStack));
        if (event.isCanceled())
        {
            playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
            return false;
        }

        BattlegearUtils.refreshAttributes(playerMP);
        if (itemStack != null && itemStack.getItem().onItemUseFirst(itemStack, playerMP, theWorld, pos, side, xOffset, yOffset, zOffset))
        {
            if (itemStack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(playerMP, itemStack);
            BattlegearUtils.refreshAttributes(playerMP);
            return true;
        }

        boolean useBlock = !playerMP.isSneaking() || playerMP.getHeldItem() == null;
        if (!useBlock) useBlock = playerMP.getHeldItem().getItem().doesSneakBypassUse(theWorld, pos, playerMP);
        boolean result = false;
        if (useBlock)
        {
            if (event.useBlock != Event.Result.DENY)
            {
                IBlockState state = theWorld.getBlockState(pos);
                result = state.getBlock().onBlockActivated(theWorld, pos, state, playerMP, side, xOffset, yOffset, zOffset);
            }
            else
            {
                playerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (itemStack != null && !result && event.useItem != Event.Result.DENY)
        {
            final int meta = itemStack.getMetadata();
            final int size = itemStack.stackSize;
            result = itemStack.onItemUse(playerMP, theWorld, pos, side, xOffset, yOffset, zOffset);
            if (playerMP.theItemInWorldManager.isCreative())
            {
                itemStack.setItemDamage(meta);
                itemStack.stackSize = size;
            }
            if (itemStack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(playerMP, itemStack);
        }
        BattlegearUtils.refreshAttributes(playerMP);
        return result;
    }

}
