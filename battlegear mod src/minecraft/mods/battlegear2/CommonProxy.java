package mods.battlegear2;


import java.util.List;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public void registerKeyHandelers() {}

    public void registerTickHandelers(){
        TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new WeaponHookContainerClass());
	    //MinecraftForge.EVENT_BUS.register(new BowHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new BowHookContainerClass2());
    }

    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

    public Icon getSlotIcon(int index) {return null;}

    public MovingObjectPosition getMouseOver(float i, float v) { return null; }

    public void registerItemRenderers() {
    }

    public void startFlash(EntityPlayer player, float damage) {
    }

    public void sendPlaceBlockPacket(EntityPlayer entityPlayer, int x, int y, int z, int face, Vec3 par8Vec3){

    }

    public void doSpecialAction(EntityPlayer entityPlayer) {}
}



