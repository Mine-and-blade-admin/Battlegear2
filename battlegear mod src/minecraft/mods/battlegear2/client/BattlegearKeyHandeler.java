package mods.battlegear2.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.BowHookContainerClass2;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.BattlegearGUIPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import org.lwjgl.input.Keyboard;

import java.util.EnumSet;

public class BattlegearKeyHandeler extends KeyBindingRegistry.KeyHandler {

    public static KeyBinding battleInv = new KeyBinding("Battle Inventory", Keyboard.KEY_I);
    public static KeyBinding drawWeapons = new KeyBinding("Draw Weapons", Keyboard.KEY_R);
    public static KeyBinding special = new KeyBinding("Special", Keyboard.KEY_Z);

    //TODO: I will replace this with some sort of command in the future, this is primarily for testing.
    //public static KeyBinding openSigilEditor = new KeyBinding("Open Sigil Editor", Keyboard.KEY_P);

    private static int previousNormal = 0;
    public static int previousBattlemode = InventoryPlayerBattle.OFFSET;

    public BattlegearKeyHandeler() {
        //super(new KeyBinding[]{battleInv, drawWeapons, openSigilEditor}, new boolean[]{false, false, false});
        super(new KeyBinding[]{battleInv, drawWeapons, special}, new boolean[]{false, false, false});
    }

    @Override
    public String getLabel() {
        return "Battlegear2";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb,
                        boolean tickEnd, boolean isRepeat) {

        if(Battlegear.battlegearEnabled){
            Minecraft mc = FMLClientHandler.instance().getClient();

            //null checks to prevent any crash outside the world (and to make sure we have no screen open)
            if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {

                EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;


                if (kb.keyCode == special.keyCode &&
                        player.specialActionTimer == 0 &&
                        FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){

                    ItemStack main = player.getCurrentEquippedItem();
                    ItemStack quiver = BowHookContainerClass2.getArrowContainer(main, player);

                    if(quiver != null){
                        Packet250CustomPayload p = BattlegearAnimationPacket.generatePacket(EnumBGAnimations.SpecialAction, player.username);
                        PacketDispatcher.sendPacketToServer(p);
                        player.specialActionTimer = 2;
                    }else if(player.isBattlemode()){
                        ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();


                        if(offhand != null && offhand.getItem() instanceof IShield){

                            //TODO: Enchantments?
                            float shieldBashPenalty = 0.33F;

                            if(BattlegearClientTickHandeler.blockBar >= shieldBashPenalty){
                                Packet250CustomPayload p = BattlegearAnimationPacket.generatePacket(EnumBGAnimations.SpecialAction, player.username);
                                PacketDispatcher.sendPacketToServer(p);
                                player.specialActionTimer = ((IShield)offhand.getItem()).getBashTimer(offhand);

                                BattlegearClientTickHandeler.blockBar = BattlegearClientTickHandeler.blockBar - shieldBashPenalty;
                            }

                        }
                    }


                }else if (kb.keyCode == battleInv.keyCode) {

                    //send packet to open container on server
                    PacketDispatcher.sendPacketToServer(BattlegearGUIPacket.generatePacket(BattlegearGUIHandeler.equipID));
                    //Also open on client
                    player.openGui(
                            Battlegear.INSTANCE, BattlegearGUIHandeler.equipID, mc.theWorld,
                            (int) player.posX, (int) player.posY, (int) player.posZ);


                } else
                if (kb.keyCode == drawWeapons.keyCode && tickEnd) {

                    InventoryPlayer playerInventory = player.inventory;
                    if (player.isBattlemode()) {
                        //i'd use int bounds check (0-8) for the item, just in case
                        previousBattlemode = playerInventory.currentItem;
                        playerInventory.currentItem = previousNormal;

                    } else {
                        //i'd use int bounds check (0-8) for the item, just in case
                        previousNormal = playerInventory.currentItem;
                        playerInventory.currentItem = previousBattlemode;

                    }
                    mc.playerController.updateController();
                /*else if (kb.keyCode == openSigilEditor.keyCode) {
                    //send packet to open container on server
                    PacketDispatcher.sendPacketToServer(BattlegearGUIPacket.generatePacket(BattlegearGUIHandeler.sigilEditor));
                    player.openGui(
                            BattleGear.instance, BattlegearGUIHandeler.sigilEditor, mc.theWorld,
                            (int) player.posX, (int) player.posY, (int) player.posZ);
                }*/
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