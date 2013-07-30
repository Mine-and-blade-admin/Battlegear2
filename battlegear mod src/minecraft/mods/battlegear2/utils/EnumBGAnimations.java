package mods.battlegear2.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public enum EnumBGAnimations {

    OffHandSwing {
        @Override
        public void processAnimation(Entity entity) {
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).swingOffItem();
        }
    };


    public abstract void processAnimation(Entity entity);

}
