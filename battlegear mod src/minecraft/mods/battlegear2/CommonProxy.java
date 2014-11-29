package mods.battlegear2;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void registerKeyHandelers() {}

    public void registerTickHandelers(){
        FMLCommonHandler.instance().bus().register(BattlegearTickHandeler.INSTANCE);
        FMLCommonHandler.instance().bus().register(BgPlayerTracker.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BattlemodeHookContainerClass.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WeaponHookContainerClass.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BowHookContainerClass2.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MobHookContainerClass.INSTANCE);
    }

    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {}

    public IIcon getSlotIcon(int index) {return null;}

    public MovingObjectPosition getMouseOver(float i, float v) { return null; }

    public void registerItemRenderers() {
    }

    public void startFlash(EntityPlayer player, float damage) {
    }

    public void doSpecialAction(EntityPlayer entityPlayer, ItemStack item) {}

	public void tryUseTConstruct() {
	}

    public void tryUseDynamicLight(EntityPlayer player, ItemStack stack){
    }

    public EntityPlayer getClientPlayer(){
        return null;
    }
}