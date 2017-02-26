package mods.battlegear2.client.renderer;

import mods.battlegear2.Battlegear;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.TRSRTransformation;

import java.lang.reflect.Field;
import java.util.Map;

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
            return ModelLoaderRegistry.getModel(location);
        } catch (Exception e) {
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

    protected final IBakedModel wrap(ModelBlock model, ResourceLocation modelLocation){
        try {
            return bakeModel(model);
        }catch (Throwable vanillaIssue){//Vanilla failed, try the "Forge" way
            Battlegear.logger.warn(vanillaIssue.toString());
            Battlegear.logger.warn("Encountered issue while trying to load model, trying fallback.");
            try {
                ICustomModelLoader loader = (ICustomModelLoader) Class.forName("net.minecraftforge.client.model.ModelLoader$VanillaLoader").getEnumConstants()[0];
                return defaultBake(loader.loadModel(ModelLoaderRegistry.getActualLocation(modelLocation)));
            }catch (Throwable forgeIssue){//Well everything is broken
                Battlegear.logger.warn(forgeIssue.toString());
                Battlegear.logger.warn("Encountered issue while trying model loading fallback. Last fallback: crappy vanilla model. Sorry :(");
            }
        }
        return null;
    }

    private final IBakedModel defaultBake(IModel bakeable){
        return bakeable.bake(ModelRotation.X0_Y0, Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
    }

    /*From ModelBakery bakeModel*/
    protected final IBakedModel bakeModel(ModelBlock model) {
        if (model.getElements().isEmpty())
        {
            return null;
        }
        else
        {
            TextureAtlasSprite sprite = ModelLoader.defaultTextureGetter().apply(new ResourceLocation(model.resolveTextureName("particle")));
            SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(model, model.createOverrides())).setTexture(sprite);
            for (BlockPart blockpart : model.getElements())
            {
                for (Map.Entry<EnumFacing,BlockPartFace> entry : blockpart.mapFaces.entrySet())
                {
                    BlockPartFace blockpartface = entry.getValue();
                    sprite = ModelLoader.defaultTextureGetter().apply(new ResourceLocation(model.resolveTextureName(blockpartface.texture)));
                    BakedQuad baked = manager.makeBakedQuad(blockpart, blockpartface, sprite, entry.getKey(), ModelRotation.X0_Y0, false);
                    if (blockpartface.cullFace == null || !TRSRTransformation.isInteger(ModelRotation.X0_Y0.getMatrix()))
                    {
                        simplebakedmodel$builder.addGeneralQuad(baked);
                    }
                    else
                    {
                        simplebakedmodel$builder.addFaceQuad(ModelRotation.X0_Y0.rotate(blockpartface.cullFace), baked);
                    }
                }
            }
            return simplebakedmodel$builder.makeBakedModel();
        }
    }

    protected final ModelBlock makeItem(ModelBlock model){
        return manager.makeItemModel(model);
    }
}
