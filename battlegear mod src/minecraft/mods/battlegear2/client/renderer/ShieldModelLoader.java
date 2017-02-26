package mods.battlegear2.client.renderer;

import com.google.common.collect.ImmutableMap;
import mods.battlegear2.api.DefaultMesh;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Handle shield model format.
 */
public class ShieldModelLoader extends BaseModelLoader{
    private final Vector3f backTranslation, trimTranslation;
    private final static Pattern NUMBER = Pattern.compile("\\d");
    private final static String BACK_EXTENSION = ".back", TRIM_EXTENSION = ".trim";

    public ShieldModelLoader(){
        this(1/16F, 0.5F);
    }

    public ShieldModelLoader(float backWidth, float trimWidth){
        backTranslation = new Vector3f(0, 0, backWidth);
        trimTranslation = new Vector3f(0, 0, trimWidth);
    }

    @SubscribeEvent
    public void onFrame(RenderItemInFrameEvent frameEvent){
        if(frameEvent.getItem().getItem() instanceof ItemShield){
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            if (!Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(frameEvent.getItem()))
            {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(frameEvent.getItem(), ItemCameraTransforms.TransformType.GUI);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            frameEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onModelBaked(ModelBakeEvent modelBakeEvent){
        setLoader(modelBakeEvent.getModelLoader());
        for(Item shield : BattlegearConfig.shield) {
            if(shield!=null) {
                ModelResourceLocation mainLoc = DefaultMesh.INVENTORY.getModelLocation(new ItemStack(shield));
                ResourceLocation itemLoc = new ResourceLocation(mainLoc.getResourceDomain(), "item/" + mainLoc.getResourcePath());
                IModel originalModel = getModel(itemLoc);
                if(originalModel instanceof IRetexturableModel){
                    ModelBlock internalFrontModel = getInternalModel(originalModel);
                    if (internalFrontModel != null) {
                        ModelBlock front = makeItem(internalFrontModel);
                        IBakedModel baked = wrap(join((IRetexturableModel) originalModel, front), itemLoc);
                        if(baked != null)
                            modelBakeEvent.getModelRegistry().putObject(mainLoc, baked);
                    }
                }
            }
        }
        setLoader(null);
    }

    private ModelBlock join(IRetexturableModel model, ModelBlock front) {
        HashMap<String,String> temp = new HashMap<String, String>();
        temp.putAll(front.textures);
        int backIndex = -1, trimIndex = -1;
        HashMap<String,String> copy = new HashMap<String, String>();
        for(Map.Entry<String, String> entry : temp.entrySet()){
            if(entry.getKey().startsWith(LAYER)) {
                if (entry.getValue().endsWith(BACK_EXTENSION)) {
                    String[] number = NUMBER.split(entry.getKey(), 2);
                    backIndex = Integer.valueOf(entry.getKey().substring(number[0].length()));
                } else if (entry.getValue().endsWith(TRIM_EXTENSION)) {
                    String[] number = NUMBER.split(entry.getKey(), 2);
                    trimIndex = Integer.valueOf(entry.getKey().substring(number[0].length()));
                    copy.put(BASE_LAYER, entry.getValue());
                }
            }
        }
        List<BlockPart> elements = new ArrayList<BlockPart>();
        if(trimIndex!=-1) {
            String trimTexture = copy.get(BASE_LAYER);
            for(String key : temp.keySet()){
                copy.put(key, trimTexture);
            }
            ModelBlock internaltrim = getInternalModel(model.retexture(ImmutableMap.copyOf(copy)));
            if (internaltrim != null) {
                ModelBlock trim = makeItem(internaltrim);
                if(trim!=null) {
                    for (BlockPart part : trim.getElements()) {
                        part.mapFaces.remove(EnumFacing.SOUTH);
                        HashMap<EnumFacing, BlockPartFace> faces = new HashMap<EnumFacing, BlockPartFace>();
                        Iterator<Map.Entry<EnumFacing, BlockPartFace>> itrMapFace = part.mapFaces.entrySet().iterator();
                        while(itrMapFace.hasNext()){
                            Map.Entry<EnumFacing, BlockPartFace> entry = itrMapFace.next();
                            if(entry.getValue()!=null){
                                faces.put(entry.getKey(), new BlockPartFace(null, trimIndex, LAYER + trimIndex, entry.getValue().blockFaceUV));
                            }
                        }
                        part.mapFaces.putAll(faces);
                        Vector3f.sub(part.positionFrom, trimTranslation, part.positionFrom);
                        Vector3f.sub(part.positionTo, trimTranslation, part.positionTo);
                        Vector3f.sub(part.positionTo, trimTranslation, part.positionTo);
                        elements.add(part);
                    }
                }
            }
        }
        for(BlockPart part : front.getElements()){
            if(trimIndex!=-1) {
                BlockPartFace face = part.mapFaces.get(EnumFacing.NORTH);
                if(face!=null && face.tintIndex == trimIndex) {
                    continue;
                }
            }
            Vector3f.add(part.positionTo, backTranslation, part.positionTo);
            BlockPartFace face;
            if(backIndex!=-1) {
                face = part.mapFaces.get(EnumFacing.SOUTH);
                if (face!=null && (face.tintIndex != backIndex || face.texture == null || !face.texture.equals(LAYER + backIndex))) {
                    part.mapFaces.put(EnumFacing.SOUTH, new BlockPartFace(face.cullFace, backIndex, LAYER + backIndex, face.blockFaceUV));
                }
            }
            face = part.mapFaces.get(EnumFacing.NORTH);
            if(trimIndex!=-1){
                if (face!=null && (face.tintIndex != trimIndex || face.texture == null || !face.texture.equals(LAYER + trimIndex))) {
                    part.mapFaces.put(EnumFacing.NORTH, new BlockPartFace(face.cullFace, trimIndex, LAYER + trimIndex, face.blockFaceUV));
                }
            }
            if(face!=null && face.texture!=null && temp.get(face.texture).endsWith(BACK_EXTENSION)){
                part.mapFaces.put(EnumFacing.NORTH, new BlockPartFace(face.cullFace, 0, BASE_LAYER, face.blockFaceUV));
            }
            face = part.mapFaces.get(EnumFacing.SOUTH);
            if(face!=null && face.texture!=null && temp.get(face.texture).endsWith(TRIM_EXTENSION)){
                part.mapFaces.put(EnumFacing.SOUTH, new BlockPartFace(face.cullFace, 0, BASE_LAYER, face.blockFaceUV));
            }
            elements.add(part);
        }

        return new ModelBlock(null, elements, ImmutableMap.copyOf(front.textures), front.isAmbientOcclusion(), front.isGui3d(), front.getAllTransforms(), front.getOverrides());
    }
}
