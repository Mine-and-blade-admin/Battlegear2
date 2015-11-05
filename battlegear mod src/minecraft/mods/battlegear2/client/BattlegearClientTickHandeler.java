package mods.battlegear2.client;

import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlemodeHookContainerClass;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.IOffhandRender;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.renderer.LayerOffhandItem;
import mods.battlegear2.client.renderer.LayerQuiver;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import mods.battlegear2.packet.OffhandPlaceBlockPacket;
import mods.battlegear2.packet.ReachTargetPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BattlegearClientTickHandeler {
    private static final int FLASH_MAX = 30;
    private final KeyBinding drawWeapons, special;
    private final Minecraft mc;

    private float blockBar = 1;
    private float partialTick;
    private boolean wasBlocking = false;
    private int previousBattlemode = InventoryPlayerBattle.OFFSET;
    private int previousNormal = 0;
    private int flashTimer;
    private boolean specialDone = false, drawDone = false, inBattle = false;
    private List<RenderPlayer> renderPlayer = new ArrayList<RenderPlayer>();
    public static final BattlegearClientTickHandeler INSTANCE = new BattlegearClientTickHandeler();

    private BattlegearClientTickHandeler(){
        drawWeapons = new KeyBinding("key.drawWeapons", Keyboard.KEY_R, "key.categories.gameplay");
        special = new KeyBinding("key.special", Keyboard.KEY_Z, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(drawWeapons);
        ClientRegistry.registerKeyBinding(special);
        mc = FMLClientHandler.instance().getClient();
    }

    @SubscribeEvent
    public void keyDown(TickEvent.ClientTickEvent event) {
        if(Battlegear.battlegearEnabled){
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {
                EntityPlayer player = mc.thePlayer;
                if(event.phase == TickEvent.Phase.START) {
                    if (!specialDone && special.isKeyDown() && ((IBattlePlayer) player).getSpecialActionTimer() == 0) {
                        ItemStack quiver = QuiverArrowRegistry.getArrowContainer(player);

                        if (quiver != null) {
                            FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player).generatePacket();
                            Battlegear.packetHandler.sendPacketToServer(p);
                            ((IBattlePlayer) player).setSpecialActionTimer(2);
                        } else if (((IBattlePlayer) player).isBattlemode()) {
                            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();

                            if (offhand != null && offhand.getItem() instanceof IShield) {
                                float shieldBashPenalty = 0.33F - 0.06F * EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashWeight, offhand);

                                if (blockBar >= shieldBashPenalty) {
                                    FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player).generatePacket();
                                    Battlegear.packetHandler.sendPacketToServer(p);
                                    ((IBattlePlayer) player).setSpecialActionTimer(((IShield) offhand.getItem()).getBashTimer(offhand));

                                    blockBar -= shieldBashPenalty;
                                }
                            }
                        }
                        specialDone = true;
                    } else if (specialDone && !special.isKeyDown()) {
                        specialDone = false;
                    }
                    if (!drawDone && drawWeapons.isKeyDown()) {
                        if (((IBattlePlayer) player).isBattlemode()) {
                            previousBattlemode = player.inventory.currentItem;
                            player.inventory.currentItem = previousNormal;
                        } else {
                            previousNormal = player.inventory.currentItem;
                            player.inventory.currentItem = previousBattlemode;
                        }
                        mc.playerController.syncCurrentPlayItem();
                        drawDone = true;
                    } else if (drawDone && !drawWeapons.isKeyDown()) {
                        drawDone = false;
                    }
                    inBattle = ((IBattlePlayer) player).isBattlemode();
                }else {
                    if(inBattle && !((IBattlePlayer) player).isBattlemode()){
                        for (int i = 0; i < InventoryPlayerBattle.WEAPON_SETS; ++i){
                            if (mc.gameSettings.keyBindsHotbar[i].isKeyDown()) {
                                previousBattlemode = InventoryPlayerBattle.OFFSET + i;
                            }
                        }
                        player.inventory.currentItem = previousBattlemode;
                        mc.playerController.syncCurrentPlayItem();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player == mc.thePlayer) {
            if (event.phase == TickEvent.Phase.START) {
                tickStart(mc.thePlayer);
            } else {
                tickEnd(mc.thePlayer);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Battlegear.battlegearEnabled = false;
    }

    public void tickStart(EntityPlayer player) {
        if (renderPlayer.isEmpty()) {
            try {
                Map<String, RenderPlayer> map = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, mc.getRenderManager(), "skinMap", "field_178636_l");
                renderPlayer.addAll(map.values());
                for (RenderPlayer render : renderPlayer) {
                    render.layerRenderers.add(new LayerOffhandItem(render));
                    if (BattlegearConfig.hasRender("quiver"))
                        render.layerRenderers.add(new LayerQuiver(render));
                }
            } catch (Throwable ignored) {
            }
        }
        if(((IBattlePlayer)player).isBattlemode()){
            ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
            if(offhand != null){
                if(offhand.getItem() instanceof IShield){
                    if(flashTimer == FLASH_MAX){
                        player.motionY = player.motionY/2;
                    }
                    if(flashTimer > 0){
                        flashTimer --;
                    }
                    if (mc.gameSettings.keyBindUseItem.isKeyDown() && !player.isSwingInProgress) {
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
                } else if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.rightClickDelayTimer == 4 && !player.isUsingItem()) {
                    tryCheckUseItem(offhand, player);
                }
            }
        }
    }

    public void tryCheckUseItem(ItemStack offhand, EntityPlayer player){
        MovingObjectPosition mouseOver = mc.objectMouseOver;
        boolean flag = true;
        if (mouseOver != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
            BlockPos pos = mouseOver.getBlockPos();
            if (!player.worldObj.getBlockState(pos).getBlock().isAir(player.worldObj, pos)) {
                final int size = offhand.stackSize;
                EnumFacing i1 = mouseOver.sideHit;
                PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, pos, i1, player.worldObj), offhand);
                if (!MinecraftForge.EVENT_BUS.post(useItemEvent)){
                    BattlegearUtils.refreshAttributes(player, false);
                    boolean result = onPlayerPlaceBlock(mc.playerController, player, useItemEvent.offhand, pos, i1, mouseOver.hitVec);
                    BattlegearUtils.refreshAttributes(player, true);
                    if(result) {
                        if (useItemEvent.swingOffhand)
                            BattlegearUtils.sendOffSwingEvent(useItemEvent.event, useItemEvent.offhand);
                        flag = false;
                    }
                }
                if (useItemEvent.offhand.stackSize == 0){
                    BattlegearUtils.setPlayerOffhandItem(player, null);
                }else if (useItemEvent.offhand.stackSize != size || mc.playerController.isInCreativeMode()){
                    ((IOffhandRender)mc.entityRenderer.itemRenderer).setEquippedProgress(0.0F);
                }
            }
        }
        if (flag){
            offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
            PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, new BlockPos(0, 0, 0), null, player.worldObj), offhand);
            if (offhand != null && !MinecraftForge.EVENT_BUS.post(useItemEvent)){
                if (!mc.playerController.isSpectatorMode()) {
                    Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(useItemEvent.offhand).generatePacket());
                    if(useItemEvent.event.useItem != Event.Result.DENY) {
                        BattlegearUtils.refreshAttributes(player, false);
                        flag = BattlemodeHookContainerClass.tryUseItem(player, useItemEvent.offhand, Side.CLIENT);
                        BattlegearUtils.refreshAttributes(player, true);
                    }
                    if (flag) {
                        if (useItemEvent.swingOffhand)
                            BattlegearUtils.sendOffSwingEvent(useItemEvent.event, useItemEvent.offhand);
                        ((IOffhandRender) mc.entityRenderer.itemRenderer).setEquippedProgress(0.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            partialTick = event.renderTickTime;
            if(mc.currentScreen instanceof GuiMainMenu){
                Battlegear.battlegearEnabled = false;
                if (!renderPlayer.isEmpty()) {
                    for (RenderPlayer render : renderPlayer) {
                        Iterator iterator = render.layerRenderers.iterator();
                        while (iterator.hasNext()) {
                            Object object = iterator.next();
                            if (object instanceof LayerOffhandItem || object instanceof LayerQuiver) {
                                iterator.remove();
                            }
                        }
                    }
                    renderPlayer.clear();
                }
            }
        }
    }

    private boolean onPlayerPlaceBlock(PlayerControllerMP controller, EntityPlayer player, ItemStack offhand, BlockPos pos, EnumFacing l, Vec3 hitVec) {
        final World worldObj = player.worldObj;
        if (!worldObj.getWorldBorder().contains(pos)) {
            return false;
        }
        float f = (float) hitVec.xCoord - (float) pos.getX();
        float f1 = (float) hitVec.yCoord - (float) pos.getY();
        float f2 = (float) hitVec.zCoord - (float) pos.getZ();
        boolean flag = false;
        if (!controller.isSpectatorMode()) {
            if (offhand.getItem().onItemUseFirst(offhand, player, worldObj, pos, l, f, f1, f2)) {
                return true;
            }
            if (!player.isSneaking() || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem().doesSneakBypassUse(worldObj, pos, player)) {
                IBlockState b = worldObj.getBlockState(pos);
                if (!b.getBlock().isAir(worldObj, pos) && b.getBlock().onBlockActivated(worldObj, pos, b, player, l, f, f1, f2)) {
                    flag = true;
                }
            }
            if (!flag && offhand.getItem() instanceof ItemBlock) {
                ItemBlock itemblock = (ItemBlock) offhand.getItem();
                if (!itemblock.canPlaceBlockOnSide(worldObj, pos, l, player, offhand)) {
                    return false;
                }
            }
        }
        Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(pos, l, offhand, f, f1, f2).generatePacket());
        if (flag || controller.isSpectatorMode()) {
            return true;
        }
        else if (offhand == null){
            return false;
        }
        else{
            if (controller.isInCreativeMode()){
                int i1 = offhand.getMetadata();
                int j1 = offhand.stackSize;
                boolean flag1 = offhand.onItemUse(player, worldObj, pos, l, f, f1, f2);
                offhand.setItemDamage(i1);
                offhand.stackSize = j1;
                return flag1;
            }
            else{
                if (!offhand.onItemUse(player, worldObj, pos, l, f, f1, f2)) {
                    return false;
                }
                if (offhand.stackSize <= 0){
                    ForgeEventFactory.onPlayerDestroyItem(player, offhand);
                }
                return true;
            }
        }
    }

    public void tickEnd(EntityPlayer player) {
        ItemStack offhand = ((InventoryPlayerBattle) player.inventory).getCurrentOffhandWeapon();
        Battlegear.proxy.tryUseDynamicLight(player, offhand);
        //If we use a shield
        if(offhand != null && offhand.getItem() instanceof IShield){
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !player.isSwingInProgress && blockBar > 0) {
                player.motionX = player.motionX/5;
                player.motionZ = player.motionZ/5;
            }
        }

        //If we JUST swung an Item
        if (player.swingProgressInt == 1 && !player.isSpectator()) {
            double extendedReach = BattlemodeHookContainerClass.INSTANCE.maxReachDistance(player);
            if (extendedReach > BattlemodeHookContainerClass.defaultReachDistance(player.capabilities.isCreativeMode)) {
                MovingObjectPosition mouseOver = Battlegear.proxy.getMouseOver(extendedReach);
                if (mouseOver != null && mouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    Entity target = mouseOver.entityHit;
                    if (target instanceof EntityLivingBase && target != player && player.getDistanceToEntity(target) > mc.playerController.getBlockReachDistance()) {
                        if (target.hurtResistantTime != ((EntityLivingBase) target).maxHurtResistantTime) {
                            player.attackTargetEntityWithCurrentItem(target);
                            //send packet
                            Battlegear.packetHandler.sendPacketToServer(new ReachTargetPacket(target).generatePacket());
                        }
                    }
                }
            }
        }
    }

    public static void resetFlash(){
        INSTANCE.flashTimer = FLASH_MAX;
    }

    public static int getFlashTimer(){
        return INSTANCE.flashTimer;
    }

    public static float getBlockTime(){
        return INSTANCE.blockBar;
    }

    public static void reduceBlockTime(float value){
        INSTANCE.blockBar -= value;
    }

    public static float getPartialTick(){
        return INSTANCE.partialTick;
    }

    public static ItemStack getPreviousMainhand(EntityPlayer player){
        return player.inventory.getStackInSlot(INSTANCE.previousBattlemode);
    }

    public static ItemStack getPreviousOffhand(EntityPlayer player){
        return player.inventory.getStackInSlot(INSTANCE.previousBattlemode+InventoryPlayerBattle.WEAPON_SETS);
    }
}
