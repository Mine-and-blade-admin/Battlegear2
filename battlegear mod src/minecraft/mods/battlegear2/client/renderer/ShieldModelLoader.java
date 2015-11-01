package mods.battlegear2.client.renderer;

import com.google.common.collect.ImmutableMap;
import mods.battlegear2.api.DefaultMesh;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.vecmath.Vector3f;
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
        this(1/16, 0.5F);
    }

    public ShieldModelLoader(float backWidth, float trimWidth){
        backTranslation = new Vector3f(0, 0, backWidth);
        trimTranslation = new Vector3f(0, 0, trimWidth);
    }

    @SubscribeEvent
    public void onFrame(RenderItemInFrameEvent frameEvent){
        if(frameEvent.item.getItem() instanceof ItemShield){
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            if (!Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(frameEvent.item))
            {
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemModelForEntity(frameEvent.item, null, ItemCameraTransforms.TransformType.GUI);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            frameEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onModelBaked(ModelBakeEvent modelBakeEvent){
        setLoader(modelBakeEvent.modelLoader);
        for(Item shield : BattlegearConfig.shield) {
            if(shield!=null) {
                ResourceLocation mainLoc = DefaultMesh.INVENTORY.getModelLocation(new ItemStack(shield));
                ResourceLocation itemLoc = new ResourceLocation(mainLoc.getResourceDomain(), "item/" + mainLoc.getResourcePath());
                IModel originalModel = getModel(itemLoc);
                if(originalModel instanceof IRetexturableModel){
                    ModelBlock internalFrontModel = getInternalModel(originalModel);
                    if (internalFrontModel != null) {
                        ModelBlock front = makeItem(internalFrontModel);
                        if (front != null) {
                            IFlexibleBakedModel baked = wrap(join((IRetexturableModel) originalModel, front));
                            if(baked != null)
                                modelBakeEvent.modelRegistry.putObject(mainLoc, baked);
                        }
                    }
                }
            }
        }
        setLoader(null);
    }

    @SuppressWarnings("unchecked")
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
                    for (BlockPart part : (List<BlockPart>) trim.getElements()) {
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
                        part.positionFrom.sub(trimTranslation);
                        part.positionTo.sub(trimTranslation);
                        part.positionTo.sub(trimTranslation);
                        elements.add(part);
                    }
                }
            }
        }
        for(BlockPart part : (List<BlockPart>) front.getElements()){
            if(trimIndex!=-1) {
                BlockPartFace face = (BlockPartFace) part.mapFaces.get(EnumFacing.NORTH);
                if(face!=null && face.tintIndex == trimIndex) {
                    continue;
                }
            }
            part.positionTo.add(backTranslation);
            BlockPartFace face;
            if(backIndex!=-1) {
                face = (BlockPartFace) part.mapFaces.get(EnumFacing.SOUTH);
                if (face!=null && (face.tintIndex != backIndex || face.texture == null || !face.texture.equals(LAYER + backIndex))) {
                    part.mapFaces.put(EnumFacing.SOUTH, new BlockPartFace(face.cullFace, backIndex, LAYER + backIndex, face.blockFaceUV));
                }
            }
            face = (BlockPartFace) part.mapFaces.get(EnumFacing.NORTH);
            if(trimIndex!=-1){
                if (face!=null && (face.tintIndex != trimIndex || face.texture == null || !face.texture.equals(LAYER + trimIndex))) {
                    part.mapFaces.put(EnumFacing.NORTH, new BlockPartFace(face.cullFace, trimIndex, LAYER + trimIndex, face.blockFaceUV));
                }
            }
            if(face!=null && face.texture!=null && temp.get(face.texture).endsWith(BACK_EXTENSION)){
                part.mapFaces.put(EnumFacing.NORTH, new BlockPartFace(face.cullFace, 0, BASE_LAYER, face.blockFaceUV));
            }
            face = (BlockPartFace) part.mapFaces.get(EnumFacing.SOUTH);
            if(face!=null && face.texture!=null && temp.get(face.texture).endsWith(TRIM_EXTENSION)){
                part.mapFaces.put(EnumFacing.SOUTH, new BlockPartFace(face.cullFace, 0, BASE_LAYER, face.blockFaceUV));
            }
            elements.add(part);
        }

        return new ModelBlock(null, elements, ImmutableMap.copyOf(front.textures), false, false, new ItemCameraTransforms(front.getThirdPersonTransform(), front.getFirstPersonTransform(), front.getHeadTransform(), front.getInGuiTransform()));
    }
}
