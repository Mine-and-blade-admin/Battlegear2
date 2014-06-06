package mods.battlegear2.client.utils;

import atomicstryker.dynamiclights.client.DynamicLights;
import atomicstryker.dynamiclights.client.IDynamicLightSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by GotoLink on 06/06/2014.
 * Using DynamicLight interface to give player a chance of light when holding offhand item
 */
public class DualHeldLight implements IDynamicLightSource{
    /**
     * Cached previous light value, because DynamicLight checks aren't consistent
     */
    private static DualHeldLight lightCache;
    private final EntityPlayer dual;
    private final int light;
    public DualHeldLight(EntityPlayer dual, int light){
        this.dual = dual;
        this.light = light;
    }

    @Override
    public Entity getAttachmentEntity() {
        return dual;
    }

    @Override
    public int getLightLevel() {
        return light;
    }

    @Override
    public boolean equals(Object object){
        if(object==null)
            return false;
        if(object==this)
            return true;
        if(object instanceof IDynamicLightSource)
            return ((IDynamicLightSource) object).getLightLevel() == this.light && ((IDynamicLightSource) object).getAttachmentEntity().getEntityId() == this.dual.getEntityId();
        return false;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(this.getAttachmentEntity().getEntityId()).append(this.light).toHashCode();
    }

    /**
     * Called by player tick if the offhand {@link ItemStack} changed
     * @param player the player entity concerned by the change
     * @param newLight light value defined by DynamicLight for the new {@link ItemStack}
     * @param oldLight light value defined by DynamicLight for the previous {@link ItemStack} (unused because caching is done instead)
     */
    public static void refresh(EntityPlayer player, int newLight, int oldLight){
        if(lightCache!=null){
            DynamicLights.removeLightSource(lightCache);
            lightCache = null;
        }
        if(newLight>0) {
            lightCache = new DualHeldLight(player, newLight);
            DynamicLights.addLightSource(lightCache);
        }
    }
}
