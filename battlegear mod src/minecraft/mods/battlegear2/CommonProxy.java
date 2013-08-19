package mods.battlegear2;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.battlegear2.api.IShield;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import paulscode.sound.Vector3D;

import java.util.List;

public abstract class CommonProxy {

    public void registerKeyHandelers() {}

    public void registerTickHandelers(){
        TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new BattlemodeHookContainerClass());
        MinecraftForge.EVENT_BUS.register(new WeaponHookContainerClass());
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



