package mods.battlegear2.client;

import java.util.EnumSet;

import mods.battlegear2.Battlegear;
import mods.battlegear2.BowHookContainerClass2;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.IShield;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class BattlegearKeyHandeler extends KeyBindingRegistry.KeyHandler {

    public static KeyBinding drawWeapons = new KeyBinding("Draw Weapons", Keyboard.KEY_R);
    public static KeyBinding special = new KeyBinding("Special", Keyboard.KEY_Z);

    private static int previousNormal = 0;
    public static int previousBattlemode = InventoryPlayerBattle.OFFSET;

    public BattlegearKeyHandeler() {
        super(new KeyBinding[]{drawWeapons, special}, new boolean[]{false, false});
    }

    @Override
    public String getLabel() {
        return "Battlegear2";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {

        if(Battlegear.battlegearEnabled){
            Minecraft mc = FMLClientHandler.instance().getClient();

            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {

                EntityClientPlayerMP player = mc.thePlayer;
                if(tickEnd && player instanceof IBattlePlayer){
	                if (kb.keyCode == special.keyCode && ((IBattlePlayer) player).getSpecialActionTimer() == 0){
	                    ItemStack main = player.getCurrentEquippedItem();
	                    ItemStack quiver = BowHookContainerClass2.getArrowContainer(main, player);
	
	                    if(quiver != null){
	                        Packet p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player.username).generatePacket();
	                        PacketDispatcher.sendPacketToServer(p);
                            ((IBattlePlayer) player).setSpecialActionTimer(2);
	                    }else if(((IBattlePlayer) player).isBattlemode()){
	                        ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();
	
	                        if(offhand != null && offhand.getItem() instanceof IShield){
	                            float shieldBashPenalty = 0.33F - 0.06F*EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bashWeight.effectId, offhand);
	
	                            if(BattlegearClientTickHandeler.blockBar >= shieldBashPenalty){
	                                Packet p = new BattlegearAnimationPacket(EnumBGAnimations.SpecialAction, player.username).generatePacket();
	                                PacketDispatcher.sendPacketToServer(p);
                                    ((IBattlePlayer) player).setSpecialActionTimer(((IShield)offhand.getItem()).getBashTimer(offhand));
	
	                                BattlegearClientTickHandeler.blockBar = BattlegearClientTickHandeler.blockBar - shieldBashPenalty;
	                            }
	                        }
	                    }
	
	                } else if (kb.keyCode == drawWeapons.keyCode) {
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

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

}