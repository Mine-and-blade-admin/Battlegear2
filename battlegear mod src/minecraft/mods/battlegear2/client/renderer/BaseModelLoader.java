package mods.battlegear2.client.renderer;

import com.google.common.base.Function;
import mods.battlegear2.Battlegear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.ModelBlockAnimation;

import java.io.IOException;
import java.lang.reflect.Constructor;
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
        try {
            return manager.getModel(location);
        } catch (IOException e) {
            return null;
        }
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected final IFlexibleBakedModel wrap(ModelBlock model, ResourceLocation modelLocation){
        try {
            return new IFlexibleBakedModel.Wrapper(bakeModel(model), Attributes.DEFAULT_BAKED_FORMAT);
        }catch (Throwable vanillaIssue){//Vanilla failed, try the "Forge" way
            Battlegear.logger.warn(vanillaIssue.toString());
            Battlegear.logger.warn("Encountered issue while trying to load model, trying fallback.");
            try {
                Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                    @Override
                    public TextureAtlasSprite apply(ResourceLocation location) {
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    }
                };
                Constructor ctor = Class.forName("net.minecraftforge.client.model.ModelLoader$VanillaModelWrapper").getDeclaredConstructor(ModelLoader.class, ResourceLocation.class, ModelBlock.class, ModelBlockAnimation.class);
                ctor.setAccessible(true);
                String modelPath = modelLocation.getResourcePath();
                if(modelPath.startsWith("models/"))
                {
                    modelPath = modelPath.substring("models/".length());
                }
                ResourceLocation armatureLocation = new ResourceLocation(modelLocation.getResourceDomain(), "armatures/" + modelPath + ".json");
                Object bakeable = ctor.newInstance(manager, null, model, Animation.INSTANCE.loadVanillaAnimation(armatureLocation));
                ctor.setAccessible(false);
                return ((IModel)bakeable).bake(ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
            }catch (Throwable forgeIssue){//Well everything is broken
                Battlegear.logger.warn(forgeIssue.toString());
                Battlegear.logger.warn("Encountered issue while trying model loading fallback. Last fallback: crappy vanilla model. Sorry :(");
            }
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
