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
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import mods.battlegear2.packet.OffhandPlaceBlockPacket;
import mods.battlegear2.packet.ReachTargetPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

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
    public static final BattlegearClientTickHandeler INSTANCE = new BattlegearClientTickHandeler();

    private BattlegearClientTickHandeler(){
        drawWeapons = new KeyBinding("key.drawWeapons", KeyConflictContext.IN_GAME, Keyboard.KEY_R, "key.categories.gameplay");
        special = new KeyBinding("key.special", KeyConflictContext.IN_GAME, Keyboard.KEY_Z,"key.categories.gameplay");
        ClientRegistry.registerKeyBinding(drawWeapons);
        ClientRegistry.registerKeyBinding(special);
        mc = FMLClientHandler.instance().getClient();
    }

    @SubscribeEvent
    public void keyDown(TickEvent.ClientTickEvent event) {
        if(Battlegear.battlegearEnabled){
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc.player != null && mc.world != null && mc.currentScreen == null) {
                EntityPlayer player = mc.player;
                if(event.phase == TickEvent.Phase.START) {
                    if (!specialDone && special.isKeyDown() && ((IBattlePlayer) player).getSpecialActionTimer() == 0) {
                        ItemStack quiver = QuiverArrowRegistry.getArrowContainer(player);

                        if (!quiver.isEmpty()) {
                            FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player).generatePacket();
                            Battlegear.packetHandler.sendPacketToServer(p);
                            ((IBattlePlayer) player).setSpecialActionTimer(2);
                        } else if (((IBattlePlayer) player).isBattlemode()) {
                            ItemStack offhand = player.getHeldItemOffhand();

                            if (offhand.getItem() instanceof IShield) {
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
                        } else if(!player.isSpectator()){
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
        if(event.player == mc.player) {
            if (event.phase == TickEvent.Phase.START) {
                tickStart(mc.player);
            } else {
                tickEnd(mc.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Battlegear.battlegearEnabled = false;
    }

    public void tickStart(EntityPlayer player) {
        if(((IBattlePlayer)player).isBattlemode()){
            ItemStack offhand = player.getHeldItemOffhand();
            if(!offhand.isEmpty()){
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
                } else if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.rightClickDelayTimer == 4 && !player.isHandActive()) {
                    tryCheckUseItem(offhand, player);
                }
            }
        }
    }

    public void tryCheckUseItem(ItemStack offhand, EntityPlayer player){
        RayTraceResult mouseOver = mc.objectMouseOver;
        boolean flag = true;
        if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.BLOCK){
            BlockPos pos = mouseOver.getBlockPos();
            if (!player.world.isAirBlock(pos)) {
                final int size = offhand.getCount();
                EnumFacing i1 = mouseOver.sideHit;
                PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent.RightClickBlock(player, EnumHand.OFF_HAND, pos, i1, mouseOver.hitVec));
                if (!MinecraftForge.EVENT_BUS.post(useItemEvent)){
                    boolean result = onPlayerPlaceBlock(mc.playerController, player, useItemEvent.offhand, pos, i1, mouseOver.hitVec);
                    if(result) {
                        if (useItemEvent.swingOffhand)
                            BattlegearUtils.sendOffSwingEvent(useItemEvent.event);
                        flag = false;
                    }
                }
                if (useItemEvent.offhand.isEmpty()){
                    BattlegearUtils.setPlayerOffhandItem(player, ItemStack.EMPTY);
                }else if (useItemEvent.offhand.getCount() != size || mc.playerController.isInCreativeMode()){
                    ((IOffhandRender)mc.entityRenderer.itemRenderer).setEquippedProgress(0.0F);
                }
            }
        }
        if (flag){
            offhand = player.getHeldItemOffhand();
            PlayerEventChild.UseOffhandItemEvent useItemEvent = new PlayerEventChild.UseOffhandItemEvent(new PlayerInteractEvent.RightClickItem(player, EnumHand.OFF_HAND));
            if (!offhand.isEmpty() && !MinecraftForge.EVENT_BUS.post(useItemEvent)){
                if (!mc.playerController.isSpectatorMode()) {
                    Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(useItemEvent.offhand).generatePacket());
                    EnumActionResult result = BattlemodeHookContainerClass.tryUseItem(player, useItemEvent.offhand, Side.CLIENT);
                    if (result != EnumActionResult.FAIL) {
                        if (useItemEvent.swingOffhand)
                            BattlegearUtils.sendOffSwingEvent(useItemEvent.event);
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
            }
        }
    }

    private boolean onPlayerPlaceBlock(PlayerControllerMP controller, EntityPlayer player, ItemStack offhand, BlockPos pos, EnumFacing l, Vec3d hitVec) {
        final World world = player.world;
        if (!world.getWorldBorder().contains(pos)) {
            return false;
        }
        float f = (float) hitVec.xCoord - (float) pos.getX();
        float f1 = (float) hitVec.yCoord - (float) pos.getY();
        float f2 = (float) hitVec.zCoord - (float) pos.getZ();
        boolean flag = false;
        if (!controller.isSpectatorMode()) {
            if (offhand.onItemUseFirst(player, world, pos, EnumHand.OFF_HAND, l, f, f1, f2) == EnumActionResult.SUCCESS) {
                return true;
            }
            if (!player.isSneaking() || player.getHeldItemOffhand().isEmpty() || player.getHeldItemOffhand().getItem().doesSneakBypassUse(offhand, world, pos, player)) {
                IBlockState b = world.getBlockState(pos);
                if (!b.getBlock().isAir(b, world, pos) && b.getBlock().onBlockActivated(world, pos, b, player, EnumHand.OFF_HAND, l, f, f1, f2)) {
                    flag = true;
                }
            }
            if (!flag && offhand.getItem() instanceof ItemBlock) {
                ItemBlock itemblock = (ItemBlock) offhand.getItem();
                if (!itemblock.canPlaceBlockOnSide(world, pos, l, player, offhand)) {
                    return false;
                }
            }
        }
        Battlegear.packetHandler.sendPacketToServer(new OffhandPlaceBlockPacket(pos, l, offhand, f, f1, f2).generatePacket());
        if (flag || controller.isSpectatorMode()) {
            return true;
        }
        else if (offhand.isEmpty()){
            return false;
        }
        else{
            if (controller.isInCreativeMode()){
                int i1 = offhand.getMetadata();
                int j1 = offhand.getCount();
                boolean flag1 = offhand.onItemUse(player, world, pos, EnumHand.OFF_HAND, l, f, f1, f2) == EnumActionResult.SUCCESS;
                offhand.setItemDamage(i1);
                offhand.setCount(j1);
                return flag1;
            }
            else{
                ItemStack copy = offhand.copy();
                if (offhand.onItemUse(player, world, pos, EnumHand.OFF_HAND, l, f, f1, f2) != EnumActionResult.SUCCESS) {
                    return false;
                }
                if (offhand.isEmpty()){
                    ForgeEventFactory.onPlayerDestroyItem(player, copy, EnumHand.OFF_HAND);
                }
                return true;
            }
        }
    }

    public void tickEnd(EntityPlayer player) {
        ItemStack offhand = player.getHeldItemOffhand();
        Battlegear.proxy.tryUseDynamicLight(player, offhand);
        //If we use a shield
        if(offhand.getItem() instanceof IShield){
            if (mc.gameSettings.keyBindUseItem.isKeyDown() && !player.isSwingInProgress && blockBar > 0) {
                player.motionX = player.motionX/5;
                player.motionZ = player.motionZ/5;
            }
        }

        //If we JUST swung an Item
        if (player.swingProgressInt == 1 && !player.isSpectator()) {
            double extendedReach = BattlemodeHookContainerClass.INSTANCE.maxReachDistance(player);
            if (extendedReach > BattlemodeHookContainerClass.defaultReachDistance(player.capabilities.isCreativeMode)) {
                RayTraceResult mouseOver = Battlegear.proxy.getMouseOver(extendedReach);
                if (mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
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
