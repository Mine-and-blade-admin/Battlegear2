package mods.battlegear2;

import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;

public class CommonProxy {

    private HashMap<Integer,Long> timers;
    public void registerHandlers(){
        timers = new HashMap<Integer,Long>();
        FMLCommonHandler.instance().bus().register(BattlegearTickHandeler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BattlemodeHookContainerClass.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WeaponHookContainerClass.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BowHookContainerClass2.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MobHookContainerClass.INSTANCE);
    }

    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

    public MovingObjectPosition getMouseOver(double v) {
        return null;
    }

    public void registerItemRenderers() {
    }

    public void startFlash(EntityPlayer player, float damage) {
    }

    public void doSpecialAction(EntityPlayer entityPlayer, ItemStack item) {}

    public boolean handleAttack(EntityPlayer entityPlayer) {
        int id = entityPlayer.getEntityId();
        long time = System.currentTimeMillis();
        if(timers.containsKey(id)){
            long prev = timers.get(id);
            if(time - prev < 500L){
                return true;
            }
        }
        timers.put(id, time);
        return false;
    }

	public void tryUseTConstruct() {
	}

    public void tryUseDynamicLight(EntityPlayer player, ItemStack stack){
    }

    public EntityPlayer getClientPlayer(){
        return null;
    }

    public void scheduleTask(Runnable runnable) {
        MinecraftServer.getServer().addScheduledTask(runnable);
    }
}