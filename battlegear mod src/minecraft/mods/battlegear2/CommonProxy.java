package mods.battlegear2;

import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public void registerKeyHandelers() {}

    public void registerTickHandelers(){
        TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new WeaponHookContainerClass());
    }

    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

    public Icon getSlotIcon(int index) {return null;}

    public MovingObjectPosition getMouseOver(int i, float v) { return null; }

    public void registerItemRenderers() {
    }
}
