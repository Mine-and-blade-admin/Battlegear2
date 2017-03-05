package mods.battlegear2.client;

import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlemodeHookContainerClass;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.gui.BattleEquipGUI;
import mods.battlegear2.client.gui.BattlegearSigilGUI;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.BattlegearShieldBlockPacket;
import mods.battlegear2.packet.ReachTargetPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.lwjgl.input.Keyboard;

public final class BattlegearClientTickHandeler {
    private final KeyBinding battleInv, openSigilEditor;
    private static final int FLASH_MAX = 30;
    private final KeyBinding drawWeapons;
    private final Minecraft mc;

    private float blockBar = 1;
    private float partialTick;
    private boolean wasBlocking = false;
    private int previousBattlemode = InventoryPlayerBattle.OFFSET;
    private int previousNormal = 0;
    private int flashTimer;
    private boolean drawDone = false, inBattle = false;
    public static final BattlegearClientTickHandeler INSTANCE = new BattlegearClientTickHandeler();

    private BattlegearClientTickHandeler(){
        if(BattlegearConfig.enableGUIKeys) {
            battleInv = new KeyBinding("Battle Inventory", KeyConflictContext.IN_GAME, Keyboard.KEY_I, "key.categories.inventory");
            openSigilEditor = new KeyBinding("Open Sigil Editor", KeyConflictContext.IN_GAME, Keyboard.KEY_P, "key.categories.misc");
            ClientRegistry.registerKeyBinding(battleInv);
            ClientRegistry.registerKeyBinding(openSigilEditor);
        }else{
            battleInv = null;
            openSigilEditor = null;
        }
        drawWeapons = new KeyBinding("key.drawWeapons", KeyConflictContext.IN_GAME, Keyboard.KEY_R, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(drawWeapons);
        mc = FMLClientHandler.instance().getClient();
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()) {
            doKey(Keyboard.getEventKey());
        }
    }

    @SubscribeEvent
    public void mouseClick(MouseEvent mouseEvent){
        if (mouseEvent.isButtonstate()) {
            if(doKey(mouseEvent.getButton() - 100)){
                mouseEvent.setCanceled(true);
            }
        }
    }

    private boolean doKey(int key) {
        if(Battlegear.battlegearEnabled){
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc.player != null && mc.world != null && mc.currentScreen == null) {
                if(key == mc.gameSettings.keyBindPickBlock.getKeyCode()){
                    if(!mc.player.isSpectator() && ((IBattlePlayer) mc.player).isBattlemode()){
                        KeyBinding.setKeyBindState(key, false);
                        while(mc.gameSettings.keyBindPickBlock.isPressed());//Exhaust key press to prevent pick block
                        return true;
                    }
                }
                else if(key == mc.gameSettings.keyBindSwapHands.getKeyCode()){
                    ItemStack quiver = QuiverArrowRegistry.getArrowContainer(mc.player);
                    if (((IBattlePlayer) mc.player).getSpecialActionTimer() == 0) {
                        if (!quiver.isEmpty()) {
                            /*FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, mc.player).generatePacket();
                            Battlegear.packetHandler.sendPacketToServer(p);*/
                            ((IBattlePlayer) mc.player).setSpecialActionTimer(2);
                        } else if (((IBattlePlayer) mc.player).isBattlemode()) {
                            ItemStack offhand = mc.player.getHeldItemOffhand();

                            if (offhand.getItem() instanceof IShield) {
                                float shieldBashPenalty = 0.33F - 0.06F * EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashWeight, offhand);

                                if (BattlegearClientTickHandeler.getBlockTime() >= shieldBashPenalty) {
                                    FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, mc.player).generatePacket();
                                    Battlegear.packetHandler.sendPacketToServer(p);
                                    ((IBattlePlayer) mc.player).setSpecialActionTimer(((IShield) offhand.getItem()).getBashTimer(offhand));
                                    BattlegearClientTickHandeler.reduceBlockTime(shieldBashPenalty);
                                }
                            }
                        }
                    }
                    if (!quiver.isEmpty() || ((IBattlePlayer) mc.player).isBattlemode()) {
                        KeyBinding.setKeyBindState(key, false);
                        while (mc.gameSettings.keyBindSwapHands.isPressed()) ;//Exhaust key press to prevent swap hand
                        return true;
                    }
                }
                else if(BattlegearConfig.enableGUIKeys){
                    if (key == battleInv.getKeyCode()) {
                        BattleEquipGUI.open(mc.player);
                    } else if (key == openSigilEditor.getKeyCode()) {
                        BattlegearSigilGUI.open(mc.player);
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(Battlegear.battlegearEnabled){
            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc.player != null && mc.world != null && mc.currentScreen == null) {
                EntityPlayer player = mc.player;
                if(event.phase == TickEvent.Phase.START) {
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
                    tickEnd(player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player == mc.player) {
            if (event.phase == TickEvent.Phase.START) {
                if(inBattle && !((IBattlePlayer) mc.player).isBattlemode()){
                    for (int i = 0; i < InventoryPlayerBattle.WEAPON_SETS; ++i){
                        if (mc.gameSettings.keyBindsHotbar[i].isKeyDown()) {
                            previousBattlemode = InventoryPlayerBattle.OFFSET + i;
                        }
                    }
                    mc.player.inventory.currentItem = previousBattlemode;
                    mc.playerController.syncCurrentPlayItem();
                }
                tickStart(mc.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Battlegear.battlegearEnabled = false;
    }

    private void tickStart(EntityPlayer player) {
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

    private void tickEnd(EntityPlayer player) {
        ItemStack offhand = player.getHeldItemOffhand();
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
