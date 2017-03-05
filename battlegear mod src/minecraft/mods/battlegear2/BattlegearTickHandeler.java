package mods.battlegear2;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.packet.BattlegearSyncItemPacket;
import mods.battlegear2.packet.LoginPacket;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class BattlegearTickHandeler {
    public static final BattlegearTickHandeler INSTANCE = new BattlegearTickHandeler();

    private BattlegearTickHandeler(){}

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            tickStart(event.player);
        }else{
            tickEnd(event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP){
            Battlegear.packetHandler.sendPacketToPlayer(new LoginPacket().generatePacket(), (EntityPlayerMP)event.player);
        }
    }

    private void tickStart(EntityPlayer entityPlayer) {

        if (!entityPlayer.world.isRemote && entityPlayer.world instanceof WorldServer) {

            if(((InventoryPlayerBattle)entityPlayer.inventory).hasChanged){

                ((WorldServer)entityPlayer.world).getEntityTracker().sendToTrackingAndSelf(
                        entityPlayer, new BattlegearSyncItemPacket(entityPlayer).generatePacket()
                );

                ((IBattlePlayer)entityPlayer).setSpecialActionTimer(0);

                ((InventoryPlayerBattle)entityPlayer.inventory).hasChanged = entityPlayer.ticksExisted < 10;

            }
            //Force update every 3 seconds
            else if(((IBattlePlayer)entityPlayer).isBattlemode() && entityPlayer.ticksExisted % BattlegearConfig.updateRate == 0 && !entityPlayer.isHandActive()){
                ((WorldServer)entityPlayer.world).getEntityTracker().sendToTrackingAndSelf(
                        entityPlayer, new BattlegearSyncItemPacket(entityPlayer).generatePacket()
                );
            }

        }
    }

    private void tickEnd(EntityPlayer entityPlayer) {
        int timer = ((IBattlePlayer)entityPlayer).getSpecialActionTimer();
        if(timer > 0){
            ((IBattlePlayer)entityPlayer).setSpecialActionTimer(timer-1);
            int targetTime = -1;
            ItemStack offhand = entityPlayer.getHeldItemOffhand();
            if(offhand.getItem() instanceof IShield){
                targetTime = ((IShield) offhand.getItem()).getBashTimer(offhand) / 2;
            }else{
                offhand = QuiverArrowRegistry.getArrowContainer(entityPlayer);
                if(!offhand.isEmpty()) {
                    targetTime = 0;
                }
            }
            if(timer-1 == targetTime){
                Battlegear.proxy.doSpecialAction(entityPlayer, offhand);
            }
        }
    }
}
