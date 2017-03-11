package mods.battlegear2.events;

import mods.battlegear2.api.PlayerEventChild;
import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.RenderPlayerEventChild;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.quiver.SwapArrowEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "events_test", name = "Events")
public class EventsTest {
    private final boolean forge = true;
    private Logger log;

    @Mod.EventHandler
    public void loading(FMLPreInitializationEvent event){
        log = event.getModLog();
        if(forge)
            MinecraftForge.EVENT_BUS.register(this);
        if(event.getSide().isClient())
            BattlegearUtils.RENDER_BUS.register(new Client());
    }

    @SubscribeEvent
    public void onShielding(PlayerEventChild.ShieldBlockEvent shield){
        log.info("Shield " + shield.source);
    }

    @SubscribeEvent
    public void onAttack(PlayerEventChild.OffhandAttackEvent attack){
        log.info("Off attack " + attack.getTarget() + attack.swingOffhand);
    }

    @SubscribeEvent
    public void onUse(PlayerEventChild.UseOffhandItemEvent use){
        log.info("Off use " + use.swingOffhand);
    }

    @SubscribeEvent
    public void onSwing(PlayerEventChild.OffhandSwingEvent swing){
        log.info("Swing off " + swing.onEntity());
    }

    @SubscribeEvent
    public void onArrow(PlayerEventChild.QuiverArrowEvent quiver){
        log.info("Quiver arrow from" + quiver.getBow() + " at " + quiver.getCharge());
    }

    @SubscribeEvent
    public void onSwap(SwapArrowEvent swapping){
        log.info("Swapping from" + swapping.getCurrentArrow() + " to " + swapping.getNextArrow());
    }

    private class Client{

        @SubscribeEvent
        public void preRender(RenderPlayerEventChild.PreRenderPlayerElement render){
            log.info("Prerender " + render.type + " in " + render.isFirstPerson);
        }

        @SubscribeEvent
        public void postRender(RenderPlayerEventChild.PostRenderPlayerElement render){
            log.info("Postrender " + render.type + " in " + render.isFirstPerson);
        }

        @SubscribeEvent
        public void postRenderBar(RenderItemBarEvent.BattleSlots slots) {
            log.info("Battle slots " + slots.isMainHand);
        }

        @SubscribeEvent
        public void postRenderQuiver(RenderItemBarEvent.QuiverSlots slots) {
            log.info("Quiver slots " + slots.mainhand + "," + slots.quiver);
        }

        @SubscribeEvent
        public void postRenderShield(RenderItemBarEvent.ShieldBar bar) {
            log.info("Shield bar " + bar.shield);
        }
    }
}
