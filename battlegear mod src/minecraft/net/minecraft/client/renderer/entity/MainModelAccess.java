package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;

public class MainModelAccess {
    public static ModelBase getMainModel(RendererLivingEntity render){
        return render.mainModel;
    }
}
