package mods.battlegear2;


import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import mods.battlegear2.utils.EnumBGAnimations;
import mods.battlegear2.utils.Release;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
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


        MinecraftForge.EVENT_BUS.register(new MobHookContainerClass());

    }

    public String getVersionCheckerMessage(){
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(Battlegear.INSTANCE);
        if(Battlegear.latestRelease == null){
            return String.format("%s: %s - %s",
                    mc.getName(), "Version Check Failed",
                    "Could not contact server or invalid response");
        }else{
            String[] version_split = mc.getVersion().split("\\.");
            int[] version = new int[version_split.length];
            try{
                for(int i = 0; i < version.length; i++){
                    version[i] = Integer.parseInt(version_split[i]);
                }
                Release thisVersion = new Release(Release.EnumReleaseType.Normal, null, version, null);

                if(thisVersion.compareTo(Battlegear.latestRelease) < 0){

                    if(Battlegear.latestRelease.url != null){
                        return String.format("%s: %s (%s)",
                                mc.getName(),"New version found", Battlegear.latestRelease.getVersionString());
                    }else{
                        return String.format("%s: %s (%s) - %s %s",
                                mc.getName(),
                                "New version found", Battlegear.latestRelease.getVersionString(),
                                 "download url:",Battlegear.latestRelease.url);
                    }
                }else{


                    return String.format("%s: %s - %s %s",
                            mc.getName(),
                            "Version Up to Date",
                            "You are running the latest version of",mc.getName());

                }

            }catch (NumberFormatException e){

                return String.format("%s: %s - %s",
                          mc.getName(),
                         "Version Check Failed",
                        "Could not determine running version");
            }

        }
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



