package mods.battlegear2.client.renderer;

import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;

import java.lang.reflect.Field;

/**
 * Manipulate item models.
 */
public class BaseModelLoader {
    protected final static String LAYER = "layer", BASE_LAYER = "layer0";
    private ModelLoader manager;

    protected final void setLoader(ModelLoader loader){
        manager = loader;
    }

    protected final IModel getModel(ResourceLocation location){
        return manager.getModel(location);
    }

    protected final ModelBlock getInternalModel(IModel model){
        try {
            Field[] fields = model.getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.getType() == ModelBlock.class) {
                    f.setAccessible(true);
                    return (ModelBlock) f.get(model);
                }
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    protected final IFlexibleBakedModel wrap(ModelBlock model){
        try {
            return new IFlexibleBakedModel.Wrapper(bakeModel(model), Attributes.DEFAULT_BAKED_FORMAT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected final IBakedModel bakeModel(ModelBlock model) {
        return manager.bakeModel(model, ModelRotation.X0_Y0, false);
    }

    protected final ModelBlock makeItem(ModelBlock model){
        return manager.makeItemModel(model);
    }
}
