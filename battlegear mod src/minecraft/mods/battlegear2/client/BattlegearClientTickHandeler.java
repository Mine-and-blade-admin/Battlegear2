package mods.battlegear2.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import mods.battlegear2.packet.OffhandPlaceBlockPacket;
import mods.battlegear2.packet.PickBlockPacket;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class BattlegearClientTickHandeler {

    public static float blockBar = 1;
    public static boolean wasBlocking = false;
    public static final float[] COLOUR_DEFAULT = new float[]{0, 0.75F, 1};
    public static final float[] COLOUR_RED = new float[]{1, 0.1F, 0.1F};
    public static final float[] COLOUR_YELLOW = new float[]{1, 1F, 0.1F};
    public static int flashTimer;

    public static float partialTick;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            tickStart(event.player);
        }else{
            tickEnd(event.player);
        }
    }

    public void tickStart(EntityPlayer player) {
        if(!Battlegear.battlegearEnabled && ! player.worldObj.isRemote){
            Battlegear.battlegearEnabled = true;
        }

        if(((IBattlePlayer)player).isBattlemode()){
            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
            if(offhand != null){
                if(offhand.getItem() instanceof IShield){
                    if(flashTimer == 30){
                        player.motionY = player.motionY/2;
                    }
                    if(flashTimer > 0){
                        flashTimer --;
                    }
                    if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem) && !player.isSwingInProgress){
                        blockBar -= ((IShield) offhand.getItem()).getDecayRate(offhand);
                        if(blockBar > 0){
                            if(!wasBlocking){
                                Battlegear.packetHandler.sendPacketToServer(new BattlegearShieldBlockPacket(true, player).generatePacket());
                            }
                            wasBlocking = true;
                        }else{
                            if(wasBlocking){
                                //Send packet
                                Battlegear.packetHandler.sendPacketToServer(new BattlegearShieldBlockPacket(false, player).generatePacket());
                            }
                            wasBlocking = false;
                            blockBar = 0;
                        }
                    }else{
                        if(wasBlocking){
                            //send packet
                            Battlegear.packetHandler.sendPacketToServer(new BattlegearShieldBlockPacket(false, player).generatePacket());
                        }
                        wasBlocking = false;
                        blockBar += ((IShield) offhand.getItem()).getRecoveryRate(offhand);
                        if(blockBar > 1){
                            blockBar = 1;
                        }
                    }
                }else if(offhand.getItem() instanceof ItemBlock){
                    if(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed() && !player.isSwingInProgress){
                        MovingObjectPosition mouseOver = Minecraft.getMinecraft().objectMouseOver;

                        if (mouseOver == null)
                        {
                        }
                        else if (mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                        {
                            Minecraft.getMinecraft().playerController.interactWithEntitySendPacket(player, mouseOver.entityHit);
                        }
                        else if (mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                        {
                            int j = mouseOver.blockX;
                            int k = mouseOver.blockY;
                            int l = mouseOver.blockZ;
                            int i1 = mouseOver.sideHit;

                            boolean result = !ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, j, k, l, i1).isCanceled();
                            if (result && onPlayerPlaceBlock(Minecraft.getMinecraft().playerController, player, player.worldObj, offhand, j, k, l, i1, mouseOver.hitVec))
                            {
                                ((IBattlePlayer)player).swingOffItem();
                            }

                            if (offhand != null && offhand.stackSize == 0){
                                player.inventory.setInventorySlotContents(player.inventory.currentItem+ 3, null);
                            }
                        }
                    }
                }
            }
        }else{
            if(player.capabilities.isCreativeMode && Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getIsKeyPressed()){
                ItemStack stack = getItemFromPointedAt(Minecraft.getMinecraft().objectMouseOver, player.worldObj);
                if(stack!=null){
                    int k = -1;
                    ItemStack temp;
                    for(int slot=0; slot<player.inventory.getSizeInventory();slot++){
                        temp = player.inventory.getStackInSlot(slot);
                        if(temp!=null && stack.isItemEqual(temp)){
                            k = slot;
                            break;
                        }
                    }
                    if(k<0||k>=9){
                        k = player.inventory.getFirstEmptyStack();
                    }
                    if (k >= 0 && k < 9)
                    {
                        player.inventory.currentItem = k;
                        Battlegear.packetHandler.sendPacketToServer(new PickBlockPacket(player, stack, k).generatePacket());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event){
        if(event.phase== TickEvent.Phase.START){
            partialTick = event.renderTickTime;
            if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu){
                Battlegear.battlegearEnabled = false;
            }
        }
    }

    /**
     * Equivalent code to the creative pick block
     * @param objectMouseOver
     * @param theWorld
     * @return the stack expected for the creative pick button
     */
    private static ItemStack getItemFromPointedAt(MovingObjectPosition target, World world) {
        if(target!=null){
            if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int x = target.blockX;
                int y = target.blockY;
                int z = target.blockZ;
                Block block = world.getBlock(x, y, z);
                if (block.isAir(world, x, y, z))
                {
                    return null;
                }
                return block.getPickBlock(target, world, x, y, z);
            }
            else
            {
                if (target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || target.entityHit == null)
                {
                    return null;
                }
                return target.entityHit.getPickedResult(target);
            }
        }
        return null;
    }

    private static boolean onPlayerPlaceBlock(PlayerControllerMP controller, EntityPlayer player, World worldObj, ItemStack offhand, int i, int j, int k, int l, Vec3 hitVec) {
        float f = (float)hitVec.xCoord - (float)i;
        float f1 = (float)hitVec.yCoord - (float)j;
        float f2 = (float)hitVec.zCoord - (float)k;
        boolean flag = false;
        int i1;
        if (offhand.getItem().onItemUseFirst(offhand, player, worldObj, i, j, k, l, f, f1, f2)){
            return true;
        }
        if (!player.isSneaking() || offhand.getItem().doesSneakBypassUse(worldObj, i, j, k, player)){
            Block b = worldObj.getBlock(i, j, k);
            if (!b.isAir(worldObj, i, j, k) && b.onBlockActivated(worldObj, i, j, k, player, l, f, f1, f2)){
                flag = true;
            }
        }
        if (!flag){
            ItemBlock itemblock = (ItemBlock)offhand.getItem();
            if (!itemblock.func_150936_a(worldObj, i, j, k, l, player, offhand)){
                return false;
            }
        }
        if (flag){
            return true;
        }
        else if (offhand == null){
            return false;
        }
        else{
            Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(i, j, k, l, offhand, f, f1, f2).generatePacket());
            if (controller.isInCreativeMode()){
                i1 = offhand.getItemDamage();
                int j1 = offhand.stackSize;
                boolean flag1 = offhand.tryPlaceItemIntoWorld(player, worldObj, i, j, k, l, f, f1, f2);
                offhand.setItemDamage(i1);
                offhand.stackSize = j1;
                return flag1;
            }
            else{
                if (!offhand.tryPlaceItemIntoWorld(player, worldObj, i, j, k, l, f, f1, f2)){
                    return false;
                }
                if (offhand.stackSize <= 0){
                    player.inventory.setInventorySlotContents(player.inventory.currentItem+ 3, null);
                    ForgeEventFactory.onPlayerDestroyItem(player, offhand);
                }
                Battlegear.packetHandler.sendPacketToServer(new BattlegearSyncItemPacket(player).generatePacket());
                return true;
            }
        }
    }

    public void tickEnd(EntityPlayer player) {
        ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
        if(offhand != null && offhand.getItem() instanceof IShield){
            if(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed() && !player.isSwingInProgress && blockBar > 0){
                player.motionX = player.motionX/5;
                player.motionZ = player.motionZ/5;
            }
        }
    }
}
