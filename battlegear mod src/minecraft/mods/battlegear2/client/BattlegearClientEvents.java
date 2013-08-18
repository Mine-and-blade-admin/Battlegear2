package mods.battlegear2.client;


import cpw.mods.fml.common.ObfuscationReflectionHelper;
import mods.battlegear2.Battlegear;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.heraldry.HeraldyPattern;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class BattlegearClientEvents {

    private BattlegearInGameGUI inGameGUI = new BattlegearInGameGUI();

    @ForgeSubscribe
    public void postRenderOverlay(RenderGameOverlayEvent.Post event){

        if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR){
            inGameGUI.renderGameOverlay(event.partialTicks);
        }
    }

    @ForgeSubscribe
    public void render3rdPersonBattlemode(RenderPlayerEvent.Specials.Post event){


        //System.out.println(event.entityPlayer.isBlockingWithShield());

        BattlegearRenderHelper.renderItemIn3rdPerson(
                event.entityPlayer,
                getModelBiped(event.renderer, 1),
                event.partialTicks
        );
        event.setResult(Event.Result.ALLOW);
    }

    @ForgeSubscribe
    public void preStitch(TextureStitchEvent.Pre event){
        if(event.map.textureType == 1){
            ClientProxy.backgroundIcon = new Icon[2];
            ClientProxy.backgroundIcon[0] = event.map.registerIcon("battlegear2:slots/mainhand");
            ClientProxy.backgroundIcon[1] = event.map.registerIcon("battlegear2:slots/offhand");

            HeraldyPattern.registerAllIcons(event.map);
        }
    }

    public static ModelBiped getModelBiped(RenderPlayer renderPlayer, int i){
        return ObfuscationReflectionHelper.getPrivateValue(RenderPlayer.class, renderPlayer, i);
    }

    @ForgeSubscribe
    public void onSoundLoad(SoundLoadEvent event){
        try
        {
            for(int i = 0; i < 10; i++)
                event.manager.soundPoolSounds.addSound(String.format("%s:%s%s.wav", "battlegear2", "shield", i));

        }
        catch (Exception e)
        {
            System.err.println("Failed to register one or more sounds.");
        }
    }
}
