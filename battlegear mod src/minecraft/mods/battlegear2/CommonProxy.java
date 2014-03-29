package mods.battlegear2;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void registerKeyHandelers() {}

    public void registerTickHandelers(){
        FMLCommonHandler.instance().bus().register(new BattlegearTickHandeler());
        FMLCommonHandler.instance().bus().register(new BgPlayerTracker());
        MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new WeaponHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new BowHookContainerClass2());
        MinecraftForge.EVENT_BUS.register(new MobHookContainerClass());
        new MCPCFixHelper();
    }

    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

    public IIcon getSlotIcon(int index) {return null;}

    public MovingObjectPosition getMouseOver(float i, float v) { return null; }

    public void registerItemRenderers() {
    }

    public void startFlash(EntityPlayer player, float damage) {
    }

    public void sendPlaceBlockPacket(EntityPlayer entityPlayer, int x, int y, int z, int face, Vec3 par8Vec3){
    }

    public void doSpecialAction(EntityPlayer entityPlayer) {}

	public void tryUseTConstruct() {
	}

    public EntityPlayer getClientPlayer(){
        return null;
    }
}