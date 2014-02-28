package mods.battlegear2.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;

public class BattlegearKeyHandeler {

    public static KeyBinding drawWeapons = new KeyBinding("Draw Weapons", Keyboard.KEY_R, "key.categories.gameplay");
    public static KeyBinding special = new KeyBinding("Special", Keyboard.KEY_Z, "key.categories.gameplay");

    private static int previousNormal = 0;
    public static int previousBattlemode = InventoryPlayerBattle.OFFSET;

    public BattlegearKeyHandeler() {
        ClientRegistry.registerKeyBinding(drawWeapons);
        ClientRegistry.registerKeyBinding(special);
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {

        if(Battlegear.battlegearEnabled){
            Minecraft mc = FMLClientHandler.instance().getClient();

            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {

                EntityClientPlayerMP player = mc.thePlayer;
                if (Keyboard.getEventKey() == special.getKeyCode() && ((IBattlePlayer) player).getSpecialActionTimer() == 0){
                    ItemStack main = player.getCurrentEquippedItem();
                    ItemStack quiver = QuiverArrowRegistry.getArrowContainer(main, player);

                    if(quiver != null){
                        FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player).generatePacket();
                        Battlegear.packetHandler.sendPacketToServer(p);
                        ((IBattlePlayer) player).setSpecialActionTimer(2);
                    }else if(((IBattlePlayer) player).isBattlemode()){
                        ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();

                        if(offhand != null && offhand.getItem() instanceof IShield){
                            float shieldBashPenalty = 0.33F - 0.06F*EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashWeight.effectId, offhand);

                            if(BattlegearClientTickHandeler.blockBar >= shieldBashPenalty){
                                FMLProxyPacket p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player).generatePacket();
                                Battlegear.packetHandler.sendPacketToServer(p);
                                ((IBattlePlayer) player).setSpecialActionTimer(((IShield)offhand.getItem()).getBashTimer(offhand));

                                BattlegearClientTickHandeler.blockBar = BattlegearClientTickHandeler.blockBar - shieldBashPenalty;
                            }
                        }
                    }

                } else if (Keyboard.getEventKey() == drawWeapons.getKeyCode()) {
                    InventoryPlayer playerInventory = player.inventory;
                    if (((IBattlePlayer) player).isBattlemode()) {
                        previousBattlemode = playerInventory.currentItem;
                        playerInventory.currentItem = previousNormal;

                    } else {
                        previousNormal = playerInventory.currentItem;
                        playerInventory.currentItem = previousBattlemode;
                    }
                    mc.playerController.updateController();
                }
            }
        }
    }
}